package com.inspiredo.latch;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Binds info about the steps to a ListView
 */
public class StepListAdapter extends ArrayAdapter<Step> implements AdapterView.OnItemClickListener {

    // Calls the super constructor
    public StepListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate view if needed
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_seq_step, null);
        }

        // Get the step
        Step s = getItem(position);

        if (s != null) {
            // Display the title
            TextView title = (TextView) convertView.findViewById(R.id.seq_step_title);
            if (title != null) {
                title.setText(s.toString());
            }

        }

        return convertView;
    }

    // Handles clicks: Toggles the completeness. If complete draws a line through and
    // makes the color gray
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Step s = getItem(position);
        TextView tv = (TextView) view.findViewById(R.id.seq_step_title);

        // Toggle completeness and act accordingly
        if (s.toggleComplete()) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv.setTextColor(getContext().getResources()
                    .getColor(android.R.color.darker_gray));
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            tv.setTextColor(getContext().getResources()
                    .getColor(android.R.color.black));
        }

    }
}
