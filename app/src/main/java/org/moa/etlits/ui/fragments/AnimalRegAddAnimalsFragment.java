package org.moa.etlits.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.moa.etlits.R;
import org.moa.etlits.data.models.Animal;
import org.moa.etlits.databinding.FragmentAnimalRegAddAnimalsBinding;
import org.moa.etlits.ui.adapters.AnimalListAdapter;
import org.moa.etlits.ui.viewmodels.AnimalRegViewModel;


public class AnimalRegAddAnimalsFragment extends Fragment implements AnimalListAdapter.AnimalItemEventsListener {
    private AnimalRegViewModel viewModel;
    private AnimalListAdapter adapter;

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

        binding.btnAddAnimal.setOnClickListener(v -> {
            for (int i = 0; i < 10; i++) {
                Animal animal = new Animal();
                animal.setAnimalId("00000000" + i);
                viewModel.getAnimals().getValue().add(animal);
            }
            adapter.notifyDataSetChanged();
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAnimalClick(int position) {

    }
}