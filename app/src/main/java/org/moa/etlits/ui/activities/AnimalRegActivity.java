package org.moa.etlits.ui.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

        Long registrationId = getIntent().getLongExtra("registrationId", 0);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new AnimalRegViewModel.Factory(getApplication(), registrationId)).get(AnimalRegViewModel.class);

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
        updateNavigationButtons();
        binding.ivFirst.setOnClickListener(v -> {
            viewModel.moveFirst();
            showFragment(viewModel.getCurrentStep());
            updateNavigationButtons();
        });
        binding.ivPrev.setOnClickListener(v -> {
            viewModel.movePrev();
            showFragment(viewModel.getCurrentStep());
            updateNavigationButtons();
        });
        binding.ivNext.setOnClickListener(v -> {
            if (viewModel.getCurrentStep() == Constants.AnimalRegStep.REGISTRATION) {
                if (viewModel.getAnimals().getValue() == null || viewModel.getAnimals().getValue().isEmpty()) {
                    showNoAnimalsDialog();
                    return;
                }
            }
            viewModel.moveNext();
            showFragment(viewModel.getCurrentStep());
            updateNavigationButtons();
        });
        binding.ivLast.setOnClickListener(v -> {
            if (viewModel.getCurrentStep() == Constants.AnimalRegStep.REGISTRATION) {
                if (viewModel.getAnimals().getValue() == null || viewModel.getAnimals().getValue().isEmpty()) {
                    showNoAnimalsDialog();
                    return;
                }
            }
            viewModel.moveLast();
            showFragment(viewModel.getCurrentStep());
            updateNavigationButtons();
        });
    }
    private void updateNavigationButtons(){
        if (viewModel == null) return;
        if(viewModel.isFirst()){
            disableNavigationButton(binding.ivFirst);
            disableNavigationButton(binding.ivPrev);
        } else {
            enableNavigationButton(binding.ivFirst);
            enableNavigationButton(binding.ivPrev);
        }

        if(viewModel.isLast()){
            disableNavigationButton(binding.ivLast);
            disableNavigationButton(binding.ivNext);
        }else{
            enableNavigationButton(binding.ivLast);
            enableNavigationButton(binding.ivNext);
        }
    }

    private void disableNavigationButton(ImageView button){
        button.setEnabled(false);
        button.setColorFilter(getResources().getColor(R.color.icon_in_active, getTheme()), PorterDuff.Mode.SRC_ATOP);
    }

    private void enableNavigationButton(ImageView button){
        button.setEnabled(true);
        button.setColorFilter(getResources().getColor(R.color.colorPrimaryDark, getTheme()), PorterDuff.Mode.SRC_ATOP);
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

    private void showNoAnimalsDialog() {
        final Dialog customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.custom_dialog);
        customDialog.setCancelable(false);

        TextView title = customDialog.findViewById(R.id.dialog_title);
        TextView message = customDialog.findViewById(R.id.dialog_message);
        Button positiveButton = customDialog.findViewById(R.id.positive_button);

        title.setText(R.string.animal_reg_no_animals_registered);
        message.setText(R.string.animal_reg_animal_list_empty_error);
        positiveButton.setText(R.string.animal_reg_dialog_ok);
        positiveButton.setOnClickListener(v -> {
            customDialog.dismiss();
        });

        customDialog.show();
    }
}