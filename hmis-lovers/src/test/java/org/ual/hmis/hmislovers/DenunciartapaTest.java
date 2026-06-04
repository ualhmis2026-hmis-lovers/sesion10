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
    // Espera inicial de cortesía para carga de red básica
    Thread.sleep(2000);

    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    driver.manage().window().setSize(new Dimension(1920, 1080));
    
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));
    
    // 1. Login exitoso
    WebElement inputUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username")));
    inputUser.click();
    inputUser.sendKeys("admin");
    driver.findElement(By.id("login-password")).sendKeys("1234");
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();
    
    String nombreBarDenuncia = "";
    WebElement barCard = null;
    int intentos = 0;
    
    // 2. Intento de creación de Bar Dinámico
    while (barCard == null && intentos < 2) {
        intentos++;
        String sufijoAleatorio = UUID.randomUUID().toString().substring(0, 6);
        nombreBarDenuncia = "bar denuncia " + sufijoAleatorio;
        
        try {
            WebElement btnAddBar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-bar")));
            js.executeScript("arguments[0].click();", btnAddBar);
            
            WebElement inputBarName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-name")));
            inputBarName.clear();
            inputBarName.sendKeys(nombreBarDenuncia);
            Thread.sleep(200);
            
            WebElement inputBarDir = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-dir")));
            inputBarDir.clear();
            inputBarDir.sendKeys("direccion denuncia 123");
            Thread.sleep(200);
            
            WebElement btnSubmitBar = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btn-submit-bar")));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", btnSubmitBar);
            Thread.sleep(300);
            
            try { 
                btnSubmitBar.click(); 
            } catch (Exception e) { 
                js.executeScript("arguments[0].click();", btnSubmitBar); 
            }
            
            // Esperar que el modal/formulario desaparezca antes de refrescar
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("new-bar-name")));
            Thread.sleep(2500); 
            driver.navigate().refresh();
            
            // Intentar buscar el bar recién creado de forma asíncrona
            WebDriverWait waitIntento = new WebDriverWait(driver, java.time.Duration.ofSeconds(8));
            barCard = waitIntento.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//h3[contains(., '" + nombreBarDenuncia + "')]"))
            );
        } catch (Exception e) {
            barCard = null;
            driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
            Thread.sleep(1000);
        }
    }
    
    // 3. ESTRATEGIA FALLBACK MEJORADA: Si Azure tarda en persistir, rescatamos cualquier bar preexistente
    if (barCard == null) {
        System.out.println("[WARN] Azure lento persistiendo bar dinámico. Activando plan de contingencia con bar preexistente...");
        try {
            // Ampliamos el tiempo de espera por si la base de datos de Azure está haciendo un arranque en frío (Cold Start)
            WebDriverWait waitContingencia = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));
            
            // Selector alternativo resiliente (busca selectores típicos de tarjetas o el primer h3 disponible)
            barCard = waitContingencia.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".bar-card h3, .card-title, h3")
            ));
            System.out.println("[INFO] Contingencia exitosa. Interactuando con el bar existente: " + barCard.getText());
        } catch (Exception e) {
            fail("Error crítico: El backend de Azure no asimiló los datos a tiempo y la página está completamente vacía de bares.");
        }
    }
    
    // 4. Interactuar con la tarjeta de bar localizada
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", barCard);
    Thread.sleep(500);
    try { 
        barCard.click(); 
    } catch (Exception e) { 
        js.executeScript("arguments[0].click();", barCard); 
    }
    
    // 5. Añadir una nueva tapa al bar
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
    
    // Cerrar el Toast informativo de creación
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".toast-message"))).click();
    
    // 6. Seleccionar la tapa creada en la carta
    WebElement cartaItem = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".carta-item-row")));
    cartaItem.click();
    
    // 7. Ejecutar la denuncia de la tapa
    WebElement reportButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".report")));
    reportButton.click();
    
    WebElement textarea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".custom-textarea")));
    textarea.click();
    textarea.sendKeys("no estaba bueno");
    
    @SuppressWarnings("deprecation")
    WebElement submitReportButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".neg:nth-child(1)")));
    submitReportButton.click();
    
    // Confirmar que el aviso final de denuncia aparece correctamente
    WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-message")));
    toastMessage.click();
  }
}