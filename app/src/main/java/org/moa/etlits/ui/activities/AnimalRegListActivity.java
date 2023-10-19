package org.moa.etlits.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import org.moa.etlits.R;
import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.ui.adapters.AnimalRegListAdapter;
import org.moa.etlits.ui.viewmodels.AnimalRegListViewModel;


public class AnimalRegListActivity extends AppCompatActivity implements AnimalRegListAdapter.AnimalRegItemEventsListener {
    private AnimalRegListViewModel viewModel;
    private AnimalRegListAdapter animalRegListAdapter;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_reg_list);
        animalRegListAdapter = new AnimalRegListAdapter(new AnimalRegListAdapter.AnimalRegDiff(), this);
        RecyclerView recyclerView = findViewById(R.id.rv_animal_reg);
        recyclerView.setAdapter(animalRegListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this, new AnimalRegListViewModel.Factory(getApplication())).get(AnimalRegListViewModel.class);
        viewModel.getAnimalRegistrationList().observe(this, animalRegistrations -> {
            if (animalRegistrations != null) {
                animalRegListAdapter.submitList(animalRegistrations);
            }
        });

        setUpActionBar();
    }

    @Override
    public void onAnimalRegItemClick(int position) {
       AnimalRegistration animalRegistration = animalRegListAdapter.getCurrentList().get(position);
       Intent intent = new Intent(this, AnimalRegActivity.class);
       intent.putExtra("registrationId", animalRegistration.getId());
       startActivity(intent);
    }

    @Override
    public void onAnimalRegItemDeleteClick(int position) {

    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.animal_reg_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            if (upArrow != null) {
                upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark, getTheme()), PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}