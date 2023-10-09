package org.moa.etlits.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.moa.etlits.R;
import org.moa.etlits.ui.activities.AnimalRegActivity;
import org.moa.etlits.ui.activities.AnimalRegListActivity;
import org.moa.etlits.utils.Constants;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class AnimalsFragment extends Fragment {
    private CardView registerAnimal;
    private CardView viewRegistrations;
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
        if (savedInstanceState == null) {
            searchFragment = new SearchFragment();
            getChildFragmentManager().beginTransaction().add(R.id.animals_search_fragment, searchFragment, "search_animals").commit();
        }

        registerAnimal = v.findViewById(R.id.card_register);
        viewRegistrations = v.findViewById(R.id.card_view_registrations);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String defaultEstablishment = sharedPreferences.getString(Constants.DEFAULT_ESTABLISHMENT, "");

       /* if (defaultEstablishment.isEmpty()) {
            registerAnimal.setVisibility(View.GONE);
            viewRegistrations.setVisibility(View.GONE);
        } else {
            registerAnimal.setVisibility(View.VISIBLE);
            viewRegistrations.setVisibility(View.VISIBLE);
        }*/

        registerAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AnimalRegActivity.class);
                startActivity(intent);
            }
        });

        viewRegistrations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AnimalRegListActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }
}