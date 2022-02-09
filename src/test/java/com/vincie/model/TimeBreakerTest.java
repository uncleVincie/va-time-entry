package com.vincie.model;

import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeBreakerTest {

    @Test
    public void calculatePageRate_givenValidInput_returnsRate() {
        TimeBreaker timeBreaker = new TimeBreaker();
        timeBreaker.setTotalPages(1000);
        timeBreaker.addInputHours(1.0);
        timeBreaker.addInputHours(1.0);
        timeBreaker.addInputHours(1.0);
        assertEquals((double) 1000/3, timeBreaker.calculatePageRate(), 0.001);
    }

    @Test
    public void breakTime_givenTimeToBreak_returnsExpected() {
        TimeBreaker timeBreaker = new TimeBreaker();
        List<Double> input = new ArrayList<>();
        input.add(3.5);
        input.add(2.0);
        input.add(1.0);
        List<HoursAndPages> output = timeBreaker.breakTime(input, 100);
        List<Double> expectedHours = Arrays.asList(3.0, 0.5, 2.0, 1.0);
        List<Double> expectedAveragePages = expectedHours.stream().map(h -> h*100.).collect(Collectors.toList());
        List<Double> maxPageDeviation = expectedAveragePages.stream().map(p -> p * TimeBreaker.MAX_PAGE_MODIFIER).collect(Collectors.toList());
        for(int i=0;i<output.size();i++) {
            assertEquals(expectedHours.get(i),output.get(i).getHours(), 0.001);
            assertTrue(Math.abs(output.get(i).getPages()-expectedAveragePages.get(i)) <= maxPageDeviation.get(i));
        }
    }

    @Test
    public void breakTime_givenNoTimeToBreak_returnsExpected() {
        TimeBreaker timeBreaker = new TimeBreaker();
        List<Double> input = new ArrayList<>();
        input.add(3.0);
        input.add(2.0);
        input.add(1.0);
        List<HoursAndPages> output = timeBreaker.breakTime(input, 100);
        List<Double> expectedHours = Arrays.asList(3.0, 2.0, 1.0);
        List<Double> expectedAveragePages = expectedHours.stream().map(h -> h*100.).collect(Collectors.toList());
        List<Double> maxPageDeviation = expectedAveragePages.stream().map(p -> p * TimeBreaker.MAX_PAGE_MODIFIER).collect(Collectors.toList());
        for(int i=0;i<output.size();i++) {
            assertEquals(expectedHours.get(i),output.get(i).getHours(), 0.001);
            assertTrue(Math.abs(output.get(i).getPages()-expectedAveragePages.get(i)) <= maxPageDeviation.get(i));
        }
    }

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
        assertEquals(3.0+0.1+2.8, testTimes.stream().mapToDouble(HoursAndPages::getHours).sum(),0.001);
        assertEquals(100+10+99, testTimes.stream().mapToDouble(HoursAndPages::getPages).sum());
    }

    @Test
    public void combineTime_givenNowhereToPutARemainder_addsToTheEnd() {
        List<HoursAndPages> testTimes = new ArrayList<>();
        HoursAndPages hp1 = new HoursAndPages(3.0, 100);
        HoursAndPages hp2 = new HoursAndPages(0.1, 10);
        HoursAndPages hp3 = new HoursAndPages(3.0, 99);
        testTimes.add(hp1);
        testTimes.add(hp2);
        testTimes.add(hp3);

        TimeBreaker timeBreaker = new TimeBreaker();
        List<HoursAndPages> combinedTime = timeBreaker.combineTime(testTimes);
        assertEquals(3,combinedTime.size());
        assertEquals(0.1, combinedTime.get(2).getHours(),0.001);
        assertEquals(10, combinedTime.get(2).getPages());
        testTimes.stream().forEach(hp -> assertTrue(hp.getHours() < 3.01));
        assertEquals(3.0+0.1+3.0, testTimes.stream().mapToDouble(HoursAndPages::getHours).sum(),0.001);
        assertEquals(100+10+99, testTimes.stream().mapToDouble(HoursAndPages::getPages).sum());
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
    public void rectifyTime_givenLowTimeAndPages_returnsCorrectTimeAndPages() {
        List<HoursAndPages> badTime = new ArrayList<>();
        badTime.add(new HoursAndPages(1.9, 300));
        badTime.add(new HoursAndPages(0.9, 300));
        badTime.add(new HoursAndPages(1.4, 300));
        rectifyTimeTest(badTime);
    }

    @Test
    public void rectifyTime_givenHighTimeAndPages_returnsCorrectTimeAndPages() {
        List<HoursAndPages> badTime = new ArrayList<>();
        badTime.add(new HoursAndPages(2.1, 400));
        badTime.add(new HoursAndPages(1.1, 400));
        badTime.add(new HoursAndPages(1.6, 400));
        rectifyTimeTest(badTime);
    }

    @Test
    public void rectifyTime_givenLowTimeHighPages_returnsCorrectTimeAndPages() {
        List<HoursAndPages> badTime = new ArrayList<>();
        badTime.add(new HoursAndPages(1.9, 400));
        badTime.add(new HoursAndPages(0.9, 400));
        badTime.add(new HoursAndPages(1.4, 400));
        rectifyTimeTest(badTime);
    }

    @Test
    public void rectifyTime_givenHighTimeLowPages_returnsCorrectTimeAndPages() {
        List<HoursAndPages> badTime = new ArrayList<>();
        badTime.add(new HoursAndPages(2.1, 300));
        badTime.add(new HoursAndPages(1.1, 300));
        badTime.add(new HoursAndPages(1.6, 300));
        rectifyTimeTest(badTime);
    }

    @Test
    public void rectifyTime_givenCorrectTimeLowPages_returnsCorrectTimeAndPages() {
        List<HoursAndPages> badTime = new ArrayList<>();
        badTime.add(new HoursAndPages(2.0, 300));
        badTime.add(new HoursAndPages(1.0, 300));
        badTime.add(new HoursAndPages(1.5, 300));
        rectifyTimeTest(badTime);
    }

    @Test
    public void rectifyTime_givenLowTimeCorrectPages_returnsCorrectTimeAndPages() {
        List<HoursAndPages> badTime = new ArrayList<>();
        badTime.add(new HoursAndPages(1.9, 400));
        badTime.add(new HoursAndPages(0.9, 400));
        badTime.add(new HoursAndPages(1.4, 200));
        rectifyTimeTest(badTime);
    }

    @Test
    public void rectifyTime_givenCorrectTime_returnsInput() {
        TimeBreaker timeBreaker = new TimeBreaker();
        timeBreaker.setTotalPages(1000);
        timeBreaker.setInputHours(Arrays.asList(2.0, 1.0, 1.5));
        timeBreaker.calculatePageRate();
        List<HoursAndPages> badTime = new ArrayList<>();
        badTime.add(new HoursAndPages(2.0, 400));
        badTime.add(new HoursAndPages(1.0, 400));
        badTime.add(new HoursAndPages(1.5, 200));
        List<HoursAndPages> output = timeBreaker.rectifyTime(badTime);
        for (int i=0;i<output.size();i++) {
            assertEquals(badTime.get(i).getHours(), output.get(i).getHours(),0.001);
            assertEquals(badTime.get(i).getPages(), output.get(i).getPages());
        }
    }

    private void rectifyTimeTest(List<HoursAndPages> testTimes) {
        final int inputPages = 1000;
        final List<Double> inputHours = Arrays.asList(2.0, 1.0, 1.5);

        TimeBreaker timeBreaker = new TimeBreaker();
        timeBreaker.setTotalPages(inputPages);
        timeBreaker.setInputHours(inputHours);
        timeBreaker.calculatePageRate();
        List<HoursAndPages> badTime = new ArrayList<>();

        final double expectedHours = inputHours.stream().mapToDouble(h -> h).sum();

        List<HoursAndPages> output = timeBreaker.rectifyTime(testTimes);
        assertEquals(expectedHours, output.stream().mapToDouble(HoursAndPages::getHours).sum(), 0.001);
        assertEquals(inputPages, output.stream().mapToInt(HoursAndPages::getPages).sum());
    }

    @Test
    public void run_givenInputTime_returnsValidOutputTime() {
        List<Double> inputTimes = new ArrayList<>();
        inputTimes.add(3.0);
        inputTimes.add(0.5);
        inputTimes.add(7.1);
        inputTimes.add(3.0);
        inputTimes.add(0.1);

        final double expectedHours = inputTimes.stream().mapToDouble(d -> d).sum();
        final int expectedPages = 1000;

        TimeBreaker timeBreaker = new TimeBreaker();
        timeBreaker.setInputHours(inputTimes);
        timeBreaker.setTotalPages(expectedPages);
        timeBreaker.run();
        List<HoursAndPages> outputTimes = timeBreaker.getOutputHoursAndPages();

        double actualHours = outputTimes.stream().mapToDouble(HoursAndPages::getHours).sum();
        int actualPages = outputTimes.stream().mapToInt(HoursAndPages::getPages).sum();

        assertEquals(expectedHours,actualHours,0.01);
        assertEquals(expectedPages,actualPages);
        outputTimes.stream().forEach(hp -> assertTrue(hp.getHours() <= TimeBreaker.MAX_ALLOWABLE_HOURS));
        outputTimes.stream().forEach(hp -> assertTrue(hp.getHours() >= TimeBreaker.MIN_ALLOWABLE_HOURS));

        //test consecutive runs
        timeBreaker.setInputHours(inputTimes);
        timeBreaker.setTotalPages(expectedPages);
        timeBreaker.run();
        timeBreaker.run();
        outputTimes = timeBreaker.getOutputHoursAndPages();

        actualHours = outputTimes.stream().mapToDouble(HoursAndPages::getHours).sum();
        actualPages = outputTimes.stream().mapToInt(HoursAndPages::getPages).sum();

        assertEquals(expectedHours,actualHours,0.01);
        assertEquals(expectedPages,actualPages);
    }

    @Test
    public void addInputHours_givenValidInputHours_addsToOutput() {
        TimeBreaker timeBreaker = new TimeBreaker();
        final double[] testHours = {3.0, 2.0};
        timeBreaker.addInputHours(testHours[0]);
        timeBreaker.setTotalPages(100);
        timeBreaker.run();
        assertEquals(testHours[0], timeBreaker.getOutputHoursAndPages().get(0).getHours());
        timeBreaker.addInputHours(testHours[1]);
        timeBreaker.run();
        assertEquals(testHours[1], timeBreaker.getOutputHoursAndPages().get(1).getHours());
    }

}
