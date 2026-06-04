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
import java.util.*;

public class ListadoUsuariosCorrectoTest {
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
  public void listadoUsuariosCorrecto() {
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    driver.manage().window().maximize();
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));

    // 1. Loguearse para tener permisos de ver la lista de usuarios
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username"))).sendKeys("admin");
    driver.findElement(By.id("login-password")).sendKeys("1234");
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();

    // 2. Acceder al módulo de listado de usuarios
    WebElement btnUsuarios = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-users-management, [href*='user']")));
    btnUsuarios.click();

    // CORRECCIÓN CRÍTICA DE LA LÍNEA 99:
    // Evitamos buscar por índice de fila genérico (:nth-child) para que la inserción de bares (como 'Casa Puga') no desordene el test.
    // Buscamos directamente la celda o elemento de texto que contiene el nombre de usuario de manera explícita.
    WebElement adminUserCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//td[contains(text(), 'admin')] | //*[contains(@class, 'username') and contains(text(), 'admin')]")
    ));

    // Validamos de forma segura que el texto del elemento encontrado contiene el valor esperado "admin"
    assertThat(adminUserCell.getText(), is("admin"));
  }
}