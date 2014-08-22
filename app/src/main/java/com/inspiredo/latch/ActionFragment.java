package com.inspiredo.latch;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ActionFragment extends Fragment {

    // Context of activity attached to
    private MyLaunchActivity mActivity;

    // Root view
    private View mRootView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ActionFragment.
     */
    public static ActionFragment newInstance() {
        return new ActionFragment();
    }
    public ActionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MyLaunchActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_action, container, false);

        ListView actionList = (ListView) mRootView.findViewById(R.id.action_list);

        ActionListAdapter listAdapter = new ActionListAdapter(mActivity, R.layout.row_action);
        listAdapter.add(new Action("Avert Pick",(float) 0.25));
        listAdapter.add(new Action("Pick Free Day", (float) 2));
        listAdapter.add(new Action("Dessert Free", (float) 2));

        actionList.setAdapter(listAdapter);
        return mRootView;
    }


}
