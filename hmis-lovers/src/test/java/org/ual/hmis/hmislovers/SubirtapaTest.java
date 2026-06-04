package org.ual.hmis.hmislovers;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
public class SubirtapaTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;
  @Before
  public void setUp() {
    driver = new FirefoxDriver();
    js = (JavascriptExecutor) driver;
    vars = new HashMap<String, Object>();
  }
  @After
  public void tearDown() {
    driver.quit();
  }
  @Test
  public void subirtapa() {
    // Test name: subir-tapa
    // Step # | name | target | value
    // 1 | open | /login | 
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/login");
    // 2 | setWindowSize | 1229x866 | 
    driver.manage().window().setSize(new Dimension(1229, 866));
    // 3 | click | id=login-username | 
    driver.findElement(By.id("login-username")).click();
    // 4 | type | id=login-username | admin
    driver.findElement(By.id("login-username")).sendKeys("admin");
    // 5 | type | id=login-password | 1234
    driver.findElement(By.id("login-password")).sendKeys("1234");
    // 6 | click | css=.btn-auth-submit | 
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();
    // 7 | click | css=.bar-card:nth-child(1) > .bar-card-body | 
    driver.findElement(By.cssSelector(".bar-card:nth-child(1) > .bar-card-body")).click();
    // 8 | mouseOver | css=.btn-add-item | 
    {
      WebElement element = driver.findElement(By.cssSelector(".btn-add-item"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    // 9 | mouseOut | css=.btn-add-item | 
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    // 10 | click | css=.btn-add-item | 
    driver.findElement(By.cssSelector(".btn-add-item")).click();
    // 11 | click | css=.menu-item-name-input | 
    driver.findElement(By.cssSelector(".menu-item-name-input")).click();
    // 12 | type | css=.menu-item-name-input | tapa nueva
    driver.findElement(By.cssSelector(".menu-item-name-input")).sendKeys("tapa nueva");
    // 13 | click | css=.menu-item-price-input | 
    driver.findElement(By.cssSelector(".menu-item-price-input")).click();
    // 14 | type | css=.menu-item-price-input | 2
    driver.findElement(By.cssSelector(".menu-item-price-input")).sendKeys("2");
    // 15 | click | css=.neg | 
    driver.findElement(By.cssSelector(".neg")).click();
    // 16 | click | css=.btn-submit-review | 
    driver.findElement(By.cssSelector(".btn-submit-review")).click();
    // 17 | click | css=.toast-message | 
    driver.findElement(By.cssSelector(".toast-message")).click();
    // 18 | click | css=.tab-content | 
    driver.findElement(By.cssSelector(".tab-content")).click();
    // 19 | assertText | css=.carta-section:nth-child(1) > .carta-item-row:nth-child(2) .carta-item-name | tapa nueva
    assertThat(driver.findElement(By.cssSelector(".carta-section:nth-child(1) > .carta-item-row:nth-child(2) .carta-item-name")).getText(), is("tapa nueva"));
  }
}
