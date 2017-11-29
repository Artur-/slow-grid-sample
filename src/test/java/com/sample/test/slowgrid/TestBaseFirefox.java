package com.sample.test.slowgrid;

import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.github.bonigarcia.wdm.FirefoxDriverManager;

/**
 * Base class for Firefox tests.
 */
public class TestBaseFirefox extends TestBase {

    @BeforeClass
    public static void setupClass() {
    	FirefoxDriverManager.getInstance().setup();
    }
    
    @Override
	@Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        setDriver(driver);
    }
    
}
