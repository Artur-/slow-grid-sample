package com.sample.test.slowgrid;

import org.junit.Test;

/**
 * This test case verifies grid rendering speeds in Firefox.
 */
public class IT_TestVerifyRenderingTimeFirefox extends TestBaseFirefox {
	
	private final static String BROWSER = "FF";
	
	@Test
	public void verifyRenderingTimeInFirefoxWithGrid_100_1_000() throws Exception {
	 	   verifyRenderingTime(10,0,1000, BROWSER);
	 	   verifyRenderingTime(100,0,1000, BROWSER);
	 	   verifyRenderingTime(50,50,1000, BROWSER);
	}
    
}