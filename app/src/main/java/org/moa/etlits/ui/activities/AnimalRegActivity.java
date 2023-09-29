package org.moa.etlits.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.moa.etlits.R;
import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.ui.adapters.EstablishmentAdapter;
import org.moa.etlits.ui.fragments.AnimalRegAddAnimalsFragment;
import org.moa.etlits.ui.fragments.AnimalRegMoveEventsFragment;
import org.moa.etlits.ui.fragments.AnimalRegTreatmentsFragment;
import org.moa.etlits.ui.viewmodels.AnimalRegViewModel;
import org.moa.etlits.utils.Constants;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

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

        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new AnimalRegViewModel.Factory(getApplication(), 0)).get(AnimalRegViewModel.class);
        if (savedInstanceState == null) {
            addFragments();
        }

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
        Log.i("AnimalRegActivity", "showFragment: " + step.toString());
        if (viewModel.getAnimalRegistration().getValue() != null) {
            Log.i("AnimalRegActivity -", "Date Move Off: " + viewModel.getAnimalRegistration().getValue().getDateMoveOff());
            Log.i("AnimalRegActivity -", "Date Move On: " + viewModel.getAnimalRegistration().getValue().getDateMoveOn());
            Log.i("AnimalRegActivity -", "Date Identification: " + viewModel.getAnimalRegistration().getValue().getDateIdentification());

            Log.i("AnimalRegActivity -", "Move Off Eid: " + viewModel.getAnimalRegistration().getValue().getHoldingGroundEid());
            Log.i("AnimalRegActivity -", "Move On Eid: " + viewModel.getAnimalRegistration().getValue().getEstablishmentEid());

        }
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