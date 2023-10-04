package org.moa.etlits.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.databinding.FragmentAnimalRegMoveEventsBinding;
import org.moa.etlits.ui.activities.AnimalEntryActivity;
import org.moa.etlits.ui.adapters.EstablishmentAdapter;
import org.moa.etlits.ui.viewmodels.AnimalRegViewModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class AnimalRegMoveEventsFragment extends Fragment {

    private AnimalRegViewModel viewModel;
    private FragmentAnimalRegMoveEventsBinding binding;
    private EstablishmentAdapter establishmentAdapter;

    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
    public AnimalRegMoveEventsFragment() {

    }

   public static AnimalRegMoveEventsFragment newInstance() {
        AnimalRegMoveEventsFragment fragment = new AnimalRegMoveEventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnimalRegMoveEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        establishmentAdapter = new EstablishmentAdapter(getActivity(), new ArrayList<>());
        viewModel = new ViewModelProvider((ViewModelStoreOwner) getActivity(), (ViewModelProvider.Factory) new AnimalRegViewModel.Factory(getActivity().getApplication(), 0)).get(AnimalRegViewModel.class);
        viewModel.getEstablishments().observe(getActivity(), lst -> {
            Collections.sort(lst);
            establishmentAdapter.clear();
            establishmentAdapter.addAll(lst);
            establishmentAdapter.notifyDataSetChanged();
        });



        binding.actvMoveOffEid.setAdapter(establishmentAdapter);
        binding.actvMoveOffEid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem != null) {
                    String code = selectedItem.split("-")[0];
                    viewModel.setMoveOffEstablishment(code.trim());
                }
            }
        });

        binding.actvMoveOnEid.setAdapter(establishmentAdapter);
        binding.actvMoveOnEid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem != null) {
                    String code = selectedItem.split("-")[0];
                    viewModel.setMoveOnEstablishment(code.trim());
                }
            }
        });


        binding.btnDateMoveOn.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(), (dView, year, month, dayOfMonth) -> {
                viewModel.setDateMoveOn(year, month, dayOfMonth);
                binding.btnDateMoveOn.setText(dateFormat.format(viewModel.getDateMoveOn().getTime()));
            }, viewModel.getDateMoveOn().get(Calendar.YEAR), viewModel.getDateMoveOn().get(Calendar.MONTH), viewModel.getDateMoveOn().get(Calendar.DAY_OF_MONTH)).show();
        });
        binding.btnDateMoveOff.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(), (dView, year, month, dayOfMonth) -> {

                viewModel.setDateMoveOff(year, month, dayOfMonth);
                binding.btnDateMoveOff.setText(dateFormat.format(viewModel.getDateMoveOff().getTime()));
            }, viewModel.getDateMoveOff().get(Calendar.YEAR), viewModel.getDateMoveOff().get(Calendar.MONTH), viewModel.getDateMoveOff().get(Calendar.DAY_OF_MONTH)).show();
        });

        binding.btnDateIdentification.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(), (dView, year, month, dayOfMonth) -> {
                viewModel.setDateIdentification(year, month, dayOfMonth);
                binding.btnDateIdentification.setText(dateFormat.format(viewModel.getDateIdentification().getTime()));
            }, viewModel.getDateIdentification().get(Calendar.YEAR), viewModel.getDateIdentification().get(Calendar.MONTH), viewModel.getDateIdentification().get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}