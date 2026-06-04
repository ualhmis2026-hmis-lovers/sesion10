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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Dimension;
import java.util.*;

public class ModificarUsuarioCorrectamenteTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;

  @Before
  public void setUp() {
    int browser = 0; // 0: firefox, 1: chrome
    boolean headless = true; 

    switch (browser) {
      case 0: // FIREFOX BLINDADO
        org.openqa.selenium.firefox.FirefoxOptions firefoxOptions = new org.openqa.selenium.firefox.FirefoxOptions();
        if (headless) {
          firefoxOptions.addArguments("--headless");
        }
        // Sintaxis correcta para forzar tamaño de ventana en Firefox Headless
        firefoxOptions.addArguments("-window-size", "1920,1080");
        driver = new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions);
        break;
      case 1: // CHROME
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
    
    // Forzado explícito mediante la API de ventanas de Selenium (Aplica a ambos navegadores)
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
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    driver.manage().window().maximize();
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));

    // 1. Login previo como Administrador
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username"))).sendKeys("admin");
    driver.findElement(By.id("login-password")).sendKeys("1234");
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();

    // 2. Navegar a la sección de usuarios (Garantizado con ventana de escritorio 1920x1080)
    WebElement btnUsuarios = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-users-management, [href*='user']")));
    btnUsuarios.click();

    // 3. Seleccionar el usuario a modificar
    WebElement userEditBtn = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//td[contains(text(), 'admin')]/following-sibling::td//button[contains(@class, 'edit')]")
    ));
    userEditBtn.click();

    // 4. Modificar datos del formulario
    WebElement inputPassword = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-user-password")));
    inputPassword.click();
    inputPassword.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
    inputPassword.sendKeys("1234");

    // 5. Guardar cambios
    driver.findElement(By.cssSelector(".btn-submit-edit-user")).click();

    // Esperar cierre asíncrono de la interfaz de edición
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("edit-user-password")));

    // 6. Validación final
    WebElement txtUserChecked = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'admin')]")));
    assertTrue(txtUserChecked.isDisplayed());
  }
}