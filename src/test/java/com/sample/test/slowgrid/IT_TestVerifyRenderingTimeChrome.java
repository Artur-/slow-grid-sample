package com.sample.test.slowgrid;

import org.junit.Test;

/**
 * This test case verifies grid rendering speeds in Google Chrome.
 */
public class IT_TestVerifyRenderingTimeChrome extends TestBaseChrome {

    private final static String BROWSER = "CH";

    @Test
    public void verifyRenderingTimeInChromeWithDifferentGrids() throws Exception {
        verifyRenderingTime(true, false, 11, 20, 1000, BROWSER);
        verifyRenderingTime(true, true, 11, 20, 1000, BROWSER);
        verifyRenderingTime(true, false, 11, 40, 1000, BROWSER);
        verifyRenderingTime(true, true, 11, 40, 1000, BROWSER);
        verifyRenderingTime(false, false, 11, 20, 1000, BROWSER);
        verifyRenderingTime(false, true, 11, 20, 1000, BROWSER);
        verifyRenderingTime(false, false, 11, 40, 1000, BROWSER);
        verifyRenderingTime(false, true, 11, 40, 1000, BROWSER);

    }

}
