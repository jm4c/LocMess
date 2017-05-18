package pt.ulisboa.tecnico.cmov.locmess.activities.outbox;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = null;

        if (getTag().equals("dateStartPicker")) {
            dialog = new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        } else {
            hour = ((PostMessageActivity) getActivity()).timeWindow.getStartHour();
            minute = ((PostMessageActivity) getActivity()).timeWindow.getStartMinute();
            dialog = new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        // Create a new instance of TimePickerDialog and return it
        return dialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user

        DialogFragment dialogFragment;

        if (getTag().equals("timeStartPicker")) {

            Calendar c = Calendar.getInstance();

            int today = c.get(Calendar.DAY_OF_MONTH);
            int currentMonth = c.get(Calendar.MONTH);
            int currentYear = c.get(Calendar.YEAR);

            //if date set before is today
            if (((PostMessageActivity) getActivity()).timeWindow.getStartDay() == today &&
                    ((PostMessageActivity) getActivity()).timeWindow.getStartMonth() == currentMonth &&
                    ((PostMessageActivity) getActivity()).timeWindow.getStartYear() == currentYear) {
                int currentHour = c.get(Calendar.HOUR_OF_DAY);
                int currentMinute = c.get(Calendar.MINUTE);
                // time set must be after current time, if it isn't set time to NOW
                if (hourOfDay < currentHour || (hourOfDay == currentHour && minute < currentMinute)) {
                    hourOfDay = currentHour;
                    minute = currentMinute;
                }
            }

            ((PostMessageActivity) getActivity()).timeWindow.setStartTime(hourOfDay, minute);
            dialogFragment = new DatePickerFragment();
            dialogFragment.show(this.getFragmentManager(), "dateEndPicker");
        } else {

            Calendar c = Calendar.getInstance();

            int startDay = ((PostMessageActivity) getActivity()).timeWindow.getStartDay();
            int startMonth =((PostMessageActivity) getActivity()).timeWindow.getStartMonth();
            int startYear = ((PostMessageActivity) getActivity()).timeWindow.getStartYear();

            int endDay = ((PostMessageActivity) getActivity()).timeWindow.getEndDay();
            int endMonth = ((PostMessageActivity) getActivity()).timeWindow.getEndMonth();
            int endYear = ((PostMessageActivity) getActivity()).timeWindow.getEndYear();

            //if date set before is today
            if (endDay == startDay &&
                    endMonth == startMonth &&
                    endYear == startYear) {

                int startHour = ((PostMessageActivity) getActivity()).timeWindow.getStartHour();
                int startMinute = ((PostMessageActivity) getActivity()).timeWindow.getStartMinute();

                // time set must be after start time, if it isn't set time to start time
                if (hourOfDay < startHour || (hourOfDay == startHour && minute < startMinute)) {
                    hourOfDay = startHour;
                    minute = startMinute;
                }
            }

            ((PostMessageActivity) getActivity()).timeWindow.setEndTime(hourOfDay, minute);
            ((PostMessageActivity) getActivity()).timeWindow.setTimeWindowSet(true);
            ((PostMessageActivity) getActivity()).refreshButtons();

            Toast.makeText(getActivity(),
                    "Message Duration\n"
                            + "From: " + ((PostMessageActivity) getActivity()).timeWindow.printFormattedStartTime() + "\n"
                            + "To: " + ((PostMessageActivity) getActivity()).timeWindow.printFormattedEndTime(),
                    Toast.LENGTH_LONG).show();

        }

    }
}
