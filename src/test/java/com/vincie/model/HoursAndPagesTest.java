package com.vincie.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HoursAndPagesTest {

    @Test
    public void getHours_givenHoursAtWrongPrecision_returnsExpected() {
        HoursAndPages testHours = new HoursAndPages(2.99, 100);
        assertEquals(3.0,testHours.getHours(),0.0001);
    }
}
