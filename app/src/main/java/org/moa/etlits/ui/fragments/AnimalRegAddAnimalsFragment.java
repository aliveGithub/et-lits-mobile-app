package org.moa.etlits.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.moa.etlits.R;
import org.moa.etlits.data.models.Animal;
import org.moa.etlits.databinding.FragmentAnimalRegAddAnimalsBinding;
import org.moa.etlits.ui.activities.AnimalEntryActivity;
import org.moa.etlits.ui.adapters.AnimalEditListAdapter;
import org.moa.etlits.ui.viewmodels.AnimalRegViewModel;


public class AnimalRegAddAnimalsFragment extends Fragment implements AnimalEditListAdapter.AnimalItemEventsListener {
    private AnimalRegViewModel viewModel;
    private AnimalEditListAdapter adapter;

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

        adapter = new AnimalEditListAdapter(new AnimalEditListAdapter.AnimalDiff(), this);
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
                            Animal animal = (Animal) data.getSerializableExtra(AnimalEntryActivity.ANIMAL_DETAILS_RESULT);
                            int position = data.getIntExtra(AnimalEntryActivity.POSITION_IN_LIST, -1);
                            if (animal != null) {
                                if (position != -1) {
                                    viewModel.getAnimals().getValue().set(position, animal);
                                } else {
                                    viewModel.getAnimals().getValue().add(animal);
                                }

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
        Intent intent = new Intent(getActivity(), AnimalEntryActivity.class);
        intent.putExtra(AnimalEntryActivity.ANIMAL, viewModel.getAnimals().getValue().get(position));
        intent.putExtra(AnimalEntryActivity.POSITION_IN_LIST, position);
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onAnimalItemDeleteClick(int position) {
        showConfirmationDialog(position);
    }

    private void showConfirmationDialog(int position) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView message = dialog.findViewById(R.id.dialog_message);

        Button positiveButton = dialog.findViewById(R.id.positive_button);
        Button negativeButton = dialog.findViewById(R.id.negative_button);
        Button neutralButton = dialog.findViewById(R.id.neutral_button);

        title.setText(R.string.animal_delete_confirmation_dialog_title);

        Animal animal = (Animal) viewModel.getAnimals().getValue().get(position);

        message.setText(getString(R.string.animal_delete_confirmation_dialog_message, animal.getAnimalId()));

        positiveButton.setText(R.string.label_cancel);
        positiveButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        negativeButton.setText(R.string.label_ok);
        negativeButton.setOnClickListener(v -> {
            viewModel.removeAnimal(position);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        neutralButton.setVisibility(View.GONE);

        dialog.show();

    }
}