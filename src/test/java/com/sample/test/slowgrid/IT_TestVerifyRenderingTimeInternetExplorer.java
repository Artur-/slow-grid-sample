package com.sample.test.slowgrid;

import org.junit.Test;

/**
 * This test case verifies grid rendering speeds in Internet Explorer.
 */
public class IT_TestVerifyRenderingTimeInternetExplorer extends TestBaseInternetExplorer {

    private final static String BROWSER = "IE";

    @Test
    public void verifyRenderingTimeInInternetExplorerWithGrid_100_1_000() throws Exception {
        verifyRenderingTime(false, true, 11, 20, 1000, BROWSER);
        verifyRenderingTime(true, true, 11, 20, 1000, BROWSER);
        verifyRenderingTime(false, true, 11, 40, 1000, BROWSER);
        verifyRenderingTime(true, true, 11, 40, 1000, BROWSER);
        /*        verifyRenderingTime(true, false, 11, 20, 1000, BROWSER);
        verifyRenderingTime(true, true, 11, 20, 1000, BROWSER);
        verifyRenderingTime(true, false, 11, 40, 1000, BROWSER);
        verifyRenderingTime(true, true, 11, 40, 1000, BROWSER);
        verifyRenderingTime(false, false, 11, 20, 1000, BROWSER);
        verifyRenderingTime(false, true, 11, 20, 1000, BROWSER);
        verifyRenderingTime(false, false, 11, 40, 1000, BROWSER);
        verifyRenderingTime(false, true, 11, 40, 1000, BROWSER);*/
    }

}
