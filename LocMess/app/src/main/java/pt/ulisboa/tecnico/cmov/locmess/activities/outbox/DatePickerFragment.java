package pt.ulisboa.tecnico.cmov.locmess.activities.outbox;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);

        if(getTag().equals("dateStartPicker")) {
            dialog.setTitle("Message posting time");
            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
            Toast.makeText(getActivity(), "Pick message posting time.", Toast.LENGTH_SHORT).show();
        }else{
            dialog.setTitle("Message expiration time");
            c.set(((PostMessageActivity) getActivity()).timeWindow.getStartYear(),
                    ((PostMessageActivity) getActivity()).timeWindow.getStartMonth(),
                    ((PostMessageActivity) getActivity()).timeWindow.getStartDay());
            dialog.getDatePicker().setMinDate(c.getTimeInMillis());
            Toast.makeText(getActivity(), "Pick message expiration time.", Toast.LENGTH_SHORT).show();
        }

        // Create a new instance of DatePickerDialog and return it
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        DialogFragment dialogFragment;
        if(getTag().equals("dateStartPicker")){
            ((PostMessageActivity) getActivity()).timeWindow.setStartDate(day, month, year);
            dialogFragment = new TimePickerFragment();
            dialogFragment.show(this.getFragmentManager(), "timeStartPicker");
        }else{
            ((PostMessageActivity) getActivity()).timeWindow.setEndDate(day, month, year);
            dialogFragment = new TimePickerFragment();
            dialogFragment.show(this.getFragmentManager(), "timeEndPicker");
        }

    }
}