package org.ual.hmis.hmislovers;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
    // Selector de navegador uniforme para toda la suite de pruebas
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

    // Sincronización base implícita
    driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(5));
    
    if (!headless) {
      driver.manage().window().maximize();
    }

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
    // 1. Abrir la página de Login
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/login");
    driver.manage().window().maximize();
    
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));
    
    // 2. Realizar Login previo
    WebElement inputUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username")));
    inputUser.click();
    inputUser.sendKeys("admin");
    
    driver.findElement(By.id("login-password")).sendKeys("1234");
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();
    
    // 3. Esperar a que cargue la lista de bares y clicar en el primer bar disponible
    WebElement barCard = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".bar-card-body")));
    barCard.click();
    
    // 4. Esperar a que cargue la carta y hacer clic en la primera tapa disponible
    WebElement cartaItem = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".carta-item-row")));
    cartaItem.click();
    
    // 5. Clicar en el botón de denunciar (report)
    WebElement reportButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".report")));
    reportButton.click();
    
    // 6. Escribir el motivo de la denuncia
    WebElement textarea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".custom-textarea")));
    textarea.click();
    textarea.sendKeys("no estaba bueno");
    
    // 7. Enviar la denuncia (primer botón de confirmación en la modal)
    WebElement submitReportButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".neg:nth-child(1)")));
    submitReportButton.click();
    
    // 8. Esperar a que aparezca el mensaje de confirmación flotante (toast) y cerrarlo
    WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-message")));
    toastMessage.click();
  }
}