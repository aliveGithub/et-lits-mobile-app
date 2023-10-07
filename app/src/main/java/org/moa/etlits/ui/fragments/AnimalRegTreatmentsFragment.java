package org.moa.etlits.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.models.Treatment;
import org.moa.etlits.databinding.FragmentAnimalRegTreatmentsBinding;
import org.moa.etlits.ui.viewmodels.AnimalRegViewModel;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

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
        viewModel = new ViewModelProvider((ViewModelStoreOwner) getActivity(), (ViewModelProvider.Factory) new AnimalRegViewModel.Factory(getActivity().getApplication(), 0)).get(AnimalRegViewModel.class);

        viewModel.getTreatmentList().observe(getActivity(), treatments ->  {
            if(treatments != null){
                for (Treatment t : treatments) {
                    Log.i("Treatment", "---------------------" + t.getTreatmentApplied());



                }

            }});

        viewModel.getTreatmentTypeList().observe(getActivity(), treatmentTypes -> {
            if (treatmentTypes != null) {
                List<String> selected = viewModel.getSelectedTreatmentTypes();
                for (int i = 0; i < treatmentTypes.size(); i++) {
                    CategoryValue treatmentType = treatmentTypes.get(i);
                    CheckBox checkBox = new CheckBox(getActivity());
                    checkBox.setTag(treatmentType.getValueId());
                    checkBox.setText(treatmentType.getValue());
                    checkBox.setChecked(selected.contains(treatmentType.getValueId()));
                    checkBox.setPadding(8, 16, 8, 16);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            String id = (String) buttonView.getTag();
                            List<Treatment> treatments = viewModel.getTreatments();
                            if (isChecked) {
                                Treatment treatment = new Treatment();
                                treatment.setTreatmentApplied(id);
                               treatments.add(treatment);
                            } else {
                                    for(int i = 0; i < treatments.size(); i++){
                                    if(treatments.get(i).getTreatmentApplied().equals(id)){
                                        treatments.remove(i);
                                        break;
                                    }
                                }
                            }
                            Log.i("Treatment", "---------------------" + viewModel.getTreatmentList().getValue().size());
                        }

                    });
                    binding.llTreatmentList.addView(checkBox);
                }
            }

        });

       binding.btnFinishRegistration.setOnClickListener(v -> {
           viewModel.save();
           getActivity().finish();
        });

   }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}