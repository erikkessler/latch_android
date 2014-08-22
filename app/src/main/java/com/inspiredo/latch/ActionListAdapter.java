package com.inspiredo.latch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.NumberFormat;

/**
 * Binds an Array of actions to a view
 */
public class ActionListAdapter extends ArrayAdapter<Action> {

    /**
     * Constructor just calls the super constructor
     */
    public ActionListAdapter(MyLaunchActivity context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Inflate the view if needed
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_action, null);
        }

        final Action a  = getItem(position);
        NumberFormat format = NumberFormat.getCurrencyInstance();

        TextView title = (TextView) convertView.findViewById(R.id.action_title);
        title.setText(a.getName());

        final TextView count = (TextView) convertView.findViewById(R.id.action_count);
        count.setText(a.getCount() + "");

        TextView value = (TextView) convertView.findViewById(R.id.action_value);
        value.setText(format.format(a.getValue()));

        TextView increment = (TextView) convertView.findViewById(R.id.action_increment);
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count.setText(a.incrementCount() + "");
            }
        });

        TextView decrement = (TextView) convertView.findViewById(R.id.action_decrement);
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count.setText(a.decrementCount() + "");
            }
        });


        return convertView;
    }
}
