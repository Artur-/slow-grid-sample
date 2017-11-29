package com.sample.test.slowgrid;

import org.junit.Test;

/**
 * This test case verifies grid rendering speeds in Google Chrome.
 */
public class IT_TestVerifyRenderingTimeChrome extends TestBaseChrome {
	
	private final static String BROWSER = "CH";
    
    @Test
    public void verifyRenderingTimeInChromeWithDifferentGrids() throws Exception {
 	   verifyRenderingTime(11,1,1000, BROWSER);
 	   verifyRenderingTime(100,0,1000, BROWSER);
 	   verifyRenderingTime(100,0,3000, BROWSER);
 	   verifyRenderingTime(100,20,3000, BROWSER);
    }

}