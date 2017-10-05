package driver;       

import com.thoughtworks.gauge.AfterSpec;
import com.thoughtworks.gauge.BeforeSpec;
import com.thoughtworks.gauge.ExecutionContext;
import com.thoughtworks.gauge.Specification;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.apache.commons.codec.binary.Base64;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStreamWriter;

public class Driver {
    public static RemoteWebDriver webDriver;
    private String apiUrl = "crossbrowsertesting.com/api/v3/selenium";
    @BeforeSpec
    public void initializeDriver(){
        webDriver = DriverFactory.getDriver();
    }

    @AfterSpec
    public void closeDriver(ExecutionContext context){
        Specification currentSpecification = context.getCurrentSpecification();
        String sessionId = webDriver.getSessionId().toString();
        webDriver.quit();
        if (currentSpecification.getIsFailing()) {
            setScore("fail", sessionId);
        } else {
            setScore("pass", sessionId);
        }
    }

    public void setScore(String score, String sessionId) {
        String url = "https://" + apiUrl + "/" + sessionId;
        String payload = "{\"action\": \"set_score\", \"score\": \"" + score + "\"}";
        makeRequest("PUT", url, payload);
    }
    
    public void takeSnapshot(String sessionId) {
        if (sessionId != null) {
            String url = "https://" + apiUrl + "/" + sessionId + "/snapshots";
            String payload = "{\"selenium_test_id\": \"" + sessionId + "\"}";
            makeRequest("POST",url,payload);
        }
    }
    
    private void makeRequest(String requestMethod, String apiUrl, String payload) {
        URL url;
        String auth = "Basic " + Base64.encodeBase64String((System.getenv("USERNAME")+":" + System.getenv("AUTHKEY")).getBytes());
        
        try {
            url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", auth);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            osw.write(payload);
            osw.flush();
            osw.close();
            conn.getResponseCode();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
