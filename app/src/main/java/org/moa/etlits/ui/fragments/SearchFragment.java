package org.moa.etlits.ui.fragments;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import org.moa.etlits.R;
import org.moa.etlits.ui.activities.EstablishmentSummaryActivity;
import org.moa.etlits.ui.activities.MainActivity;
import org.moa.etlits.ui.adapters.EstablishmentAdapter;
import org.moa.etlits.ui.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class SearchFragment extends Fragment {

    private AutoCompleteTextView autoCompleteTextView;
    private EstablishmentAdapter establishmentAdapter;
    private SearchViewModel searchViewModel;

    public SearchFragment() {
    }

     public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_search, container, false);

        autoCompleteTextView = view.findViewById(R.id.acv_establishment_search);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        establishmentAdapter = new EstablishmentAdapter((MainActivity) getActivity(), new ArrayList<>());
        autoCompleteTextView.setAdapter(establishmentAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem != null) {
                    String code = selectedItem.split("-")[0];
                    Intent intent = new Intent(getActivity(), EstablishmentSummaryActivity.class);
                    intent.putExtra("code", code.trim());
                    startActivity(intent);
                }
            }
        });

        searchViewModel = new SearchViewModel((Application) getActivity().getApplicationContext());
        searchViewModel.getEstablishments().observe(getActivity(), lst -> {
            Collections.sort(lst);
            establishmentAdapter.clear();
            establishmentAdapter.addAll(lst);
            establishmentAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        autoCompleteTextView.setText("");
    }
}