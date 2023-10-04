package org.moa.etlits.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.databinding.FragmentAnimalRegAddAnimalsBinding;
import org.moa.etlits.ui.activities.AnimalEntryActivity;
import org.moa.etlits.ui.adapters.AnimalListAdapter;
import org.moa.etlits.ui.viewmodels.AnimalRegViewModel;

import static android.app.Activity.RESULT_OK;


public class AnimalRegAddAnimalsFragment extends Fragment implements AnimalListAdapter.AnimalItemEventsListener {
    private AnimalRegViewModel viewModel;
    private AnimalListAdapter adapter;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private FragmentAnimalRegAddAnimalsBinding binding;

    public AnimalRegAddAnimalsFragment() {
        // Required empty public constructor
    }

    public static AnimalRegAddAnimalsFragment newInstance() {
        AnimalRegAddAnimalsFragment fragment = new AnimalRegAddAnimalsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnimalRegAddAnimalsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) getActivity(), (ViewModelProvider.Factory) new AnimalRegViewModel.Factory(getActivity().getApplication(), 0)).get(AnimalRegViewModel.class);

        adapter = new AnimalListAdapter(new AnimalListAdapter.AnimalDiff(), this);
        binding.rvAnimals.setAdapter(adapter);
        binding.rvAnimals.setLayoutManager(new LinearLayoutManager(requireActivity()));
        viewModel.getAnimals().observe(getViewLifecycleOwner(), animals -> {
            adapter.submitList(animals);
        });

        viewModel.getSpeciesList().observe(getActivity(), speciesList -> {
            binding.sSpecies.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, speciesList));
        });

        binding.sSpecies.setEnabled(false);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Animal animal = (Animal) data.getSerializableExtra(AnimalEntryActivity.ADD_ANIMAL_RESULT);
                             if (animal != null) {
                                 Log.d("AnimalRegAddAnimalsFragment", "onViewCreated: " + animal.getAnimalId());
                                 Log.d("AnimalRegAddAnimalsFragment", "onViewCreated: " + animal.getBreed());
                                 Log.d("AnimalRegAddAnimalsFragment", "onViewCreated: " + animal.getSex());
                                 Log.d("AnimalRegAddAnimalsFragment", "onViewCreated: " + animal.getAge());
                                 Log.d("AnimalRegAddAnimalsFragment", "onViewCreated: " + animal.isDead());
                                 Log.d("AnimalRegAddAnimalsFragment", "onViewCreated: " + animal.getSeller());

                                 viewModel.getAnimals().getValue().add(animal);
                                 adapter.notifyDataSetChanged();
                             }
                             boolean addAnotherAnimal = data.getBooleanExtra(AnimalEntryActivity.ADD_ANOTHER_ANIMAL, false);

                             if(addAnotherAnimal){
                                 Intent intent = new Intent(getActivity(), AnimalEntryActivity.class);
                                 activityResultLauncher.launch(intent);
                             }
                        }
                    }
                });

        binding.btnAddAnimal.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AnimalEntryActivity.class);
            activityResultLauncher.launch(intent);
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAnimalItemClick(int position) {
    }

    @Override
    public void onAnimalItemDeleteClick(int position) {
        viewModel.getAnimals().getValue().remove(position);
        adapter.notifyDataSetChanged();
    }
}