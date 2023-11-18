package com.example.votingapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.votingapp.R;
import com.example.votingapp.adapter.LogsAdapter;
import com.example.votingapp.db.LogDbHelper;
import com.example.votingapp.utils.PrefHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogsFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button backButton;

    public LogsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogsFragment newInstance(String param1, String param2) {
        LogsFragment fragment = new LogsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_logs, container, false);
        PrefHelper prefHelper = new PrefHelper(getContext());
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        LogDbHelper logDbHelper = new LogDbHelper(getContext());
        LogsAdapter logsAdapter = new LogsAdapter(logDbHelper.findLogsByUserIdAndElectionCode(prefHelper.getPasscode().getElectionCode(), prefHelper.getUserName()));
        recyclerView.setAdapter(logsAdapter);
        backButton = v.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.re_entry);
            }
        });
        return v;
    }
}