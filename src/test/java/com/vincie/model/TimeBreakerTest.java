package com.vincie.model;

import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.Test;

import javax.management.timer.TimerMBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TimeBreakerTest {

    @Test
    public void combineTime_givenARemainder_addsToAShortTime() {
        List<HoursAndPages> testTimes = new ArrayList<>();
        HoursAndPages hp1 = new HoursAndPages(3.0, 100);
        HoursAndPages hp2 = new HoursAndPages(0.1, 10);
        HoursAndPages hp3 = new HoursAndPages(2.8, 99);
        testTimes.add(hp1);
        testTimes.add(hp2);
        testTimes.add(hp3);

        TimeBreaker timeBreaker = new TimeBreaker();
        List<HoursAndPages> combinedTime = timeBreaker.combineTime(testTimes);
        assertEquals(2,combinedTime.size());
        assertEquals(2.9, combinedTime.get(1).getHours(),0.001);
        assertEquals(109, combinedTime.get(1).getPages(), 0.001);
        testTimes.stream().forEach(hp -> assertTrue(hp.getHours() < 3.01));
        assertEquals(3.0+0.1+2.8, testTimes.stream().mapToDouble(hp -> hp.getHours()).sum(),0.001);
        assertEquals(100+10+99, testTimes.stream().mapToDouble(hp -> hp.getPages()).sum());
    }

    @Test
    public void combineTime_givenNoRemainders_returnsInput() {
        List<HoursAndPages> testTimes = new ArrayList<>();
        HoursAndPages hp1 = new HoursAndPages(3.0, 100);
        HoursAndPages hp2 = new HoursAndPages(0.5, 10);
        HoursAndPages hp3 = new HoursAndPages(2.8, 99);
        testTimes.add(hp1);
        testTimes.add(hp2);
        testTimes.add(hp3);

        TimeBreaker timeBreaker = new TimeBreaker();
        List<HoursAndPages> combinedTime = timeBreaker.combineTime(testTimes);
        for (int i=0;i<combinedTime.size();i++) {
            assertEquals(testTimes.get(i).getHours(),combinedTime.get(i).getHours(),0.001);
            assertEquals(testTimes.get(i).getPages(),combinedTime.get(i).getPages());
        }
    }

    @Test
    public void run_givenInputTime_returnsValidOutputTime() {
        List<Double> inputTimes = new ArrayList<>();
        inputTimes.add(3.0);
        inputTimes.add(0.5);
        inputTimes.add(6.0);
        inputTimes.add(3.1);
        inputTimes.add(0.1);

        final double expectedHours = inputTimes.stream().mapToDouble(d -> d).sum();
        final int expectedPages = 1000;

        TimeBreaker timeBreaker = new TimeBreaker();
        timeBreaker.setInputHours(inputTimes);
        timeBreaker.setTotalPages(expectedPages);
        timeBreaker.run();
        List<HoursAndPages> outputTimes = timeBreaker.getOutputHoursAndPages();

        final double actualHours = outputTimes.stream().mapToDouble(hp -> hp.getHours()).sum();
        final int actualPages = outputTimes.stream().mapToInt(hp -> hp.getPages()).sum();

        assertEquals(expectedHours,actualHours,0.01);
        assertEquals(expectedPages,actualPages);
        outputTimes.stream().forEach(hp -> assertTrue(hp.getHours() <= TimeBreaker.MAX_ALLOWABLE_HOURS));
        outputTimes.stream().forEach(hp -> assertTrue(hp.getHours() >= TimeBreaker.MIN_ALLOWABLE_HOURS));

    }

    @Test
    public void run_runTwice_returnsSameAsRunningOnce() {

    }

}
