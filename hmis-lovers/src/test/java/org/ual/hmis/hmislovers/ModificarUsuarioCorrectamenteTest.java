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
      case 0:
        org.openqa.selenium.firefox.FirefoxOptions firefoxOptions = new org.openqa.selenium.firefox.FirefoxOptions();
        if (headless) firefoxOptions.addArguments("--headless");
        driver = new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions);
        break;
      case 1:
        org.openqa.selenium.chrome.ChromeOptions chromeOptions = new org.openqa.selenium.chrome.ChromeOptions();
        if (headless) chromeOptions.addArguments("--headless=new");
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
  public void modificarUsuarioCorrectamente() {
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    driver.manage().window().maximize();
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));

    // 1. Login previo como Administrador
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username"))).sendKeys("admin");
    driver.findElement(By.id("login-password")).sendKeys("1234");
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();

    // 2. Navegar a la sección/gestión de usuarios (Ajusta el selector si difiere en tu app)
    WebElement btnUsuarios = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-users-management, [href*='user']")));
    btnUsuarios.click();

    // 3. Seleccionar el usuario a modificar (usamos un XPath para evitar colisiones por orden)
    WebElement userEditBtn = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//td[contains(text(), 'admin')]/following-sibling::td//button[contains(@class, 'edit')]")
    ));
    userEditBtn.click();

    // 4. Modificar datos del formulario
    WebElement inputPassword = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-user-password")));
    inputPassword.click();
    inputPassword.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
    inputPassword.sendKeys("1234"); // Mantenemos o actualizamos contraseña de pruebas

    // 5. Guardar cambios
    driver.findElement(By.cssSelector(".btn-submit-edit-user")).click();

    // CORRECCIÓN CRÍTICA DE LA LÍNEA 110: 
    // En lugar de esperar la invisibilidad de todos los elementos 'label' (que causaba el Timeout),
    // esperamos a que el campo del formulario de edición deje de estar visible, asegurando el cierre del modal.
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("edit-user-password")));

    // 6. Validación final: Asegurar que seguimos viendo al usuario administrador tras la mutación
    WebElement txtUserChecked = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'admin')]")));
    assertTrue(txtUserChecked.isDisplayed());
  }
}