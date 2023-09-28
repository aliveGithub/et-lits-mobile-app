package org.moa.etlits.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.ImageView;

import org.moa.etlits.R;
import org.moa.etlits.ui.fragments.AnimalRegAddAnimalsFragment;
import org.moa.etlits.ui.fragments.AnimalRegMoveEventsFragment;
import org.moa.etlits.ui.fragments.AnimalRegTreatmentsFragment;
import org.moa.etlits.ui.fragments.HomeTabsFragment;
import org.moa.etlits.ui.viewmodels.AnimalRegViewModel;
import org.moa.etlits.utils.Constants;

public class AnimalRegActivity extends AppCompatActivity {
    private Fragment moveEventsFragment;
    private Fragment animalRegFragment;
    private Fragment treatmentsFragment;

    private ImageView ivFirst;
    private ImageView ivPrev;
    private ImageView ivNext;
    private ImageView ivLast;
    private AnimalRegViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_reg);
        viewModel = new AnimalRegViewModel(getApplication());
        addFragments();
        setupNavigation();
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
    private void setupNavigation() {
        ivFirst = findViewById(R.id.iv_first);
        ivPrev = findViewById(R.id.iv_prev);
        ivNext = findViewById(R.id.iv_next);
        ivLast = findViewById(R.id.iv_last);
        ivFirst.setOnClickListener(v -> {
            viewModel.first();
            showFragment(viewModel.getCurrentStep());
        });
        ivPrev.setOnClickListener(v -> {
            viewModel.prev();
            showFragment(viewModel.getCurrentStep());
        });
        ivNext.setOnClickListener(v -> {
            viewModel.next();
            showFragment(viewModel.getCurrentStep());
        });
        ivLast.setOnClickListener(v -> {
            viewModel.last();
            showFragment(viewModel.getCurrentStep());
        });
    }
}