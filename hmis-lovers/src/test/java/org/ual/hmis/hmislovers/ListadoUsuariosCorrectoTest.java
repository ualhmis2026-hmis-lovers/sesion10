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
import org.openqa.selenium.Dimension;
import java.util.*;

public class ListadoUsuariosCorrectoTest {
  private WebDriver driver;
  private JavascriptExecutor js;

  @Before
  public void setUp() {
    int browser = 0; // 0: firefox, 1: chrome
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
  }

  @After
  public void tearDown() {
    if (driver != null) driver.quit();
  }

  @Test
  public void listadoUsuariosCorrecto() {
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));

    // 1. Proceso de Login masivo
    WebElement userInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username")));
    userInput.sendKeys("admin");
    driver.findElement(By.id("login-password")).sendKeys("1234");
    driver.findElement(By.cssSelector(".btn-auth-submit")).click();

    // PUNTO CRÍTICO: Esperar a que el formulario de login se oculte (garantiza éxito del login asíncrono)
    wait.until(ExpectedConditions.invisibilityOf(userInput));

    // 2. Acceder de forma segura al módulo de usuarios utilizando una ruta XPath limpia y directa
    By userMenuSelector = By.xpath("//a[contains(@href, 'user') or contains(text(), 'User')]");
    WebElement btnUsuarios = wait.until(ExpectedConditions.presenceOfElementLocated(userMenuSelector));
    
    try {
        wait.until(ExpectedConditions.elementToBeClickable(userMenuSelector)).click();
    } catch (Exception e) {
        js.executeScript("arguments[0].click();", btnUsuarios);
    }

    // 3. Comprobar que el usuario admin aparece en la tabla
    WebElement adminUserCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//td[contains(text(), 'admin')]")
    ));

    assertThat(adminUserCell.getText(), is("admin"));
  }
}