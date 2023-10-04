package org.moa.etlits.ui.activities;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import org.moa.etlits.R;
import org.moa.etlits.databinding.ActivityAnimalRegBinding;
import org.moa.etlits.ui.fragments.AnimalRegAddAnimalsFragment;
import org.moa.etlits.ui.fragments.AnimalRegMoveEventsFragment;
import org.moa.etlits.ui.fragments.AnimalRegTreatmentsFragment;
import org.moa.etlits.ui.viewmodels.AnimalRegViewModel;
import org.moa.etlits.utils.Constants;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class AnimalRegActivity extends AppCompatActivity {
    private Fragment moveEventsFragment;
    private Fragment animalRegFragment;
    private Fragment treatmentsFragment;
    private ActivityAnimalRegBinding binding;
    private AnimalRegViewModel viewModel;
    private SharedPreferences sharedPreferences;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimalRegBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new AnimalRegViewModel.Factory(getApplication(), 0)).get(AnimalRegViewModel.class);

        if (savedInstanceState == null) {
            addFragments();
        }
        setUpNavigation();
        setUpActionBar();
    }

    private void addFragments() {
        animalRegFragment = new AnimalRegAddAnimalsFragment();
        moveEventsFragment = new AnimalRegMoveEventsFragment();
        treatmentsFragment = new AnimalRegTreatmentsFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_animal_reg, moveEventsFragment, Constants.AnimalRegStep.MOVE_EVENTS.toString()).commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_animal_reg, animalRegFragment, Constants.AnimalRegStep.REGISTRATION.toString()).hide(animalRegFragment).commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_animal_reg, treatmentsFragment, Constants.AnimalRegStep.TREATMENTS.toString()).hide(treatmentsFragment).commit();
    }

    public void showFragment(Constants.AnimalRegStep step) {
        switch (step) {
            case REGISTRATION:
                getSupportFragmentManager().beginTransaction().hide(moveEventsFragment).commit();
                getSupportFragmentManager().beginTransaction().hide(treatmentsFragment).commit();
                getSupportFragmentManager().beginTransaction().show(animalRegFragment).commit();
                break;
            case MOVE_EVENTS:
                getSupportFragmentManager().beginTransaction().hide(animalRegFragment).commit();
                getSupportFragmentManager().beginTransaction().hide(treatmentsFragment).commit();
                getSupportFragmentManager().beginTransaction().show(moveEventsFragment).commit();
                break;
            case TREATMENTS:
                getSupportFragmentManager().beginTransaction().hide(animalRegFragment).commit();
                getSupportFragmentManager().beginTransaction().hide(moveEventsFragment).commit();
                getSupportFragmentManager().beginTransaction().show(treatmentsFragment).commit();
                break;
        }
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

    private void setUpNavigation() {
        binding.ivFirst.setOnClickListener(v -> {
            viewModel.first();
            showFragment(viewModel.getCurrentStep());
        });
        binding.ivPrev.setOnClickListener(v -> {
            viewModel.prev();
            showFragment(viewModel.getCurrentStep());
        });
        binding.ivNext.setOnClickListener(v -> {
            viewModel.next();
            showFragment(viewModel.getCurrentStep());
        });
        binding.ivLast.setOnClickListener(v -> {
            viewModel.last();
            showFragment(viewModel.getCurrentStep());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        String defaultEstablishment = sharedPreferences.getString(Constants.DEFAULT_ESTABLISHMENT, "");
        binding.tvSelectedEstablishment.setText(defaultEstablishment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}