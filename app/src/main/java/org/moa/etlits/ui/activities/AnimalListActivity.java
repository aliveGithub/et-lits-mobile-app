package org.moa.etlits.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.moa.etlits.R;
import org.moa.etlits.ui.adapters.AnimalListAdapter;
import org.moa.etlits.ui.viewmodels.AnimalViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AnimalListActivity extends AppCompatActivity implements AnimalListAdapter.AnimalItemEventsListener {
    private FloatingActionButton btnAddAnimal;

    private AnimalViewModel animalViewModel;

    private AnimalListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_list);
        animalViewModel  = new ViewModelProvider(this).get(AnimalViewModel.class);
        btnAddAnimal = findViewById(R.id.addAnimal);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new AnimalListAdapter(new AnimalListAdapter.AnimalDiff(), AnimalListActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnAddAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnimalListActivity.this, EditAnimalActivityOld.class);
                startActivity(intent);
            }
        });

        animalViewModel.getAllAnimals().observe(this , animals -> {
            adapter.submitList(animals);
        });
    }

    @Override
    public void onAnimalItemClick(int position) {
       /*Animal animal = adapter.getCurrentList().get(position);
       Intent intent = new Intent(AnimalListActivity.this, EditAnimalActivityOld.class);
       intent.putExtra("animalId", animal.getId());
       startActivity(intent);*/
    }

    @Override
    public void onAnimalItemDeleteClick(int position) {
    }
}