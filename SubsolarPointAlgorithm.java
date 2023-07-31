package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

class SubsolarPointAlgorithm {
    public SubsolarPointAlgorithm() {
    }

    public double[] getCoordinates(int year, int month, int day, int hour, int minute) {
        double a = hour + (double) minute / 60;
        double b = 367 * year - Math.floor(7 * (year + Math.floor((double) (month + 9) / 12)) / 4);
        double c = b + Math.floor(275 * (double) month / 9) + day - 730531.5 + a / 24;
        double d = c * 0.01720279239 + 4.894967873;
        double e = c * 0.01720197034 + 6.240040768;
        b = d + 0.03342305518 * Math.sin(e);
        double f = b + 0.0003490658504 * Math.sin(2 * e);
        double g = 0.4090877234 - 0.000000006981317008 * c;
        double h = 4.894961213 + 6.300388099 * c;
        double i = Math.atan2(Math.cos(g) * Math.sin(f), Math.cos(f));
        double j = Math.asin(Math.sin(g) * Math.sin(f));
        double[] coordinates = new double[2];
        coordinates[0] = Math.toDegrees(j);
        coordinates[1] = Math.toDegrees(i - h) % 360;
        if (coordinates[1] <= -180) {
            coordinates[1] += 360;
        }
        if (coordinates[1] > 180) {
            coordinates[1] -= 360;
        }
        return coordinates;
    }

    public double getElevation(int year, int month, int day, int hour, int minute, double latitude, double longitude) {
        double sunLatitude = getCoordinates(year, month, day, hour, minute)[0];
        double sunLongitude = getCoordinates(year, month, day, hour, minute)[1];
        double distance = Math.acos(Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(sunLatitude)) + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(sunLatitude)) * Math.cos(Math.toRadians(sunLongitude - longitude)));
        return 90 - Math.toDegrees(distance);
    }

    LocalDateTime getSolarNoon(int year, int month, int day, double latitude, double longitude, double timeZone) {
        LocalDateTime startTime = LocalDateTime.of(year, month, day, 0, 0).minusMinutes(Math.round(longitude * 4));
        if (timeZone * 60 - longitude * 4 > 720) {
            startTime = startTime.minusDays(1);
        }
        if (timeZone * 60 - longitude * 4 < -720) {
            startTime = startTime.plusDays(1);
        }
        LocalDateTime solarNoon = startTime;
        for (LocalDateTime time = startTime; time.isBefore(startTime.plusDays(1)); time = time.plusMinutes(1)) {
            if (getElevation(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), time.getHour(), time.getMinute(), latitude, longitude) > getElevation(solarNoon.getYear(), solarNoon.getMonthValue(), solarNoon.getDayOfMonth(), solarNoon.getHour(), solarNoon.getMinute(), latitude, longitude)) {
                solarNoon = time;
            }
        }
        return solarNoon.plusMinutes(Math.round(timeZone * 60));
    }

    LocalDateTime getSunrise(int year, int month, int day, double latitude, double longitude, double timeZone, double elevation) {
        LocalDateTime solarNoon = getSolarNoon(year, month, day, latitude, longitude, timeZone);
        solarNoon = solarNoon.minusMinutes(Math.round(timeZone * 60));
        LocalDateTime sunrise = solarNoon;
        for (LocalDateTime time = solarNoon.minusHours(12); time.isBefore(solarNoon); time = time.plusMinutes(1)) {
            if (Math.abs(getElevation(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), time.getHour(), time.getMinute(), latitude, longitude) - elevation) < Math.abs(getElevation(sunrise.getYear(), sunrise.getMonthValue(), sunrise.getDayOfMonth(), sunrise.getHour(), sunrise.getMinute(), latitude, longitude) - elevation)) {
                sunrise = time;
            }
        }
        return sunrise.plusMinutes(Math.round(timeZone * 60));
    }

    LocalDateTime getSunset(int year, int month, int day, double latitude, double longitude, double timeZone, double elevation) {
        LocalDateTime solarNoon = getSolarNoon(year, month, day, latitude, longitude, timeZone);
        solarNoon = solarNoon.minusMinutes(Math.round(timeZone * 60));
        LocalDateTime sunset = solarNoon;
        for (LocalDateTime time = solarNoon.plusHours(12); time.isAfter(solarNoon); time = time.minusMinutes(1)) {
            if (Math.abs(getElevation(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), time.getHour(), time.getMinute(), latitude, longitude) - elevation) < Math.abs(getElevation(sunset.getYear(), sunset.getMonthValue(), sunset.getDayOfMonth(), sunset.getHour(), sunset.getMinute(), latitude, longitude) - elevation)) {
                sunset = time;
            }
        }
        return sunset.plusMinutes(Math.round(timeZone * 60));
    }

    String timeToString(LocalDateTime time, boolean twelveHourFormat) {
        if (!twelveHourFormat) {
            return time.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else {
            return time.format(DateTimeFormatter.ofPattern("h:mm a"));
        }
    }

    String sunriseSunsetToString(LocalDateTime time, double latitude, double longitude, double timeZone, double elevation, boolean twelveHourFormat) {
        time = time.minusMinutes(Math.round(timeZone * 60));
        if ((getElevation(time.plusMinutes(1).getYear(), time.plusMinutes(1).getMonthValue(), time.plusMinutes(1).getDayOfMonth(), time.plusMinutes(1).getHour(), time.plusMinutes(1).getMinute(), latitude, longitude) > elevation) != (getElevation(time.minusMinutes(1).getYear(), time.minusMinutes(1).getMonthValue(), time.minusMinutes(1).getDayOfMonth(), time.minusMinutes(1).getHour(), time.minusMinutes(1).getMinute(), latitude, longitude) > elevation)) {
            time = time.plusMinutes(Math.round(timeZone * 60));
            if (!twelveHourFormat) {
                return time.format(DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                return time.format(DateTimeFormatter.ofPattern("h:mm a"));
            }
        } else {
            return "";
        }
    }

    public String toString(int year, int month, int day, double latitude, double longitude, double timeZone, boolean twelveHourFormat) {
        return "Astronomical sunrise - " + sunriseSunsetToString(getSunrise(year, month, day, latitude, longitude, timeZone, -18), latitude, longitude, timeZone, -18, twelveHourFormat) + "\nNautical sunrise - " + sunriseSunsetToString(getSunrise(year, month, day, latitude, longitude, timeZone, -12), latitude, longitude, timeZone, -12, twelveHourFormat) + "\nCivil sunrise - " + sunriseSunsetToString(getSunrise(year, month, day, latitude, longitude, timeZone, -6), latitude, longitude, timeZone, -6, twelveHourFormat) + "\nSunrise - " + sunriseSunsetToString(getSunrise(year, month, day, latitude, longitude, timeZone, (double) -5 / 6), latitude, longitude, timeZone, (double) -5 / 6, twelveHourFormat) + "\nSolar noon - " + timeToString(getSolarNoon(year, month, day, latitude, longitude, timeZone), twelveHourFormat) + "\nSunset - " + sunriseSunsetToString(getSunset(year, month, day, latitude, longitude, timeZone, (double) -5 / 6), latitude, longitude, timeZone, (double) -5 / 6, twelveHourFormat) + "\nCivil sunset - " + sunriseSunsetToString(getSunset(year, month, day, latitude, longitude, timeZone, -6), latitude, longitude, timeZone, -6, twelveHourFormat) + "\nNautical sunset - " + sunriseSunsetToString(getSunset(year, month, day, latitude, longitude, timeZone, -12), latitude, longitude, timeZone, -12, twelveHourFormat) + "\nAstronomical sunset - " + sunriseSunsetToString(getSunset(year, month, day, latitude, longitude, timeZone, -18), latitude, longitude, timeZone, -18, twelveHourFormat);
    }

    public void yearToCSV(int year, double latitude, double longitude, double timeZone) {
        for (int month = 1; month <= 12; month++) {
            for (int day = 1; day <= (new GregorianCalendar(year, month - 1, 1)).getActualMaximum(Calendar.DAY_OF_MONTH); day++) {
                System.out.println(String.format("%04d", year) + "/" + String.format("%02d", month) + "/" + String.format("%02d", day) + ", " + sunriseSunsetToString(getSunrise(year, month, day, latitude, longitude, timeZone, (double) -5 / 6), latitude, longitude, timeZone, (double) -5 / 6, false) + ", " + sunriseSunsetToString(getSunset(year, month, day, latitude, longitude, timeZone, (double) -5 / 6), latitude, longitude, timeZone, (double) -5 / 6, false));
            }
        }
    }
}
