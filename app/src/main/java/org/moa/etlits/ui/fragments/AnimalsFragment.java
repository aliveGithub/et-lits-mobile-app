package org.moa.etlits.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.moa.etlits.R;

public class AnimalsFragment extends Fragment {
    private Fragment searchFragment;
    public AnimalsFragment() {
    }

  public static AnimalsFragment newInstance() {
        AnimalsFragment fragment = new AnimalsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_animals, container, false);
        searchFragment = new SearchFragment();
        getChildFragmentManager().beginTransaction().add(R.id.animals_search_fragment, searchFragment, "search").commit();
        return v;
    }

}