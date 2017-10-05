import com.thoughtworks.gauge.Gauge;
import com.thoughtworks.gauge.Step;
import driver.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.Assert.assertEquals;

public class StepImplementation {
  private String username;
  private String password;
  private String login_url;
  @Step("a valid username is <usernameString>")
  public void setUsername(String usernameString) {
      username = usernameString;
  }

  @Step("a valid password is <passwordString>")
  public void setPassword(String passwordString) {
      password = passwordString;
      setUrl();
  }

  private void setUrl() {
    login_url = System.getenv("LOGIN_URL"); 
  }

  @Step("You shouldn't proceed with invalid credentials")
  public void verifyInvalidCreds() {
      Driver.webDriver.get(login_url);
      Driver.webDriver.findElementByName("username").sendKeys("test@crossbrowsertesting.com");
      Driver.webDriver.findElementByName("password").sendKeys("test123");

      Driver.webDriver.findElementByCssSelector("div.form-actions > button").click();

      WebDriverWait wait = new WebDriverWait(Driver.webDriver, 10);
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div > div > div > div.ng-binding.ng-scope.alert.alert-danger")));
      String failedLoginMessage = Driver.webDriver.findElementByCssSelector("body > div > div > div > div.ng-binding.ng-scope.alert.alert-danger").getText();
      System.out.println(failedLoginMessage);
      assertEquals("Username or password is incorrect", failedLoginMessage);
  }

  @Step("You should proceed with valid credentials")
  public void verifyValidCreds() {
      Driver.webDriver.get(login_url);
      Driver.webDriver.findElementByName("username").sendKeys(username);
      Driver.webDriver.findElementByName("password").sendKeys(password);

      Driver.webDriver.findElementByCssSelector("div.form-actions > button").click();
            
      WebDriverWait wait = new WebDriverWait(Driver.webDriver, 10);
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"logged-in-message\"]/h2")));
      
      String welcomeMessage = Driver.webDriver.findElementByXPath("//*[@id=\"logged-in-message\"]/h2").getText();
      assertEquals("Welcome tester@crossbrowsertesting.com", welcomeMessage);
      
  }    
}
