package com.example;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Year:");
        int year = input.nextInt();
        System.out.println("Month:");
        int month = input.nextInt();
        System.out.println("Day:");
        int day = input.nextInt();
        System.out.println("Latitude:");
        double latitude = input.nextDouble();
        System.out.println("Longitude:");
        double longitude = input.nextDouble();
        System.out.println("Time zone:");
        double timeZone = input.nextDouble();
        SubsolarPointAlgorithm calculator = new SubsolarPointAlgorithm();
        calculator.yearToCSV(year, latitude, longitude, timeZone);
        System.out.println(calculator.toString(year, month, day, latitude, longitude, timeZone, true));
    }
}