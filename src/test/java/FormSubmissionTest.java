import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class FormSubmissionTest {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    @DisplayName("Test form submission functionality")
    public void formSubmission() throws InterruptedException {
        driver.get("https://demo.wpeverest.com/user-registration/guest-registration-form/");

        // Wait for form to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("first_name")));

        driver.findElement(By.id("first_name")).sendKeys("Ayesha");
        driver.findElement(By.id("last_name")).sendKeys("Prianka");
        driver.findElement(By.id("user_email")).sendKeys("ayesha" + System.currentTimeMillis() + "@gmail.com");
        driver.findElement(By.cssSelector("input[value='Female']")).click();
        driver.findElement(By.id("user_pass")).sendKeys("StrongPass123%^^%#@!");
        driver.findElement(By.id("input_box_1665629217")).sendKeys("Bangladeshi");


        // Date Of Birth
        WebElement dateInput = driver.findElement(By.cssSelector(".ur-flatpickr-field"));
        dateInput.click();
        Thread.sleep(500);
        WebElement yearInput = driver.findElement(By.className("numInput"));
        yearInput.click();
        yearInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        yearInput.sendKeys("1997");
        WebElement monthSelect = driver.findElement(By.className("flatpickr-monthDropdown-months"));
        monthSelect.click();
        WebElement mayOption = driver.findElement(By.cssSelector("option[value='3']"));
        mayOption.click();
        WebElement day10 = driver.findElement(By.cssSelector("span.flatpickr-day[aria-label*='4']"));
        day10.click();

        //  phone number
        WebElement visiblePhoneInput = driver.findElement(By.cssSelector("input[name='phone_1665627880']"));
        visiblePhoneInput.click();
        visiblePhoneInput.clear();
        visiblePhoneInput.sendKeys("1621648946");

        new Select(driver.findElement(By.id("country_1665629257"))).selectByVisibleText("Bangladesh");
        driver.findElement(By.id("privacy_policy_1665633140")).click();


        // submit
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", submitButton);
        Thread.sleep(1000);
        js.executeScript("document.activeElement.blur();");
        Thread.sleep(500);
        boolean submitted = false;
        int attempts = 0;
        while (!submitted) {
            try {
                submitButton.click();
                submitted = true;
            } catch (Exception e) {
                attempts++;
                if (attempts < 3) {
                    // Try JavaScript click
                    js.executeScript("arguments[0].click();", submitButton);
                    Thread.sleep(500);
                } else {
                    throw new RuntimeException("Failed to submit form after 3 attempts");
                }
            }
        }

        WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("ur-submit-message-node")));

        Assertions.assertTrue(success.isDisplayed(), "Success message should be displayed");
        Assertions.assertTrue(success.getText().contains("User successfully registered"),
                "Success message should contain registration confirmation");

        System.out.println("✓ Form submitted successfully!");
        System.out.println("Success message: " + success.getText());
    }


    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}