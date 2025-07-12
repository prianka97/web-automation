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

public class WebScrapTests {
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
    @DisplayName("Test table scraping functionality")
    public void scrapeTable() throws IOException, InterruptedException {
        try {
            driver.get("https://dsebd.org/latest_share_price_scroll_by_value.php");

            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("table.shares-table"), 1));

            List<WebElement> tables = driver.findElements(By.cssSelector("table.shares-table"));
            if (tables.size() < 2) {
                throw new RuntimeException("Expected at least 2 tables, but found: " + tables.size());
            }

            WebElement headerTable = tables.get(0);
            WebElement dataTable = tables.get(1);
            BufferedWriter writer = new BufferedWriter(new FileWriter("dse_data.txt"));

            // Get header data
            WebElement thead = headerTable.findElement(By.tagName("thead"));
            WebElement headerRow = thead.findElement(By.tagName("tr"));
            List<WebElement> headerCells = headerRow.findElements(By.tagName("th"));

            StringBuilder headerText = new StringBuilder();
            for (WebElement cell : headerCells) {
                String cellText = cell.getText().trim();
                headerText.append(cellText).append("\t");
            }

            String headerLine = headerText.toString().trim();
            writer.write(headerLine);
            writer.newLine();
            System.out.println("Header: " + headerLine + "\n");

            // get all row data
            wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(dataTable, By.cssSelector("tbody tr td")));

            List<WebElement> rows = dataTable.findElements(By.cssSelector("tbody tr"));
            System.out.println("Rows found: " + rows.size());

            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                StringBuilder rowText = new StringBuilder();
                for (WebElement cell : cells) {
                    String cellText = cell.getText().trim();
                    rowText.append(cellText).append("\t");
                }

                String line = rowText.toString().trim();
                writer.write(line);
                writer.newLine();
                System.out.println("Row: " + line);
            }

            writer.close();
            System.out.println("✓ Data saved to dse_data.txt");

        } catch (Exception e) {
            System.out.println("❌ Table scraping failed: " + e.getMessage());
            throw e;
        }
    }


    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}