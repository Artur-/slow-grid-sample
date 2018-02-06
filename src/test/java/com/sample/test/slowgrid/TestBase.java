package com.sample.test.slowgrid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.text.MessageFormat;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.commands.TestBenchCommands;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;

/**
 * Base class for all our tests, allowing us to change the applicable driver,
 * test URL or other configurations in one place.
 */
public class TestBase extends TestBenchTestCase {
	
	private FileWriter fw;
	private static final String resultsFile = "results.txt";
    public static final String baseUrl = "http://localhost:8080/";

    @Before
    public void setUp() throws Exception {
  	
        // Create a new Selenium driver - it is automatically extended to work
        // with TestBench
        driver = new ChromeDriver();
        setDriver(driver);

        // Open the test application URL with the ?restartApplication URL
        // parameter to ensure Vaadin provides us with a fresh UI instance.
        getDriver().get(baseUrl + "?restartApplication");
        // Set a fixed view port of 1024x768 pixels for comparison
        ((TestBenchCommands)driver).resizeViewPortTo( 1280, 1024 );

        // If you deploy using WTP in Eclipse, this will fail. You should
        // update baseUrl to point to where the app is deployed.
        String pageSource = getDriver().getPageSource();
        String errorMsg = "Application is not available at " + baseUrl + ". Server not started?";
        assertFalse(errorMsg, pageSource.contains("HTTP Status 404")
        		|| pageSource.contains("can't establish a connection to the server"));
    }

    @After
    public void tearDown() throws Exception {

        // Calling quit() on the driver closes the test browser.
        // When called like this, the browser is immediately closed on _any_
        // error. If you wish to take a screenshot of the browser at the time
        // the error occurred, you'll need to add the ScreenshotOnFailureRule
        // to your test and remove this call to quit().
    	getDriver().quit();
    }
    
    private String getVaadinVersionfromHiddenLabel() {
    	String replaceme = "Grid Test with Vaadin Version: ";
        return $(LabelElement.class).id("vaadinVersionLabel").getText().replace(replaceme, "");
    }
    
    private WebElement getGridButton() {
        return $(ButtonElement.class).id("gridButton");
    }
    
    private GridElement getGrid() {
    	return $(GridElement.class).id("testGrid");
    }
    
    private void clickGridButton() {
    	getGridButton().click();
    }
    
    /**
     * Set value in the TextField with id.
     */
    private void setTextField(final String id, final String value) {
    	$(TextFieldElement.class).id(id).setValue(value);
    }
    
    /**
     * Set the grid details.
     */
    private void setGridValues(final int columns, final int hiddenColumns, final int rows) {
    	setTextField("columncount", String.valueOf(columns));
    	setTextField("hiddencolumncount", String.valueOf(hiddenColumns));
    	setTextField("rowcount", String.valueOf(rows));
    }

    /**
     * Get the rendering time for the grid in a browser.
     * 
     * @throws Exception an Exception during tests
     */
    public void verifyRenderingTime(final int columns, final int hiddenColumns, final int rows, final String browser) throws Exception {
    	File file = new File(resultsFile);
    	if (file.exists()) {
    		fw = new FileWriter(resultsFile, true); // append to file
    	} else {
    		fw = new FileWriter(resultsFile, true); // append to file
    		fw.write("|Browser|Vaadin Version|Grid Size (C,HC,R)|Render Time (ms)|Request Time (ms)| Time Until Rendered");
    		fw.write(System.getProperty("line.separator")); // newline
    		fw.write("|-------|--------------|------------------|----------------|-----------------|");
    		fw.write(System.getProperty("line.separator")); // newline
    	}
    	getDriver().get(baseUrl + "?restartApplication");
    	String vaadinVersion = getVaadinVersionfromHiddenLabel();
    	//String vaadinVersion = "xxx";
    	
    	clickGridButton(); // hides the grid
    	
        long currentSessionTime = testBench(getDriver())
                .totalTimeSpentServicingRequests();
        
        setGridValues(columns,hiddenColumns,rows);
        long startGrid = System.currentTimeMillis();
        clickGridButton(); // builds the grid with specified columns, hidden columns and rows
      
        testBench().waitForVaadin();
        startGrid = System.currentTimeMillis() - startGrid;
        long timeSpentByServerForServicingGridRequest = testBench()
                .totalTimeSpentServicingRequests() - currentSessionTime;
        

//        System.out.println("Building the grid took about "
//                + timeSpentByServerForSimpleCalculation
//                + "ms in servlets service method.");

        long totalTimeSpentRendering = testBench().totalTimeSpentRendering();
        
        String out = MessageFormat.format("|{0}|{1}|({2,number,#}, {3,number,#}, {4,number,#})|{5,number,#}|{6,number,#}|{7,number,#}",
        		browser, vaadinVersion, columns, hiddenColumns, rows, totalTimeSpentRendering, timeSpentByServerForServicingGridRequest, startGrid);
        fw.write(out);
        fw.write(System.getProperty("line.separator")); // newline
        System.out.println(out);

        if (totalTimeSpentRendering > 10000) {
            //fail("Rendering Grid shouldn't take " + totalTimeSpentRendering + "ms!");
        	System.err.println("Rendering Grid shouldn't take " + totalTimeSpentRendering + "ms!");
        }

        fw.close();
        //assert("Content #1", getGrid().getCell(0, 1).getText());
    }
}