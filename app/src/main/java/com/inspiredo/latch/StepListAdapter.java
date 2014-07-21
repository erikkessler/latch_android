package com.inspiredo.latch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by erik on 7/20/14.
 */
public class StepListAdapter extends ArrayAdapter<Step> {
    public StepListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(":djkf", position + "/" + getCount() );


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_seq_step, null);
        }

        Step s = getItem(position);

        if (s != null) {
            TextView title = (TextView) convertView.findViewById(R.id.seq_step_title);
            if (title != null) {
                title.setText(s.toString());
            }

        }

        return convertView;
    }

}
