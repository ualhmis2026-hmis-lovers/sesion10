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
public class DenunciartapaTest {
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
  public void denunciartapa() {
    // Test name: Denunciar-tapa
    // Step # | name | target | value
    // 1 | open | /login | 
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/login");
    // 2 | setWindowSize | 1229x874 | 
    driver.manage().window().setSize(new Dimension(1229, 874));
    // 3 | click | id=login-username | 
    driver.findElement(By.id("login-username")).click();
    // 4 | type | id=login-username | admin
    driver.findElement(By.id("login-username")).sendKeys("admin");
    // 5 | type | id=login-password | 1234
    driver.findElement(By.id("login-password")).sendKeys("1234");
    // 6 | click | css=.btn-auth-submit | 
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();
    // 7 | click | css=.bar-card:nth-child(2) > .bar-card-body | 
    driver.findElement(By.cssSelector(".bar-card:nth-child(2) > .bar-card-body")).click();
    // 8 | click | css=.carta-item-row:nth-child(2) | 
    driver.findElement(By.cssSelector(".carta-item-row:nth-child(2)")).click();
    // 9 | click | css=.report | 
    driver.findElement(By.cssSelector(".report")).click();
    // 10 | click | css=.custom-textarea | 
    driver.findElement(By.cssSelector(".custom-textarea")).click();
    // 11 | type | css=.custom-textarea | no estaba bueno
    driver.findElement(By.cssSelector(".custom-textarea")).sendKeys("no estaba bueno");
    // 12 | click | css=.neg:nth-child(1) | 
    driver.findElement(By.cssSelector(".neg:nth-child(1)")).click();
    // 13 | click | css=.toast-message | 
    driver.findElement(By.cssSelector(".toast-message")).click();
  }
}
