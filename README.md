# Getting Started with CBT and Gauge

This is an example for getting started with Gauge, Selenium, and CrossBrowserTesting's hub. Before getting started with this example, try checking out the [documentation for setting up](https://docs.getgauge.io/installing.html) gauge.  

## Installing a Selenium template

Gauge has built in templates to help you get started. In fact, we'll simply be modifying one of their basic templates to work with CBT's cloud environment, as well as make use of CBT's API and local connection feature.

    gauge --install java_maven_selenium

## Building on top of this template

### Defining Specifications

* This template includes a sample specification called "login.spec" which opens up a browser and navigates to a basic login form. 

* To add more specifications throw them in the /specs directory of the root of the project. Gauge has [plenty of documentation](https://docs.getgauge.io/longstart.html#specifications-spec) on writing your own specs. 

### Writing the implementations

This is where the java implementation of the steps would be implemented. Since this is a Selenium based project, the java implementation would invoke Selenium APIs as necessary to perform work within a browser. 

Note that every Gauge step implementation is annotated with a `Step` attribute that takes the Step text pattern as a parameter. See the example from this project below:

```
# login.spec
* a valid username is "tester@crossbrowsertesting.com"
* a valid password is "test123"

# StepImplementation.java
  @Step("a valid username is <usernameString>")
  public void setUsername(String usernameString) {
      username = usernameString;
  }

  @Step("a valid password is <passwordString>")
  public void setPassword(String passwordString) {
      password = passwordString;
      setUrl();
  }
```

You can read more about [step implementations in Java here](http://getgauge.io/documentation/user/current/test_code/java/java.html).

### Testing Different Platforms

An obvious reason to test on a cloud platform like CBT is to make use of all the different OS/Device/Browser combinations that are offered. You can select from our API names using the configuration on [our Selenium dashboard](https://app.crossbrowsertesting.com/selenium/run). To change the platform for this project, I've simplified the process to only editing some environment variables in the /env/default/user.properties file. Gauge will set these environment variables, and the example will automatically use them when creating the webDriver object. 

#### Desktop
```
caps.setCapability("browserName", "Internet Explorer");
caps.setCapability("version", "11");
caps.setCapability("platform", "Windows 10");
caps.setCapability("screenResolution", "1366x768");
```
#### Mobile
```
caps.setCapability("browserName", "Chrome");
caps.setCapability("deviceName", "Nexus 6P");
caps.setCapability("platformVersion", "7.0");
caps.setCapability("platformName", "Android");
caps.setCapability("deviceOrientation", "portrait");
```

### Starting a Local Connection

For the purposes of the example, we've setup the local connection for you. There are only two steps you need to take. First, you'll need a binary for cbt-tunnels. The latest version [can be found here](https://github.com/crossbrowsertesting/cbt-tunnel-nodejs/releases). This file needs to sit in a directory called SecureTunnel which should be in the root of the project. Next, just set the environment variable in the user.properties file:

```
START_TUNNEL = true
```

From the point onward, the local connection will be started (or at least checked for activity) everytime you execute your specs. 

### Executing your Specs

* You can execute the specification as:

```
mvn test
```
