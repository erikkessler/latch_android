package com.inspiredo.latch;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Binds an Array of sequences to a view
 */
public class SeqListAdapter extends ArrayAdapter<Sequence>{

    StepListAdapter mStepAdapter;

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
                mStepAdapter =
                        new StepListAdapter(getContext(), R.layout.row_seq_step);
                mStepAdapter.addAll(s.getSteps());
                final float scale = getContext().getResources().getDisplayMetrics().density;
                steps.getLayoutParams().height = (int) (
                        (56 * scale + 0.5f) * mStepAdapter.getCount());
                steps.setAdapter(mStepAdapter);
                steps.setOnItemClickListener(new CompleteOnClickListener());
            }

            TextView reward = (TextView) convertView.findViewById(R.id.seq_reward);
            if (reward != null) {
                reward.setText(s.getReward());
            }


        }

        return convertView;
    }

    public class CompleteOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Step s = mStepAdapter.getItem(position);
            TextView tv = (TextView) view.findViewById(R.id.seq_step_title);
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

}
