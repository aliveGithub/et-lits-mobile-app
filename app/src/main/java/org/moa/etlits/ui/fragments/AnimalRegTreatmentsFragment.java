package org.moa.etlits.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.databinding.FragmentAnimalRegTreatmentsBinding;
import org.moa.etlits.ui.activities.AnimalRegActivity;
import org.moa.etlits.ui.viewmodels.AnimalRegViewModel;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import static android.app.Activity.RESULT_OK;

public class AnimalRegTreatmentsFragment extends Fragment {
    private AnimalRegViewModel viewModel;
    private FragmentAnimalRegTreatmentsBinding binding;

    public AnimalRegTreatmentsFragment() {
    }

    public static AnimalRegTreatmentsFragment newInstance() {
        return new AnimalRegTreatmentsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnimalRegTreatmentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AnimalRegViewModel.class);

        viewModel.getTreatmentTypeList().observe(getActivity(), treatmentTypes -> {
            if (treatmentTypes != null) {
                List<String> selected = viewModel.getSelectedTreatmentTypes();
                for (int i = 0; i < treatmentTypes.size(); i++) {
                    binding.llTreatmentList.addView(createTreatmentChecBox(treatmentTypes.get(i), selected));
                }
            }
        });

        viewModel.getTreatmentList().observe(getActivity(), treatments -> {
            if (treatments != null) {
                for (int i = 0; i < treatments.size(); i++) {
                    CheckBox checkBox = binding.llTreatmentList.findViewWithTag(treatments.get(i).getTreatmentApplied());
                    if (checkBox != null && !checkBox.isChecked()) {
                        checkBox.setChecked(true);
                    }
                }
            }
        });

        binding.btnFinishRegistration.setOnClickListener(v -> {
            viewModel.save();
            Intent replyIntent = new Intent();
            int registeredAnimals = viewModel.getAnimals().getValue() != null ? viewModel.getAnimals().getValue().size() : 0;
            replyIntent.putExtra(AnimalRegActivity.REGISTERED_ANIMALS, registeredAnimals);
            getActivity().setResult(RESULT_OK, replyIntent);
            getActivity().finish();
        });

    }

    private CheckBox createTreatmentChecBox(CategoryValue treatmentType, List<String> selected) {
        CheckBox checkBox = new CheckBox(getActivity());
        checkBox.setTag(treatmentType.getValueId());
        checkBox.setText(treatmentType.getValue());
        checkBox.setChecked(selected.contains(treatmentType.getValueId()));
        checkBox.setPadding(8, 16, 8, 16);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    String id = (String) buttonView.getTag();
                    if (isChecked) {
                        viewModel.addTreatment(id);
                    } else {
                        viewModel.removeTreatment(id);
                    }
                }
            }
        });

        return checkBox;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}