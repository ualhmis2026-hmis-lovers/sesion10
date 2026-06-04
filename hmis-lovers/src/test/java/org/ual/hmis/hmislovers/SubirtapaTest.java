package org.ual.hmis.hmislovers;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import java.util.*;

public class SubirtapaTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;

  @Before
  public void setUp() {
    int browser = 0;
    boolean headless = true;

    switch (browser) {
      case 0:
        org.openqa.selenium.firefox.FirefoxOptions firefoxOptions = new org.openqa.selenium.firefox.FirefoxOptions();
        if (headless) {
          firefoxOptions.addArguments("--headless");
        }
        driver = new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions);
        break;

      case 1:
        org.openqa.selenium.chrome.ChromeOptions chromeOptions = new org.openqa.selenium.chrome.ChromeOptions();
        if (headless) {
          chromeOptions.addArguments("--headless=new");
        }
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("window-size=1920,1080");
        driver = new org.openqa.selenium.chrome.ChromeDriver(chromeOptions);
        break;

      default:
        fail("Please select a browser");
        break;
    }

    driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(5));
    js = (JavascriptExecutor) driver;
    vars = new HashMap<String, Object>();
  }

  @After
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  public void subirtapa() {
    String sufijoAleatorio = UUID.randomUUID().toString().substring(0, 6);
    String nombreBarTapa = "bar tapa " + sufijoAleatorio;

    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    driver.manage().window().setSize(new Dimension(1920, 1080));

    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));
    WebDriverWait waitLargo = new WebDriverWait(driver, java.time.Duration.ofSeconds(45));

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username"))).sendKeys("admin");
    driver.findElement(By.id("login-password")).sendKeys("1234");
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();

    WebElement btnAddBar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-bar")));
    btnAddBar.click();

    WebElement inputBarName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-name")));
    inputBarName.click();
    inputBarName.sendKeys(nombreBarTapa);

    WebElement inputBarDir = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-dir")));
    inputBarDir.click();
    inputBarDir.sendKeys("direccion tapa 123");
    
    driver.findElement(By.cssSelector(".btn-submit-bar")).click();

    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("new-bar-name")));
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".btn-submit-bar")));

    // ESTABILIZACIÓN AZURE: Pausa y refresco antes de buscar el bar recién creado
    try { 
        Thread.sleep(1500); 
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    driver.navigate().refresh();

    WebElement barCard = waitLargo.until(
        ExpectedConditions.elementToBeClickable(By.xpath("//h3[contains(., '" + nombreBarTapa + "')]"))
    );
    barCard.click();

    {
      WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btn-add-item")));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    {
      WebElement element = driver.findElement(By.tagName("body"));
      Actions builder = new Actions(driver);
      builder.moveToElement(element, 0, 0).perform();
    }
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-item"))).click();

    // Sincronización robusta en la inserción de tapa
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".menu-item-name-input"))).sendKeys("tapa nueva");
    driver.findElement(By.cssSelector(".menu-item-price-input")).sendKeys("2");
    
    WebElement btnNeg = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".neg")));
    btnNeg.click();
    
    WebElement btnSubmitReview = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-submit-review")));
    btnSubmitReview.click();
    
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".toast-message"))).click();
    
    driver.findElement(By.cssSelector(".tab-content")).click();

    assertThat(driver.findElement(By.cssSelector(".carta-section:nth-child(1) > .carta-item-row:nth-child(2) .carta-item-name")).getText(), is("tapa nueva"));
  }
}