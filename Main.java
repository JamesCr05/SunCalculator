package com.example;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Date:");
        String date = input.nextLine();
        System.out.println("Latitude:");
        double latitude = input.nextDouble();
        System.out.println("Longitude:");
        double longitude = input.nextDouble();
        System.out.println("Time zone:");
        double timeZone = input.nextDouble();
        Calculator calculator = new Calculator(date, latitude, longitude, timeZone);
        System.out.println("Sunrise: " + calculator.timeToString(calculator.getSunrise()));
        System.out.println("Solar noon: " + calculator.timeToString(calculator.getSolarNoon()));
        System.out.println("Sunset: " + calculator.timeToString(calculator.getSunset()));
    }
}