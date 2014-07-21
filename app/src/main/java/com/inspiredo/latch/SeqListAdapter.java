package com.inspiredo.latch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Binds an Array of sequences to a view
 */
public class SeqListAdapter extends ArrayAdapter<Sequence>{
    public SeqListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_seq, null);
        }

        Sequence s = getItem(position);

        if (s != null) {
            TextView title = (TextView) convertView.findViewById(R.id.seq_title);
            if (title != null) {
                title.setText(s.getTitle());
            }

            ListView steps = (ListView) convertView.findViewById(R.id.seq_step_list);
            if (steps != null) {
                StepListAdapter a =
                        new StepListAdapter(getContext(), R.layout.row_seq_step);
                a.addAll(s.getSteps());
                steps.setAdapter(a);
            }

            TextView reward = (TextView) convertView.findViewById(R.id.seq_reward);
            if (reward != null) {
                reward.setText(s.getReward());
            }


        }

        return convertView;
    }
}
