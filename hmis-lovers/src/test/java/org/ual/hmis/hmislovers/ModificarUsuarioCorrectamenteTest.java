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
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(15));

    // 1 | open | Cargar URL
    driver.get("https://calm-moss-09572aa03.7.azurestaticapps.net/");
    driver.manage().window().setSize(new Dimension(1920, 1080));
    
    // LOGIN OBLIGATORIO PARA ENTORNO LIMPIO
    try {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-username"))).sendKeys("admin");
        driver.findElement(By.id("login-password")).sendKeys("1234");
        driver.findElement(By.cssSelector(".btn-auth-submit")).click();
        Thread.sleep(2000);
    } catch (Exception e) {
        System.out.println("Login no requerido o estructura diferente. Continuando...");
    }
    
    // 3 | click | Botón del panel de administración
    WebElement btnAdminHeader = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-admin-header > .material-icons")));
    btnAdminHeader.click();
    
    // 4 & 5 | mouseOver/mouseOut
    try {
      Actions builder = new Actions(driver);
      builder.moveToElement(btnAdminHeader).perform();
      WebElement bodyElement = driver.findElement(By.tagName("body"));
      builder.moveToElement(bodyElement, 0, 0).perform();
    } catch (Exception e) {}
    
    // 6 | click | Clic en 'edit' de la fila 5
    WebElement btnEditRow = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("tr:nth-child(5) .edit")));
    btnEditRow.click();
    
    // 7 & 8 | click & type | Modificar input username
    WebElement inputUsername = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("admin-edit-username")));
    inputUsername.click();
    inputUsername.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
    inputUsername.sendKeys("pepejuan370123");
    
    // 9 | click | Guardar cambios
    WebElement btnSubmitBar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-submit-bar")));
    btnSubmitBar.click();
    
    // [MEJORA CRÍTICA]: Esperar a que el modal overlay desaparezca de la pantalla por completo
    try {
      wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal-overlay")));
    } catch (Exception e) {
      System.out.println("El modal no apareció o tardó en irse, intentando continuar...");
    }
    
    // 10 & 11 | click & assertText | Comprobar fila modificada
    WebElement txtResult = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".admin-card:nth-child(1) tr:nth-child(5) > .font-bold")));
    txtResult.click(); 
    
    assertThat(txtResult.getText(), is("pepejuan370123"));
  }
}