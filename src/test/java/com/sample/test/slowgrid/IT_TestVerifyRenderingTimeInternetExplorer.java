package com.sample.test.slowgrid;

import org.junit.Test;

/**
 * This test case verifies grid rendering speeds in Internet Explorer.
 */
public class IT_TestVerifyRenderingTimeInternetExplorer extends TestBaseInternetExplorer {
	
	private final static String BROWSER = "IE";
	
	@Test
	public void verifyRenderingTimeInInternetExplorerWithGrid_100_1_000() throws Exception {
	 	   verifyRenderingTime(10,0,1000, BROWSER);
	 	   verifyRenderingTime(100,0,1000, BROWSER);
	 	   verifyRenderingTime(50,50,1000, BROWSER);
	}
 
}