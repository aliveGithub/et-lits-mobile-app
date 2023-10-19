package org.moa.etlits.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

    private Dialog confirmLeaveDialog;
    private Dialog noAnimalsDialog;

    public static String REGISTERED_ANIMALS = "registeredAnimals";
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_activity_menu, menu);
        return true;
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
         if (item.getItemId() == android.R.id.home) {
             if (viewModel.getAnimalRegistration().getValue() != null) {
                 showLeaveDialog();
             } else {
                 finish();
             }
             return true;
         } else if (item.getItemId() == R.id.action_info) {
             Intent intent = new Intent(this, InfoActivity.class);
             intent.putExtra("title", getString(R.string.animal_reg_title));
             intent.putExtra("message", getInfoMessage());
             startActivity(intent);
             return true;
         }

         return super.onOptionsItemSelected(item);
     }

    private String getInfoMessage() {
        switch (viewModel.getCurrentStep()) {
            case REGISTRATION:
                return getString(R.string.animal_reg_animal_list_info);
            case MOVE_EVENTS:
                return getString(R.string.animal_reg_move_events_info);
            case TREATMENTS:
                return getString(R.string.animal_reg_treatments_info);
            default:
                return "";
        }
    }

    private Dialog showDialog(int titleRId, int messageRId,
                            int positiveButtonRId,
                            int negativeButtonRId,
                            boolean showPositiveButton, boolean showNegativeButton,
                            View.OnClickListener positiveButtonListener,
                            View.OnClickListener negativeButtonListener) {

        final Dialog customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.custom_dialog);
        customDialog.setCancelable(false);

        TextView title = customDialog.findViewById(R.id.dialog_title);
        title.setText(titleRId);

        TextView message = customDialog.findViewById(R.id.dialog_message);
        message.setText(messageRId);

        Button positiveButton = customDialog.findViewById(R.id.positive_button);
        positiveButton.setVisibility(showPositiveButton ? View.VISIBLE : View.GONE);
        positiveButton.setText(positiveButtonRId);
        positiveButton.setOnClickListener(positiveButtonListener);

        Button negativeButton = customDialog.findViewById(R.id.negative_button);
        negativeButton.setVisibility(showNegativeButton ? View.VISIBLE : View.GONE);
        negativeButton.setText(negativeButtonRId);
        negativeButton.setOnClickListener(negativeButtonListener);
        customDialog.show();
        return customDialog;
    }

    private void showNoAnimalsDialog() {
        noAnimalsDialog = showDialog(R.string.animal_reg_no_animals_registered,
                R.string.animal_reg_animal_list_empty_error,
                R.string.animal_reg_dialog_ok,
                R.string.animal_reg_dialog_ok,
                true,
                false,
                v -> {
                 if (noAnimalsDialog != null) {
                     noAnimalsDialog.dismiss();
                 }
                 },
                null);
    }

    private void showLeaveDialog() {
        confirmLeaveDialog =  showDialog(R.string.animal_reg_leave_the_form,
                R.string.animal_reg_leave_form_msg,
                R.string.animal_reg_leave_form_cancel,
                R.string.animal_reg_leave_form_yes,
                true,
                true,
                v -> {
                    if (confirmLeaveDialog != null) {
                        confirmLeaveDialog.dismiss();
                    }
                },
                v -> {
                    if (confirmLeaveDialog != null) {
                        confirmLeaveDialog.dismiss();
                    }
                    finish();
                });
    }
}