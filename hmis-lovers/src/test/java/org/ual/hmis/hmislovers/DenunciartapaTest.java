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
    boolean headless = true;

    switch (browser) {
      case 0:  // Firefox
        org.openqa.selenium.firefox.FirefoxOptions firefoxOptions = new org.openqa.selenium.firefox.FirefoxOptions();
        if (headless) {
          firefoxOptions.addArguments("--headless");
        }
        firefoxOptions.addArguments("--width=1920");
        firefoxOptions.addArguments("--height=1080");
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
    // COOL-DOWN PARA AZURE: Evitamos saturar el servidor al inicio de la prueba
    try { Thread.sleep(4000); } catch (Exception e) {}

    String sufijoAleatorio = UUID.randomUUID().toString().substring(0, 6);
    String nombreBarDenuncia = "bar denuncia " + sufijoAleatorio;
    
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    driver.manage().window().setSize(new Dimension(1920, 1080));
    
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(20));
    WebDriverWait waitLargoAzure = new WebDriverWait(driver, java.time.Duration.ofSeconds(45));
    
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
    try { Thread.sleep(300); } catch (Exception e) {} // Tiempo para enlazar modelo (Data binding)
    
    WebElement inputBarDir = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-dir")));
    inputBarDir.click();
    inputBarDir.sendKeys("direccion denuncia 123");
    try { Thread.sleep(300); } catch (Exception e) {} // Tiempo para enlazar modelo (Data binding)
    
    // Envío del formulario forzado y robusto
    WebElement btnSubmitBar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-submit-bar")));
    try {
        btnSubmitBar.click();
    } catch (Exception e) {
        js.executeScript("arguments[0].click();", btnSubmitBar);
    }
    
    // Asegurar que el modal se cierra confirmando el guardado en el servidor antes de refrescar
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("new-bar-name")));
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".btn-submit-bar")));
    
    // ESTABILIZACIÓN AZURE: Pausa estratégica de persistencia y refresco completo
    try { 
        Thread.sleep(2000); 
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    driver.navigate().refresh();
    
    // Localizar por presencia, realizar scroll al centro de la pantalla y pulsar con contingencia JS
    WebElement barCard = waitLargoAzure.until(
        ExpectedConditions.presenceOfElementLocated(By.xpath("//h3[contains(., '" + nombreBarDenuncia + "')]"))
    );
    
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", barCard);
    try { Thread.sleep(600); } catch (Exception e) {}
    
    wait.until(ExpectedConditions.elementToBeClickable(barCard));
    try {
        barCard.click();
    } catch (Exception e) {
        js.executeScript("arguments[0].click();", barCard);
    }
    
    // Añadir tapa
    {
      WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btn-add-item")));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    }
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-item"))).click();
    
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".menu-item-name-input"))).sendKeys("tapa a denunciar");
    driver.findElement(By.cssSelector(".menu-item-price-input")).sendKeys("2");
    
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