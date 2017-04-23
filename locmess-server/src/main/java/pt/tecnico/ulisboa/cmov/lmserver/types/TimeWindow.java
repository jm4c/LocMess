package pt.tecnico.ulisboa.cmov.lmserver.types;

/**
 * Created by joaod on 12-Apr-17.
 */

public class TimeWindow {
    private boolean isTimeWindowSet;

    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;

    private int endYear;
    private int endMonth;
    private int endDay;
    private int endHour;
    private int endMinute;

    public TimeWindow() {
        this.isTimeWindowSet = false;
    }

    public boolean isTimeWindowSet() {
        return isTimeWindowSet;
    }


    public void setTimeWindowSet(boolean timeWindowSet) {
        isTimeWindowSet = timeWindowSet;
    }

    public void setStartDate(int startDay, int startMonth, int startYear){
        this.startDay = startDay;
        this.startMonth = startMonth;
        this.startYear = startYear;
    }

    public void setStartTime(int startHour, int startMinute){
        this.startHour = startHour;
        this.startMinute = startMinute;
    }

    public void setEndDate(int endDay, int endMonth, int endYear){
        this.endDay = endDay;
        this.endMonth = endMonth;
        this.endYear = endYear;
    }

    public void setEndTime(int endHour, int endMinute){
        this.endHour = endHour;
        this.endMinute = startMinute;
    }

    public int getStartYear() {
        return startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getStartDay() {
        return startDay;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndYear() {
        return endYear;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public int getEndDay() {
        return endDay;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public String getFormattedStartTime(){
        return String.valueOf(startDay) + "/" +
                String.valueOf(startMonth) + "/" +
                String.valueOf(startYear) + " " +
                String.valueOf(startHour) + ":" +
                String.valueOf(startMinute);
    }

    public String getFormattedEndTime(){
        return String.valueOf(endDay) + "/" +
                String.valueOf(endMonth) + "/" +
                String.valueOf(endYear) + " " +
                String.valueOf(endHour) + ":" +
                String.valueOf(endMinute);
    }
}
