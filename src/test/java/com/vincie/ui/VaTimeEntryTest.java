package com.vincie.ui;

import com.vincie.model.TimeBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VaTimeEntryTest {

    private static final String GOOD_PAGES = "1000";
    private static final String BAD_PAGES = "Horse";
    private static final String GOOD_HOURS = "1.0\n2.0\n3.0\n";
    private static final String GOOD_TOTAL_HOURS = "6.0";
    private static final String[] BAD_HOURS = {"1.0.0\n","2.0, 3.0\n"};

    VaTimeEntry vaTimeEntry;

    @BeforeEach
    void setup() {
        vaTimeEntry = new VaTimeEntry(new TimeBreaker());
        vaTimeEntry.buildGui();
    }

    @Test
    void parseTotalPages_givenValidPages_returnsExpected() {
        vaTimeEntry.getTotalHoursField().setText(GOOD_PAGES);
        vaTimeEntry.getInputHoursField().setText(GOOD_HOURS);
        //click twice
        vaTimeEntry.getComputeButton().doClick();
        vaTimeEntry.getComputeButton().doClick();
        assertEquals(GOOD_TOTAL_HOURS+" , "+GOOD_PAGES, vaTimeEntry.getOutputTotals().getText());
        assertEquals(3, vaTimeEntry.getOutputField().getText().split("\n").length);
    }

    @Test
    void parseTotalPages_givenInvalidPages_displaysErrorMessage() {
        vaTimeEntry.getTotalHoursField().setText(BAD_PAGES);
        vaTimeEntry.getInputHoursField().setText(GOOD_HOURS);
        vaTimeEntry.getComputeButton().doClick();
        assertEquals(VaTimeEntry.TOTAL_NUMBER_OF_PAGES_ARE_BAD, vaTimeEntry.getOutputTotals().getText());
    }

    @Test
    void parseTotalPages_givenInvalidHours_displaysErrorMessage() {
        for (String badHours:BAD_HOURS) {
            vaTimeEntry.getTotalHoursField().setText(GOOD_PAGES);
            vaTimeEntry.getInputHoursField().setText(badHours);
            vaTimeEntry.getComputeButton().doClick();
            assertEquals(VaTimeEntry.ONE_OR_MORE_INPUT_HOURS_ARE_BAD, vaTimeEntry.getOutputTotals().getText());
        }
    }
}
