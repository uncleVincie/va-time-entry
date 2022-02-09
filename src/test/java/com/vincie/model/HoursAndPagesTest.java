package com.vincie.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HoursAndPagesTest {

    @Test
    public void getHours_givenHoursAtWrongPrecision_returnsExpected() {
        HoursAndPages testHours = new HoursAndPages(2.99, 100);
        assertEquals(3.0,testHours.getHours(),0.0001);
    }

    @Test
    public void getHours_givenHoursAtRightPrecision_returnsExpected() {
        HoursAndPages testHours = new HoursAndPages(3.0, 100);
        assertEquals(3.0,testHours.getHours(),0.0001);
    }

    @Test
    public void toString_givenHoursAndPages_returnsExpected() {
        HoursAndPages testHours = new HoursAndPages(3.0, 100);
        String expectedString = "hours: " + testHours.getHours() + ",pages: " + testHours.getPages();
        assertEquals(expectedString, testHours.toString());
    }
}
