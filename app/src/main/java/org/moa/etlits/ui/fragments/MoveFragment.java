package org.moa.etlits.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.moa.etlits.R;

public class MoveFragment extends Fragment {
    private Fragment searchFragment;
    public MoveFragment() {

    }
    public static MoveFragment newInstance() {
        MoveFragment fragment = new MoveFragment();
       return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_move, container, false);
        searchFragment = new SearchFragment();
        getChildFragmentManager().beginTransaction().add(R.id.move_search_fragment, searchFragment, "search").commit();
        return v;
    }
}