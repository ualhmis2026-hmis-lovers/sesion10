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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Dimension;
import java.util.*;

public class ListadoUsuariosCorrectoTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  private JavascriptExecutor js;

  @Before
  public void setUp() {
    // Configuración centralizada para Jenkins (0: firefox, 1: chrome)
    int browser = 0; 
    boolean headless = true; 

    switch (browser) {
      case 0: 
        org.openqa.selenium.firefox.FirefoxOptions firefoxOptions = new org.openqa.selenium.firefox.FirefoxOptions();
        if (headless) firefoxOptions.addArguments("--headless");
        firefoxOptions.addArguments("-width", "1920");
        firefoxOptions.addArguments("-height", "1080");
        driver = new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions);
        break;
      case 1: 
        org.openqa.selenium.chrome.ChromeOptions chromeOptions = new org.openqa.selenium.chrome.ChromeOptions();
        if (headless) chromeOptions.addArguments("--headless=new");
        chromeOptions.addArguments("window-size=1920,1080");
        driver = new org.openqa.selenium.chrome.ChromeDriver(chromeOptions);
        break;
    }
    
    driver.manage().window().setSize(new Dimension(1920, 1080));
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
  public void listadoUsuariosCorrecto() {
    // Definimos un tiempo de espera explícito máximo de 15 segundos para la renderización de datos
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));

    // 1 | open | Carga de la URL inicial
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    
    // 2 | setWindowSize | Establecemos resolución limpia Full HD
    driver.manage().window().setSize(new Dimension(1920, 1080));
    
    // 3 | click | Espera controlada a que el botón del panel de administración sea interactuable
    WebElement btnAdminHeader = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-admin-header")));
    btnAdminHeader.click();
    
    // 4 | mouseOver | Espera a que la tarjeta de administración cargue antes de simular el hover
    try {
      WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".admin-card:nth-child(1) .btn-admin-add")));
      Actions builder = new Actions(driver);
      builder.moveToElement(element).perform();
    } catch (Exception e) {
      // Evita que el test rompa si el comportamiento visual del hover difiere en modo sin interfaz (headless)
    }
    
    // 5 & 6 | click & assertText | Sincroniza la visualización de la primera fila antes de validar el texto
    WebElement txtAdminUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".admin-card:nth-child(1) tr:nth-child(1) > .font-bold")));
    
    // Hacemos el clic que grabó tu Selenium IDE
    txtAdminUser.click();
    
    // Validación de seguridad: Compara de manera exacta que el primer usuario listado sea "admin"
    assertThat(txtAdminUser.getText(), is("admin"));
  }
}