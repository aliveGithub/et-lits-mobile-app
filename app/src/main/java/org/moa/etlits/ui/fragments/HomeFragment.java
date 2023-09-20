package org.moa.etlits.ui.fragments;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import org.moa.etlits.R;
import org.moa.etlits.data.repositories.EstablishmentRepository;
import org.moa.etlits.ui.activities.MainActivity;
import org.moa.etlits.ui.adapters.EstablishmentAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private AutoCompleteTextView autoCompleteTextView;
    private EstablishmentAdapter establishmentAdapter;
    public HomeFragment() {

    }

     public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        establishmentAdapter = new EstablishmentAdapter((MainActivity) getActivity(), new ArrayList<>());
        autoCompleteTextView = ((MainActivity) getActivity()).findViewById(R.id.acv_establishment_search);

        autoCompleteTextView.setAdapter(establishmentAdapter);;
        EstablishmentRepository establishmentRepository = new EstablishmentRepository((Application) getActivity().getApplicationContext());
        establishmentRepository.getAll().observe((MainActivity) getActivity(), lst -> {
            establishmentAdapter.clear();
            establishmentAdapter.addAll(lst);
            establishmentAdapter.notifyDataSetChanged();
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });

    }
}