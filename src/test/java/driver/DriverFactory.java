package driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import org.apache.commons.codec.binary.Base64;

public class DriverFactory {
    public static RemoteWebDriver getDriver() {
        boolean startTunnel = System.getenv("START_TUNNEL").toString().equals("true");
        
        if (startTunnel) {
            try {
                secureTunnel();    
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        DesiredCapabilities caps = new DesiredCapabilities();
        URL hubUrl = null;
        try {
            hubUrl = new URL("http://" + System.getenv("USERNAME").replace("@", "%40") + ":" + System.getenv("AUTHKEY") +"@hub.crossbrowsertesting.com:80/wd/hub");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        caps.setCapability("name", "Login Form Example");
        caps.setCapability("build", "1.0");
        caps.setCapability("browserName", System.getenv("BROWSER_NAME"));
        caps.setCapability("platform", System.getenv("PLATFORM"));
        caps.setCapability("screenResolution", System.getenv("SCREEN_RESOLUTION"));
        caps.setCapability("record_video", "true");
        
        RemoteWebDriver driver = new RemoteWebDriver(hubUrl, caps);
        return driver;
    }

    private static void secureTunnel() throws IOException, InterruptedException {
        if (isTunnelActive()) {
            System.out.println("Currently a tunnel active, carrying on..");
        } else {
            System.out.println("Tunnel not active, starting tunnel");
            String runString = System.getProperty("user.dir") + "/SecureTunnel/cbt-tunnels --username " 
                             + System.getenv("USERNAME") + " --authkey " + System.getenv("AUTHKEY") 
                             + " --verbose > log.text";
            Process p = Runtime.getRuntime().exec(runString);
            while (!isTunnelActive()) {
                System.out.println("Waiting for tunnel");
                Thread.sleep(10000);
            }
            System.out.println("Tunnel started successfully");
        }
    }

    private static boolean isTunnelActive() {
        String JSON_RESPONSE;
        boolean tunnelActive = false;
        try {
            JSON_RESPONSE = checkTunnels();
            JSONObject jo = new JSONObject(JSON_RESPONSE);
            JSONArray ja = jo.getJSONArray("tunnels");
            JSONObject tunnel = ja.getJSONObject(0);
            tunnelActive = tunnel.get("active").toString().equals("true");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return tunnelActive;
    }

    private static String checkTunnels() throws IOException{
        String requestString = "https://crossbrowsertesting.com/api/v3/tunnels";
        URL url = new URL(requestString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        String userpassEncoding = Base64.encodeBase64String((System.getenv("USERNAME") + ":" + System.getenv("AUTHKEY")).getBytes());
        conn.setRequestProperty("Authorization", "Basic " + userpassEncoding);
    
        if (conn.getResponseCode() != 200) {
            throw new IOException("EXCEPTION " + conn.getResponseMessage());
        }
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb.toString();
    }
}
