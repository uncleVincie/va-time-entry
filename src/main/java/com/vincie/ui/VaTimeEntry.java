package com.vincie.ui;

import com.vincie.model.HoursAndPages;
import com.vincie.model.TimeBreaker;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VaTimeEntry {

    TimeBreaker timeBreaker;
    private JTextArea outputField;
    private JTextArea inputHoursField;
    private JTextField totalHoursField;
    private JTextField outputTotals;

    public VaTimeEntry(TimeBreaker timeBreaker) {
        this.timeBreaker = timeBreaker;
    }

    public static void main(String[] args) {
        VaTimeEntry app = new VaTimeEntry(new TimeBreaker());
        app.buildGui();
    }

    private void buildGui() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //calculate button
        JButton computeButton = new JButton("Calculate");
        frame.getContentPane().add(BorderLayout.SOUTH, computeButton);
        computeButton.addActionListener(new CalculateListener());

        //total pages
        JPanel pagesPanel = new JPanel();
        pagesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        frame.getContentPane().add(BorderLayout.NORTH, pagesPanel);
        totalHoursField = new JTextField();
        pagesPanel.setLayout(new BoxLayout(pagesPanel, BoxLayout.X_AXIS));
        pagesPanel.add(new JLabel("Enter total pages:"));
        pagesPanel.add(totalHoursField);

        //input hours
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        frame.getContentPane().add(BorderLayout.CENTER, inputPanel);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(new JLabel("Enter hours:"));
        inputPanel.add(new JSeparator());
        inputHoursField = new JTextArea(18,3);
        JScrollPane inputScroller = new JScrollPane(inputHoursField);
        inputScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inputHoursField.setEditable(true);
        inputPanel.add(inputScroller);

        //output hours
        JPanel outputPanel = new JPanel();
        outputPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        frame.getContentPane().add(BorderLayout.EAST, outputPanel);
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        outputPanel.add(new JLabel("Output (Hours, Pages):"));
        outputPanel.add(new JSeparator());
        outputField = new JTextArea(18, 15);
        JScrollPane outputScroller = new JScrollPane(outputField);
        outputScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        outputScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outputField.setEditable(false);
        outputPanel.add(outputScroller);
        outputPanel.add(new JSeparator());
        outputTotals = new JTextField();
        outputTotals.setEditable(false);
        outputPanel.add(outputTotals);

        //draw
        frame.setSize(400,500);
        frame.setVisible(true);
    }

    private List<Double> parseInputHours() throws Exception {
        List<Double> inputHours = new ArrayList<>();
        String inputText = inputHoursField.getText();
        String[] parsedInput = inputText.split("\n");
        for (String input:parsedInput) {
            try {
                inputHours.add(Double.parseDouble(input));
            } catch (NumberFormatException e) {
                throw new Exception("One or more input hours are bad!");
            }
        }
        return inputHours;
    }

    private int parseTotalPages() throws Exception {
        try {
            return Integer.parseInt(totalHoursField.getText());
        } catch (NumberFormatException e) {
            throw new Exception("Total number of pages are bad!");
        }
    }

    private void parseOutput(List<HoursAndPages> output) {
        outputField.selectAll();
        outputField.replaceSelection("");
        double totalHours = 0;
        int totalPages = 0;
        for (HoursAndPages hp:output) {
            totalHours += hp.getHours();
            totalPages += hp.getPages();
            String outputString = String.format("%.1f",hp.getHours()) + " , " + hp.getPages() + "\n";
            outputField.append(outputString);
        }
        String totalsString = String.format("%.1f", totalHours) + " , " + totalPages;
        outputTotals.setText(totalsString);
    }

    private class CalculateListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                timeBreaker.setTotalPages(parseTotalPages());
                timeBreaker.setInputHours(parseInputHours());
            } catch (Exception ex) {
                outputTotals.setText(ex.getMessage());
                return;
            }
            timeBreaker.run();
            parseOutput(timeBreaker.getOutputHoursAndPages());
        }
    }

}
