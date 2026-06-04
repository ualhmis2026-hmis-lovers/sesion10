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
import org.openqa.selenium.Keys;
import java.util.*;

public class ModificarbarTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;

  @Before
  public void setUp() {
    int browser = 0; // 0: firefox, 1: chrome
    boolean headless = true;

    switch (browser) {
      case 0:
        org.openqa.selenium.firefox.FirefoxOptions firefoxOptions = new org.openqa.selenium.firefox.FirefoxOptions();
        if (headless) {
          firefoxOptions.addArguments("--headless");
        }
        firefoxOptions.addArguments("--width=1920");
        firefoxOptions.addArguments("--height=1080");
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
  public void modificarbar() throws InterruptedException {
    Thread.sleep(3000);

    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    driver.manage().window().setSize(new Dimension(1920, 1080));
    
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));
    
    // Login
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username"))).sendKeys("admin");
    driver.findElement(By.id("login-password")).sendKeys("1234");
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();
    
    String nombreBarOriginal = "";
    WebElement barCard = null;
    int intentos = 0;
    
    while (barCard == null && intentos < 3) {
        intentos++;
        String sufijoAleatorio = UUID.randomUUID().toString().substring(0, 6);
        nombreBarOriginal = "bar original " + sufijoAleatorio;
        
        try {
            WebElement btnAddBar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-bar")));
            js.executeScript("arguments[0].click();", btnAddBar);
            
            WebElement inputBarNameNuevo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-name")));
            inputBarNameNuevo.clear();
            inputBarNameNuevo.sendKeys(nombreBarOriginal);
            Thread.sleep(300);
            
            WebElement inputBarDirNuevo = driver.findElement(By.id("new-bar-dir"));
            inputBarDirNuevo.clear();
            inputBarDirNuevo.sendKeys("direccion original 123");
            Thread.sleep(300);
            
            // CORRECCIÓN: Scroll explícito y click nativo sobre el botón Guardar
            WebElement btnSubmitBar = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btn-submit-bar")));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", btnSubmitBar);
            Thread.sleep(300);
            
            try {
                btnSubmitBar.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", btnSubmitBar);
            }
            
            // Esperar que el modal desaparezca confirmando que se procesó la petición
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("new-bar-name")));
            Thread.sleep(3000); // Tiempo de persistencia en Azure
            driver.navigate().refresh();
            
            WebDriverWait waitIntento = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
            barCard = waitIntento.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//h3[contains(., '" + nombreBarOriginal + "')]"))
            );
        } catch (Exception e) {
            barCard = null;
            driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
            Thread.sleep(1500);
        }
    }
    
    if (barCard == null) {
        fail("El backend de Azure no procesó la inserción del bar tras 3 reintentos consecutivos.");
    }
    
    // Seleccionar y realizar la edición
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", barCard);
    Thread.sleep(500);
    try { barCard.click(); } catch (Exception e) { js.executeScript("arguments[0].click();", barCard); }
    
    WebElement btnEdit = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".edit")));
    btnEdit.click();
    
    WebElement inputBarDir = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-dir")));
    inputBarDir.click();
    inputBarDir.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE); 
    inputBarDir.sendKeys("alli o no");
    Thread.sleep(200);
    
    String nombreBarModificado = "casa angel si " + UUID.randomUUID().toString().substring(0, 6);
    WebElement inputBarName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-name")));
    inputBarName.click();
    inputBarName.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE); 
    inputBarName.sendKeys(nombreBarModificado);
    Thread.sleep(300);
    
    // Guardar cambios modificados con el mismo proceso robusto
    WebElement btnSubmit = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btn-submit-bar")));
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", btnSubmit);
    Thread.sleep(300);
    try {
        btnSubmit.click();
    } catch (Exception e) {
        js.executeScript("arguments[0].click();", btnSubmit);
    }
    
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".btn-submit-bar")));
    
    WebElement btnCloseModal = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-close-modal > .material-icons")));
    btnCloseModal.click();
    
    Thread.sleep(3000);
    driver.navigate().refresh();
    
    WebElement barCardAgain = wait.until(
        ExpectedConditions.presenceOfElementLocated(By.xpath("//h3[contains(., '" + nombreBarModificado + "')]"))
    );
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", barCardAgain);
    Thread.sleep(500);
    try { barCardAgain.click(); } catch (Exception e) { js.executeScript("arguments[0].click();", barCardAgain); }
    
    WebElement txtTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div:nth-child(2) > h2")));
    assertTrue(txtTitle.isDisplayed());
    assertThat(txtTitle.getText(), is(nombreBarModificado));
  }
}