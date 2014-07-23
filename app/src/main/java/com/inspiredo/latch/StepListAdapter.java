package com.inspiredo.latch;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Binds info about the steps to a ListView
 */
public class StepListAdapter extends ArrayAdapter<Step> implements AdapterView.OnItemClickListener {

    Context mContext;

    // Calls the super constructor
    public StepListAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
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
                setView(title, s.isComplete());
            }

        }

        return convertView;
    }

    private void setView(TextView tv, boolean complete) {
        if (complete) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv.setTextColor(getContext().getResources()
                    .getColor(android.R.color.darker_gray));
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            tv.setTextColor(getContext().getResources()
                    .getColor(android.R.color.black));
        }
    }

    // Handles clicks: Toggles the completeness. If complete draws a line through and
    // makes the color gray
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Step s = getItem(position);
        TextView tv = (TextView) view.findViewById(R.id.seq_step_title);

        // Toggle completeness and act accordingly
        StepDataSource dataSource = new StepDataSource(mContext);
        dataSource.open();
        if (s.toggleComplete()) {
            // Step complete
            setView(tv, true);
            dataSource.completeStep(s, true);
        } else {
            // Step incomplete
            setView(tv, false);
            dataSource.completeStep(s, false);
        }
        dataSource.close();
    }
}
