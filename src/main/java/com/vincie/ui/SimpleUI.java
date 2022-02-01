package com.vincie.ui;

import com.vincie.model.HoursAndPages;
import com.vincie.model.IllegalHoursOrPagesException;
import com.vincie.model.TimeBreaker;
import org.apache.commons.math3.util.Precision;

import java.util.Scanner;

public class SimpleUI {

    private static TimeBreaker timeBreaker = new TimeBreaker();

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        System.out.println("Enter total pages:");

        timeBreaker.setTotalPages(in.nextInt());

        System.out.println("Now enter your hours (enter 0 to finish):");

        double hours;
        do {
            hours = in.nextDouble();

            timeBreaker.addInputHours(hours);

        } while (!Precision.equals(hours, 0));
        calculateOutput();
    }

    private static void calculateOutput() {
        timeBreaker.run();
        System.out.println("Here is the corrected time:");
        for (HoursAndPages hp: timeBreaker.getOutputHoursAndPages()) {
            System.out.println(hp.getHours() + "," + hp.getPages());
        }
    }

}
