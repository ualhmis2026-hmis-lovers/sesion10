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
    // 2 | setWindowSize | 1229x867 | 
    driver.manage().window().setSize(new Dimension(1229, 867));
    // 3 | click | id=login-username | 
    driver.findElement(By.id("login-username")).click();
    // 4 | click | id=login-username | 
    driver.findElement(By.id("login-username")).click();
    // 5 | type | id=login-username | admin
    driver.findElement(By.id("login-username")).sendKeys("admin");
    // 6 | type | id=login-password | 1234
    driver.findElement(By.id("login-password")).sendKeys("1234");
    // 7 | click | css=.btn-auth-submit | 
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();
    // 8 | click | css=.btn-add-bar | 
    driver.findElement(By.cssSelector(".btn-add-bar")).click();
    // 9 | mouseOver | css=.btn-add-bar | 
    {
      WebElement element = driver.findElement(By.cssSelector(".btn-add-bar"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    // 10 | mouseOut | css=.btn-add-bar | 
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    // 11 | click | id=new-bar-name | 
    driver.findElement(By.id("new-bar-name")).click();
    // 12 | type | id=new-bar-name | bartest
    driver.findElement(By.id("new-bar-name")).sendKeys("bartest");
    // 13 | type | id=new-bar-dir | bartest
    driver.findElement(By.id("new-bar-dir")).sendKeys("bartest");
    // 14 | click | css=.btn-submit-bar | 
    driver.findElement(By.cssSelector(".btn-submit-bar")).click();
    // 15 | click | css=.toast-item | 
    driver.findElement(By.cssSelector(".toast-item")).click();
    // 16 | click | css=.toast-item | 
    driver.findElement(By.cssSelector(".toast-item")).click();
    // 17 | doubleClick | css=.toast-item | 
    {
      WebElement element = driver.findElement(By.cssSelector(".toast-item"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    // 18 | click | css=.toast-message | 
    driver.findElement(By.cssSelector(".toast-message")).click();
    // 19 | click | css=.toast-message | 
    driver.findElement(By.cssSelector(".toast-message")).click();
    // 20 | doubleClick | css=.toast-message | 
    {
      WebElement element = driver.findElement(By.cssSelector(".toast-message"));
      Actions builder = new Actions(driver);
      builder.doubleClick(element).perform();
    }
    // 21 | click | css=.search-input | 
    driver.findElement(By.cssSelector(".search-input")).click();
    // 22 | type | css=.search-input | bartest
    driver.findElement(By.cssSelector(".search-input")).sendKeys("bartest");
    // 23 | click | css=.bar-card-body | 
    driver.findElement(By.cssSelector(".bar-card-body")).click();
    // 24 | click | css=.menu-items-empty | 
    driver.findElement(By.cssSelector(".menu-items-empty")).click();
    // 25 | assertText | css=.menu-items-empty > p | Aún no hay platos en la carta. ¡Añade el primero!
    assertThat(driver.findElement(By.cssSelector(".menu-items-empty > p")).getText(), is("Aún no hay platos en la carta. ¡Añade el primero!"));
    // 26 | click | css=.menu-items-empty | 
    driver.findElement(By.cssSelector(".menu-items-empty")).click();
    // 28 | click | css=.delete | 
    driver.findElement(By.cssSelector(".delete")).click();
    // 29 | assertConfirmation | ¿Estás seguro de que deseas eliminar este bar? También se borrarán sus reseñas. | 
    assertThat(driver.switchTo().alert().getText(), is("¿Estás seguro de que deseas eliminar este bar? También se borrarán sus reseñas."));
    // 30 | webdriverChooseOkOnVisibleConfirmation |  | 
    driver.switchTo().alert().accept();
  }
}
