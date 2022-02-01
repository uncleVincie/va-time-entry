package com.vincie.model;

public class HoursAndPages {

    private static final double ROUND_TO = 0.1;

    private final double hours;
    private final int pages;

    public HoursAndPages(double hours, int pages) {
        this.hours = Math.round(hours/ROUND_TO)*ROUND_TO;
        this.pages = pages;
    }

    public double getHours() {
        return hours;
    }

    public int getPages() {
        return pages;
    }

    public String toString() {
        return "hours: " + getHours() + ",pages: " + getPages();
    }
}
