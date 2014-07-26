package com.inspiredo.latch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Modal dialog for setting the Trigger
 */
public class TriggerDialog extends DialogFragment {

    // Use this instance of the interface to deliver action events
    TriggerDialogListener mListener;

    // the fragment initialization parameters
    private static final String ARG_SEQ = "seq_id";

    // Id of the owning Sequence
    private long mSeqId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param seqId Sequence of the owning id
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TriggerDialog newInstance(long seqId) {
        TriggerDialog fragment = new TriggerDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_SEQ, seqId);
        fragment.setArguments(args);
        return fragment;
    }
    public TriggerDialog() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the params
        if (getArguments() != null) {
            mSeqId = getArguments().getLong(ARG_SEQ);
        }

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogLayout = inflater.inflate(R.layout.dialog_trigger, null);
        builder.setView(dialogLayout)
                // Add action buttons
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        TimePicker time = (TimePicker) dialogLayout
                                .findViewById(R.id.tigger_timepicker);
                        CheckBox alarm = (CheckBox) dialogLayout
                                .findViewById(R.id.trigger_alarm_cb);

                        // Get the time
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, time.getCurrentHour());
                        cal.set(Calendar.MINUTE, time.getCurrentMinute());
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        Date d = cal.getTime();

                        // Get the type
                        int type = Trigger.NOTIFICATION;
                        if (alarm.isChecked()) {
                            type = Trigger.ALARM;
                        }

                        // Create and return the Trigger
                        Trigger t = new Trigger(d, type);
                        t.setSequenceId(mSeqId);
                        mListener.onDialogPositiveClick(t);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TriggerDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (TriggerDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement TriggerDialogListener");
        }
    }

    public interface  TriggerDialogListener {
        public void onDialogPositiveClick(Trigger t);
    }
}
