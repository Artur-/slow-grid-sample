package com.sample.test.slowgrid;

import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.ie.InternetExplorerDriver;

import io.github.bonigarcia.wdm.InternetExplorerDriverManager;

/**
 * Base class for Internet Explorer tests.
 */
public class TestBaseInternetExplorer extends TestBase {

    @BeforeClass
    public static void setupClass() {
    	InternetExplorerDriverManager.getInstance().setup();
    }
    
    @Override
	@Before
    public void setUp() throws Exception {
        driver = new InternetExplorerDriver();
        setDriver(driver);
    }
    
}