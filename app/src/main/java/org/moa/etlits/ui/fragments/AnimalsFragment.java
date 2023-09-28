package org.moa.etlits.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.moa.etlits.R;
import org.moa.etlits.ui.activities.AnimalRegActivity;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class AnimalsFragment extends Fragment {
    private CardView registerAnimal;
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
        getChildFragmentManager().beginTransaction().add(R.id.animals_search_fragment, searchFragment, "search_animals").commit();
        registerAnimal = v.findViewById(R.id.card_register);
        registerAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AnimalRegActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

}