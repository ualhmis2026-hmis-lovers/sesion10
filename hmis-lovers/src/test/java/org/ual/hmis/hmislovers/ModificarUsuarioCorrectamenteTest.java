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
import org.openqa.selenium.Keys;
import org.openqa.selenium.Dimension;
import java.util.*;

public class ModificarUsuarioCorrectamenteTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  private JavascriptExecutor js;

  @Before
  public void setUp() {
    // Configuración para Jenkins (0: firefox, 1: chrome)
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
  public void modificarUsuarioCorrectamente() {
    // Definimos un tiempo de espera explícito máximo de 15 segundos para elementos lentos
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));

    // 1 | open | URL de la aplicación
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    
    // 2 | setWindowSize | Forzamos resolución Full HD estable
    driver.manage().window().setSize(new Dimension(1920, 1080));
    
    // 3 | click | Espera y hace clic de forma segura en el botón admin del header
    WebElement btnAdminHeader = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-admin-header > .material-icons")));
    btnAdminHeader.click();
    
    // 4 & 5 | mouseOver/mouseOut | Mantenemos las acciones de hover del IDE de forma segura
    try {
      Actions builder = new Actions(driver);
      builder.moveToElement(btnAdminHeader).perform();
      WebElement bodyElement = driver.findElement(By.tagName("body"));
      builder.moveToElement(bodyElement, 0, 0).perform();
    } catch (Exception e) {
      // Ignorar fallos visuales de hover menores si estamos en headless
    }
    
    // 6 | click | Espera a que la tabla cargue y hace clic en el botón 'edit' de la fila 5
    WebElement btnEditRow = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("tr:nth-child(5) .edit")));
    btnEditRow.click();
    
    // 7 & 8 | click & type | Espera al input de texto, lo limpia por completo y escribe el nuevo valor
    WebElement inputUsername = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("admin-edit-username")));
    inputUsername.click();
    
    // TRUCO SEGURO: Borramos el contenido previo seleccionándolo todo antes de escribir (evita concatenaciones)
    inputUsername.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
    inputUsername.sendKeys("pepejuan370123");
    
    // 9 | click | Envía el formulario haciendo clic en guardar
    WebElement btnSubmitBar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-submit-bar")));
    btnSubmitBar.click();
    
    // 10 & 11 | click & assertText | Espera a que el DOM se actualice y confirma el cambio de texto
    WebElement txtResult = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".admin-card:nth-child(1) tr:nth-child(5) > .font-bold")));
    
    // Clic opcional que hacía tu script
    txtResult.click(); 
    
    // Verificación final del texto esperado
    assertThat(txtResult.getText(), is("pepejuan370123"));
  }
}