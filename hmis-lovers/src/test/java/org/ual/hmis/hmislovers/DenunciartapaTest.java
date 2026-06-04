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

public class DenunciartapaTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;

  @Before
  public void setUp() {
    int browser = 0; // 0: firefox, 1: chrome
    boolean headless = true; // Forzado a true para evitar fallos de pantalla en Jenkins

    switch (browser) {
      case 0:  // Firefox
        org.openqa.selenium.firefox.FirefoxOptions firefoxOptions = new org.openqa.selenium.firefox.FirefoxOptions();
        if (headless) {
          firefoxOptions.addArguments("--headless");
        }
        driver = new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions);
        break;

      case 1: // Chrome
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
  public void denunciartapa() {
    String sufijoAleatorio = UUID.randomUUID().toString().substring(0, 6);
    String nombreBarDenuncia = "bar denuncia " + sufijoAleatorio;
    
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    driver.manage().window().maximize();
    
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));
    WebDriverWait waitLargo = new WebDriverWait(driver, java.time.Duration.ofSeconds(30));
    
    // Login
    WebElement inputUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username")));
    inputUser.click();
    inputUser.sendKeys("admin");
    
    driver.findElement(By.id("login-password")).sendKeys("1234");
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();
    
    // Crear bar
    WebElement btnAddBar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-bar")));
    btnAddBar.click();
    
    WebElement inputBarName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-name")));
    inputBarName.click();
    inputBarName.sendKeys(nombreBarDenuncia);
    
    // CORRECCIÓN: Foco/click explícito en la dirección antes de escribir
    WebElement inputBarDir = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-dir")));
    inputBarDir.click();
    inputBarDir.sendKeys("direccion denuncia 123");
    
    driver.findElement(By.cssSelector(".btn-submit-bar")).click();
    
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("new-bar-name")));
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".btn-submit-bar")));
    
    // Seleccionar bar
    WebElement barCard = waitLargo.until(
        ExpectedConditions.elementToBeClickable(By.xpath("//h3[contains(., '" + nombreBarDenuncia + "')]"))
    );
    barCard.click();
    
    // Añadir tapa
    {
      WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btn-add-item")));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-item"))).click();
    
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".menu-item-name-input"))).sendKeys("tapa a denunciar");
    driver.findElement(By.cssSelector(".menu-item-price-input")).sendKeys("2");
    
    // CORRECCIÓN: Esperas explícitas para botones del modal de creación de tapa
    WebElement btnNeg = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".neg")));
    btnNeg.click();
    
    WebElement btnSubmitReview = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-submit-review")));
    btnSubmitReview.click();
    
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".toast-message"))).click();
    
    // Seleccionar tapa de la carta
    WebElement cartaItem = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".carta-item-row")));
    cartaItem.click();
    
    // Denunciar
    WebElement reportButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".report")));
    reportButton.click();
    
    WebElement textarea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".custom-textarea")));
    textarea.click();
    textarea.sendKeys("no estaba bueno");
    
    WebElement submitReportButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".neg:nth-child(1)")));
    submitReportButton.click();
    
    WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-message")));
    toastMessage.click();
  }
}