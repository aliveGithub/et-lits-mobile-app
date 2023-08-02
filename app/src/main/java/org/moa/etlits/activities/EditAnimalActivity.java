package org.moa.etlits.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.moa.etlits.R;
import org.moa.etlits.models.Animal;
import org.moa.etlits.viewmodels.AnimalEditViewModel;
import org.moa.etlits.viewmodels.AnimalViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditAnimalActivity extends AppCompatActivity {

    String[] registrationMethods = { "Feedlot", "Slaughter house"};

    private Spinner methodSpinner;
    private Button btnSave;

    private TextView date;

    private EditText tag;

    private AnimalEditViewModel animalViewModel;

    private Calendar identificationCalendar;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_animal);

        tag = findViewById(R.id.edit_tag);
        date = findViewById(R.id.date);
        btnSave = findViewById(R.id.button_save);

        long animalId = getIntent().getLongExtra("animalId", 0);

        animalViewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new AnimalEditViewModel.Factory(getApplication(), animalId)).get(AnimalEditViewModel.class);



        methodSpinner = findViewById(R.id.spinner_method);
        ArrayAdapter ad  = new ArrayAdapter(this,android.R.layout.simple_spinner_item, registrationMethods);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        methodSpinner.setAdapter(ad);

        identificationCalendar = Calendar.getInstance();
        date.setOnClickListener(v -> {
            new DatePickerDialog(EditAnimalActivity.this, (view, year, month, dayOfMonth) -> {
                identificationCalendar.set(year, month, dayOfMonth);
                date.setText(dateFormat.format(identificationCalendar.getTime()));
            }, identificationCalendar.get(Calendar.YEAR), identificationCalendar.get(Calendar.MONTH), identificationCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        animalViewModel.getAnimal().observe(this, animal -> {
            if (animal != null) {
                tag.setText(animal.getTag());
                methodSpinner.setSelection(ad.getPosition(animal.getMethod()));
                date.setText(dateFormat.format(animal.getDateIdentification()));
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedMethod = methodSpinner.getSelectedItem() != null ? (String) methodSpinner.getSelectedItem() : "";
                Toast.makeText(EditAnimalActivity.this, selectedMethod + "" , Toast.LENGTH_SHORT).show();

                long animalId = getIntent().getLongExtra("animalId", 0);
                Animal animal = new Animal();
                animal.setId(animalId);

                if (!TextUtils.isEmpty(tag.getText())){
                    animal.setTag(tag.getText().toString());
                }

                animal.setMethod(selectedMethod);
                if(date.getText() != null){
                    animal.setDateIdentification(identificationCalendar.getTime());
                }

                if (!TextUtils.isEmpty(tag.getText())) {
                    Log.i("SAVE", "id" + animal.getId());
                    if (animal.getId() == 0) {
                        animalViewModel.insert(animal);
                    } else {
                        animalViewModel.update(animal);
                    }

                    finish();
                }
            }
        });

    }
}