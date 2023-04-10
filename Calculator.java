package com.example;

public class Calculator {
    private String date;
    private double latitude;
    private double longitude;
    private double timeZone;

    public Calculator(String date, double latitude, double longitude, double timeZone) {
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeZone = timeZone;
    }

    public int getDayOfYear() {
        int day = Integer.parseInt(date.substring(0, date.length() - 9));
        String month = date.substring(date.length() - 8, date.length() - 5);
        int year = Integer.parseInt(date.substring(date.length() - 4, date.length()));
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int[] days = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
        for (int i = 0; i < months.length; i++) {
            if (month.equals(months[i])) {
                day += days[i];
            }
        }
        if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0) && !month.equals("Jan") && !month.equals("Feb")) {
            day++;
        }
        return day;
    }

    public int getDaysInYear() {
        Calculator lastDay = new Calculator("31 Dec " + date.substring(date.length() - 4, date.length()), 0, 0, 0);
        return lastDay.getDayOfYear();
    }

    public double getDeclination() {
        double dayAngle = 2 * Math.PI * getDayOfYear() / getDaysInYear();
        return (0.006918 - 0.399912 * Math.cos(dayAngle) + 0.070257 * Math.sin(dayAngle) - 0.006758 * Math.cos(2 * dayAngle) + 0.000907 * Math.sin(2 * dayAngle) - 0.002697 * Math.cos(3 * dayAngle) + 0.001480 * Math.sin(3 * dayAngle)) * 180 / Math.PI;
    }

    public double getDayLength() {
        if (-Math.tan(latitude * Math.PI / 180) * Math.tan(getDeclination() * Math.PI / 180) >= 1) {
            return 0;
        } else if (-Math.tan(latitude * Math.PI / 180) * Math.tan(getDeclination() * Math.PI / 180) <= -1) {
            return 24;
        } else {
            return 24 / Math.PI * Math.acos(-Math.tan(latitude * Math.PI / 180) * Math.tan(getDeclination() * Math.PI / 180));
        }
    }

    public double getEquationOfTime() {
        double dayAngle = (double) getDayOfYear() / getDaysInYear() * 2 * Math.PI;
        return (0.00037 + 0.43177 * Math.cos(dayAngle) - 7.3464 * Math.sin(dayAngle) - 3.165 * Math.cos(2 * dayAngle) - 9.3893 * Math.sin(2 * dayAngle) + 0.07272 * Math.cos(3 * dayAngle) - 0.24498 * Math.sin(3 * dayAngle)) / 60;
    }

    public double getSolarNoon() {
        return 12 - getEquationOfTime() + timeZone - longitude / 15;
    }

    public double getSunrise() {
        return getSolarNoon() - getDayLength() / 2;
    }

    public double getSunset() {
        return getSolarNoon() + getDayLength() / 2;
    }

    public String timeToString(double hours) {
        while (hours < 0) {
            hours += 24;
        }
        hours = hours % 24;
        return String.format("%02d", (int) hours) + ":" + String.format("%02d", Math.round(((hours - (int) hours) * 60)));
    }
}