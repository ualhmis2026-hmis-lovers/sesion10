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

    driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(4));
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
  public void denunciartapa() throws InterruptedException {
    Thread.sleep(3000);

    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    driver.manage().window().setSize(new Dimension(1920, 1080));
    
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));
    
    // Login
    WebElement inputUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username")));
    inputUser.click();
    inputUser.sendKeys("admin");
    driver.findElement(By.id("login-password")).sendKeys("1234");
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();
    
    String nombreBarDenuncia = "";
    WebElement barCard = null;
    int intentos = 0;
    
    while (barCard == null && intentos < 3) {
        intentos++;
        String sufijoAleatorio = UUID.randomUUID().toString().substring(0, 6);
        nombreBarDenuncia = "bar denuncia " + sufijoAleatorio;
        
        try {
            WebElement btnAddBar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-bar")));
            js.executeScript("arguments[0].click();", btnAddBar);
            
            // Input Nombre
            WebElement inputBarName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-name")));
            inputBarName.clear();
            inputBarName.sendKeys(nombreBarDenuncia);
            // Sincronización forzada del DOM/Framework para el campo de nombre
            js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", inputBarName);
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", inputBarName);
            Thread.sleep(400);
            
            // Input Dirección
            WebElement inputBarDir = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-dir")));
            inputBarDir.clear();
            inputBarDir.sendKeys("direccion denuncia 123");
            // Sincronización forzada del DOM/Framework para el campo de dirección
            js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", inputBarDir);
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", inputBarDir);
            Thread.sleep(400);
            
            // Asegurar visibilidad del botón de envío
            WebElement btnSubmitBar = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btn-submit-bar")));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", btnSubmitBar);
            Thread.sleep(500);
            
            // Intentar click nativo, si falla (por estar semioculto o bloqueado temporalmente), forzar por JS
            try {
                btnSubmitBar.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", btnSubmitBar);
            }
            
            // Esperar que el formulario desaparezca
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("new-bar-name")));
            Thread.sleep(3000); // Tiempo óptimo para el reflejo de persistencia en Azure DB
            driver.navigate().refresh();
            
            WebDriverWait waitIntento = new WebDriverWait(driver, java.time.Duration.ofSeconds(12));
            barCard = waitIntento.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//h3[contains(., '" + nombreBarDenuncia + "')]"))
            );
        } catch (Exception e) {
            barCard = null;
            driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
            Thread.sleep(2000);
        }
    }
    
    if (barCard == null) {
        fail("El backend de Azure no asimiló la persistencia del bar tras 3 intentos.");
    }
    
    // Interactuar con el bar localizado
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", barCard);
    Thread.sleep(500);
    try { barCard.click(); } catch (Exception e) { js.executeScript("arguments[0].click();", barCard); }
    
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
    
    @SuppressWarnings("deprecation")
    WebElement submitReportButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".neg:nth-child(1)")));
    submitReportButton.click();
    
    WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-message")));
    toastMessage.click();
  }
}