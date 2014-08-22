package com.inspiredo.latch;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class GoalFragment extends Fragment {


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment GoalFragment.
     */
    public static GoalFragment newInstance() {
        return new GoalFragment();
    }
    public GoalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal, container, false);
    }


}
