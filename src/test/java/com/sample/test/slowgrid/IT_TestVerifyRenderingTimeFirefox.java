package com.sample.test.slowgrid;

import org.junit.Ignore;
import org.junit.Test;

/**
 * This test case verifies grid rendering speeds in Firefox.
 */
public class IT_TestVerifyRenderingTimeFirefox extends TestBaseFirefox {

    private final static String BROWSER = "FF";

    /**
     * Use the LatencyFilter method by just clicking the hide/show Grid button.
     * 
     * @throws Exception Exception and exception during tests
     */
    @Test
    public void clickHideAndShowGridButtonAllCombo() throws Exception {
    	clickHideAndShowGridButton(true, false, 20, 15, 1000, BROWSER);
    	clickHideAndShowGridButton(true, true, 20, 15, 1000, BROWSER);
    	clickHideAndShowGridButton(false, true, 20, 15, 1000, BROWSER);
    }
    
    /**
     * These tests do not create times for rendering because Vaadin Testbench 
     * with the waitForVaadin routine adds extra time querying the grid.
     * <br>
     * Better use the LatencyFilter method by just clicking the hide/show Grid button.
     * 
     * @throws Exception Exception and exception during tests
     */
    @Test
    @Ignore("Taking rendering time with Testbench takes longer because waitForVaadin is too intrusive.")
    public void verifyRenderingTimeInFirefoxWithGrid_100_1_000() throws Exception {
        verifyRenderingTime(true, false, 20, 15, 1000, BROWSER);
        verifyRenderingTime(true, true, 20, 15, 1000, BROWSER);
        verifyRenderingTime(false, true, 20, 15, 1000, BROWSER);
    }

}
