package com.sample.test.slowgrid;

import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.ChromeDriverManager;

/**
 * Base class for Chrome tests.
 */
public class TestBaseChrome extends TestBase {

    @BeforeClass
    public static void setupClass() {
        ChromeDriverManager.getInstance().setup();
    }
    
    @Override
	@Before
    public void setUp() throws Exception {
        driver = new ChromeDriver();
        setDriver(driver);
    }
    
}