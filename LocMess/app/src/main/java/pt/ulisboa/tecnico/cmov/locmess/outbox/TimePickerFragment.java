package pt.ulisboa.tecnico.cmov.locmess.outbox;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import pt.ulisboa.tecnico.cmov.locmess.R;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user

        DialogFragment dialogFragment;

        if(getTag().equals("timeStartPicker")){
            ((NewMessageActivity) getActivity()).getTimeWindow().setStartTime(hourOfDay, minute);
            dialogFragment = new DatePickerFragment();
            dialogFragment.show(this.getFragmentManager(), "dateEndPicker");
        }else {
            ((NewMessageActivity) getActivity()).getTimeWindow().setEndTime(hourOfDay, minute);
            ((NewMessageActivity) getActivity()).getTimeWindow().setTimeWindowSet(true);
            ((NewMessageActivity) getActivity()).refreshButtons();

            Toast.makeText(getActivity(),
                    "Message Duration\n"
                            + "From: " + ((NewMessageActivity) getActivity()).getTimeWindow().getFormattedStartTime() + "\n"
                            + "To: " + ((NewMessageActivity) getActivity()).getTimeWindow().getFormattedEndTime(),
                    Toast.LENGTH_LONG).show();

        }

    }
}
