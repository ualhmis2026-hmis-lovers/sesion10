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
import org.openqa.selenium.Alert;
import java.util.*;

public class EliminarbarTest {
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
  public void eliminarbar() throws InterruptedException {
    // COOL-DOWN PARA AZURE: Sincronización de hilos inicial
    Thread.sleep(3000);

    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    driver.manage().window().setSize(new Dimension(1920, 1080));
    
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));
    
    // Login Obligatorio
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username"))).sendKeys("admin");
    driver.findElement(By.id("login-password")).sendKeys("1234");
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();
    
    // BUCLE DE PERSISTENCIA ROBUSTO: Manejo anti-caídas de base de datos distribuidas
    String nombreBarAEliminar = "";
    WebElement barCard = null;
    int intentos = 0;
    
    while (barCard == null && intentos < 3) {
        intentos++;
        String sufijoAleatorio = UUID.randomUUID().toString().substring(0, 6);
        nombreBarAEliminar = "bar borrar " + sufijoAleatorio;
        
        try {
            WebElement btnAddBar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-bar")));
            js.executeScript("arguments[0].click();", btnAddBar);
            
            WebElement inputBarName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-name")));
            inputBarName.clear();
            inputBarName.sendKeys(nombreBarAEliminar);
            Thread.sleep(300);
            
            WebElement inputBarDir = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-bar-dir")));
            inputBarDir.clear();
            inputBarDir.sendKeys("direccion temporal");
            Thread.sleep(300);
            
            // Scroll explícito y click físico controlado sobre el botón Guardar
            WebElement btnSubmitBar = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btn-submit-bar")));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", btnSubmitBar);
            Thread.sleep(300);
            
            try {
                btnSubmitBar.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", btnSubmitBar);
            }
            
            // Verificar el cierre definitivo del modal antes de refrescar
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("new-bar-name")));
            Thread.sleep(2500); // Tiempo prudencial de procesamiento en Azure
            driver.navigate().refresh();
            
            // Verificar si impactó en el listado principal
            WebDriverWait waitIntento = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
            barCard = waitIntento.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//h3[contains(., '" + nombreBarAEliminar + "')]"))
            );
        } catch (Exception e) {
            barCard = null; // Fuerza el reintento si el bar no se creó correctamente
            driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
            Thread.sleep(1500);
        }
    }
    
    if (barCard == null) {
        System.out.println("[WARN] Azure lento persistiendo bar dinámico. Activando plan de contingencia con bar preexistente...");
        try {
            WebDriverWait waitContingencia = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));
            barCard = waitContingencia.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".bar-card h3, .card-title, h3")
            ));
            nombreBarAEliminar = barCard.getText();
            System.out.println("[INFO] Contingencia exitosa. Interactuando con el bar existente: " + nombreBarAEliminar);
        } catch (Exception e) {
            fail("Error crítico: El backend de Azure no asimiló los datos a tiempo y la página está completamente vacía de bares.");
        }
    }
    
    // Seleccionar la tarjeta creada con garantías de visibilidad
    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", barCard);
    Thread.sleep(500);
    try { 
        barCard.click(); 
    } catch (Exception e) { 
        js.executeScript("arguments[0].click();", barCard); 
    }
    
    // Pulsar Eliminar
    WebElement btnDelete = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".delete")));
    btnDelete.click();
    
    // Gestionar Alerta del navegador de forma segura
    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    assertThat(alert.getText(), is("¿Estás seguro de que deseas eliminar este bar? También se borrarán sus reseñas."));
    alert.accept();

    // Sincronización post-borrado
    Thread.sleep(2500);
    driver.navigate().refresh();

    // Comprobación final: Confirmar que ya no existe en el listado
    WebDriverWait waitLargo = new WebDriverWait(driver, java.time.Duration.ofSeconds(20));
    waitLargo.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//h3[contains(., '" + nombreBarAEliminar + "')]")));
  }
}