package com.vincie.model;

import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimeBreaker {

    public static final double MAX_ALLOWABLE_HOURS = 3.0;
    public static final double MAX_PAGE_MODIFIER = 0.10; //fraction of any division of time
    public static final double MIN_ALLOWABLE_HOURS = 0.2;

    private List<Double> inputHours = new ArrayList<>();
    private List<HoursAndPages> outputHoursAndPages = new ArrayList<>();
    private int totalPages;
    private double totalHours;

    public TimeBreaker() {}

    public void run() {
        final double rate = calculatePageRate();
        List<HoursAndPages> output = new ArrayList<>(breakTime(inputHours, rate));

        output = combineTime(output);
        output = rectifyTime(output);
        setOutputHoursAndPages(output);
    }

    public double calculatePageRate() {
        totalHours = inputHours.stream().mapToDouble(d -> d).sum();
        return totalPages/totalHours;
    }

    public List<HoursAndPages> breakTime(List<Double> input, double rate) {
        List<HoursAndPages> brokenTime = new ArrayList<>();

        for (Double hoursToGo:input) {

            if (hoursToGo <= MAX_ALLOWABLE_HOURS) {
                //if time doesn't need broken
                brokenTime.add(new HoursAndPages(hoursToGo, makeUpPages(hoursToGo, rate)));
            } else {
                while (hoursToGo > MAX_ALLOWABLE_HOURS) {
                    hoursToGo -= MAX_ALLOWABLE_HOURS;
                    brokenTime.add(new HoursAndPages(MAX_ALLOWABLE_HOURS, makeUpPages(MAX_ALLOWABLE_HOURS, rate)));
                }
                if (!Precision.equals(hoursToGo,0)) {
                    brokenTime.add(new HoursAndPages(hoursToGo, makeUpPages(hoursToGo, rate)));
                }
            }
        }
        return brokenTime;
    }

    private int makeUpPages(double hours, double rate) {
        final int sign = new Random().nextBoolean() ? 1 : -1;
        int currentPages = (int) (hours * rate);
        currentPages += (int) (sign * currentPages * Math.random() * MAX_PAGE_MODIFIER);
        return currentPages;
    }

    public List<HoursAndPages> combineTime(List<HoursAndPages> inputHoursAndPages) {
        List<HoursAndPages> combinedHours = new ArrayList<>();
        List<HoursAndPages> stubs = new ArrayList<>();
        for (HoursAndPages hp : inputHoursAndPages) {
            if (hp.getHours() < MIN_ALLOWABLE_HOURS) {
                stubs.add(hp);
            } else {
                combinedHours.add(hp);
            }
        }

        if (stubs.isEmpty()) { return combinedHours; }

        final double totalStubHours = stubs.stream().mapToDouble(HoursAndPages::getHours).sum();
        final int totalStubPages = stubs.stream().mapToInt(HoursAndPages::getPages).sum();

        if (totalStubHours < MIN_ALLOWABLE_HOURS) {
            for (int i=0;i<combinedHours.size();i++) {
                HoursAndPages hp = combinedHours.get(i);
                if (hp.getHours() + totalStubHours < MAX_ALLOWABLE_HOURS) {
                    combinedHours.set(i, new HoursAndPages(hp.getHours()+totalStubHours, hp.getPages()+totalStubPages));
                    return combinedHours;
                }
            }
        }
        combinedHours.add(new HoursAndPages(totalStubHours, totalStubPages)); //if no place to put the stub was found

        return combinedHours;
    }

    public List<HoursAndPages> rectifyTime(List<HoursAndPages> incorrectTime) {
        final double incorrectHours = incorrectTime.stream().mapToDouble(HoursAndPages::getHours).sum();
        List<HoursAndPages> correctTime = new ArrayList<>(incorrectTime);
        final int incorrectPages = incorrectTime.stream().mapToInt(HoursAndPages::getPages).sum();
        double hoursToAdd = totalHours - incorrectHours;
        int pagesToAdd = totalPages - incorrectPages;
        double timeIncrement;

        if (Math.abs(hoursToAdd) > 0 || Math.abs(pagesToAdd) > 0) {
            int pagesIncrement = pagesToAdd/correctTime.size();
            final int theRemainingPages = pagesToAdd % correctTime.size();
            for(int i=0;i<correctTime.size();i++) {
                HoursAndPages hp = correctTime.get(i);

                if (hp.getHours() + hoursToAdd < MAX_ALLOWABLE_HOURS) {
                    timeIncrement = hoursToAdd;
                } else {
                    timeIncrement = MAX_ALLOWABLE_HOURS - hp.getHours();
                }
                hoursToAdd -= timeIncrement;
                if (i == correctTime.size()-1) {
                    correctTime.set(i, new HoursAndPages(hp.getHours()+timeIncrement,hp.getPages()+pagesIncrement+theRemainingPages));
                } else {
                    correctTime.set(i, new HoursAndPages(hp.getHours() + timeIncrement, hp.getPages() + pagesIncrement));
                }
            }
        }
        return correctTime;
    }

    public List<HoursAndPages> getOutputHoursAndPages() {
        return outputHoursAndPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setInputHours(List<Double> inputHours) {
        this.inputHours = inputHours;
    }

    public void addInputHours(double inputHours) {
        this.inputHours.add(inputHours);
    }

    private void setOutputHoursAndPages(List<HoursAndPages> hoursAndPages) {
        outputHoursAndPages = hoursAndPages;
    }
}
