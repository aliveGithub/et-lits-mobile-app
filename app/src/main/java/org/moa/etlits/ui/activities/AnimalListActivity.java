package org.moa.etlits.ui.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.moa.etlits.R;
import org.moa.etlits.ui.adapters.AnimalViewListAdapter;
import org.moa.etlits.ui.fragments.SearchFragment;
import org.moa.etlits.ui.viewmodels.AnimalListViewModel;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AnimalListActivity extends AppCompatActivity implements AnimalViewListAdapter.AnimalItemEventsListener {
    private AnimalListViewModel viewModel;
    private AnimalViewListAdapter animalsAdapter;

    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_list);

        animalsAdapter = new AnimalViewListAdapter(new AnimalViewListAdapter.AnimalRegDiff(), this);
        RecyclerView recyclerView = findViewById(R.id.rv_animal_list);
        recyclerView.setAdapter(animalsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this, new AnimalListViewModel.Factory(getApplication())).get(AnimalListViewModel.class);
        viewModel.getAnimalList().observe(this, animals -> {
            if (animals != null) {
                animalsAdapter.submitList(animals);
            }
        });

        setUpActionBar();

        if (savedInstanceState == null) {
            addSearchFragment();
        }
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.animals_list_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            if (upArrow != null) {
                upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryDark, getTheme()), PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
            }
        }
    }

    private void addSearchFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment searchFragment = SearchFragment.newInstance(SearchFragment.ANIMAL_VIEW, null);
        fragmentTransaction.add(R.id.fl_animal_search, searchFragment, "search_animals");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_info) {
            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra("title", getString(R.string.animals_list_title));
            intent.putExtra("message", getString(R.string.animals_list_info));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onAnimalItemClick(int position) {
        Intent intent = new Intent(this, AnimalViewActivity.class);
        intent.putExtra("animalId", viewModel.getAnimalList().getValue().get(position).getAnimalId());
        startActivity(intent);
    }
}