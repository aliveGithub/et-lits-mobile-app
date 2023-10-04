package org.moa.etlits.ui.viewmodels;

import android.app.Application;
import android.widget.TextView;

import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.models.Establishment;
import org.moa.etlits.data.repositories.AnimalRegistrationRepository;
import org.moa.etlits.data.repositories.AnimalRepository;
import org.moa.etlits.data.repositories.EstablishmentRepository;
import org.moa.etlits.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class AnimalRegViewModel extends AndroidViewModel {

    private AnimalRegistrationRepository animalRegistrationRepository;
    private AnimalRepository animalRepository;
    private EstablishmentRepository establishmentRepository;
    private final MutableLiveData<Integer> currentStep = new MutableLiveData<>(0);

    private LiveData<AnimalRegistration> animalRegistration = new MutableLiveData<>(new AnimalRegistration());
    private LiveData<List<Establishment>> establishmentList;

    private LiveData<List<Animal>> animalList;

    private LiveData<List<CategoryValue>> speciesList;

    private Calendar dateMoveOn;
    private Calendar dateMoveOff;

    private Calendar dateIdentification;

    public AnimalRegViewModel(Application application, long id) {
        super(application);
        animalRegistrationRepository = new AnimalRegistrationRepository(application);
        animalRepository = new AnimalRepository(application);
        establishmentRepository = new EstablishmentRepository(application);
        establishmentList = establishmentRepository.getAll();
        animalList = animalRepository.getByAnimalRegistrationId(id);
        dateMoveOn = Calendar.getInstance();
        dateMoveOff = Calendar.getInstance();
        dateIdentification = Calendar.getInstance();
        populateSpeciesList();
    }

    private void populateSpeciesList() {
        List<CategoryValue> species = new ArrayList<>();
        CategoryValue newCategoryValue = new CategoryValue();
        newCategoryValue.setValueId("csCattle");
        newCategoryValue.setValue("Cattle");
        newCategoryValue.setCategoryKey(Constants.CATEGORY_KEY_SPECIES);
        newCategoryValue.setLanguage("en");
        species.add(newCategoryValue);
        speciesList = new MutableLiveData<>(species);
    }

    public void next() {
        if (currentStep.getValue() < Constants.AnimalRegStep.values().length - 1) {
            currentStep.setValue(currentStep.getValue() + 1);
        }
    }

    public void prev() {
        if (currentStep.getValue() > 0) {
            currentStep.setValue(currentStep.getValue() - 1);
        }
    }

    public void first() {
        currentStep.setValue(0);
    }

    public void last() {
        currentStep.setValue(Constants.AnimalRegStep.values().length - 1);
    }

    public Constants.AnimalRegStep getCurrentStep() {
        return Constants.AnimalRegStep.values()[currentStep.getValue()];
    }

    public LiveData<AnimalRegistration> getAnimalRegistration() {
        return animalRegistration;
    }

    public void insert(AnimalRegistration animalRegistration) {
        animalRegistrationRepository.insert(animalRegistration);
    }

    public void update(AnimalRegistration animalRegistration) {
        animalRegistrationRepository.update(animalRegistration);
    }

    public LiveData<List<Establishment>> getEstablishmentList() {
        return establishmentList;
    }

    public LiveData<List<Establishment>> getEstablishments() {
        return establishmentList;
    }

    public void setDateMoveOn(int year, int month, int dayOfMonth) {
        this.dateMoveOn.set(year, month, dayOfMonth);
        if (getAnimalRegistration().getValue() != null) {
            getAnimalRegistration().getValue().setDateMoveOn(getDateMoveOn().getTime());
        }
    }

    public Calendar getDateMoveOn() {
        return dateMoveOn;
    }

    public void setDateMoveOff(int year, int month, int dayOfMonth) {
        this.dateMoveOff.set(year, month, dayOfMonth);
        if (getAnimalRegistration().getValue() != null) {
            getAnimalRegistration().getValue().setDateMoveOff(getDateMoveOff().getTime());
        }
    }

    public Calendar getDateMoveOff() {
        return dateMoveOff;
    }

    public void setDateIdentification(int year, int month, int dayOfMonth) {
        this.dateIdentification.set(year, month, dayOfMonth);
        if (getAnimalRegistration().getValue() != null) {
            getAnimalRegistration().getValue().setDateIdentification(getDateIdentification().getTime());
        }
    }

    public Calendar getDateIdentification() {
        return dateIdentification;
    }

    public void setMoveOffEstablishment(String code) {
        if (getAnimalRegistration().getValue() != null) {
            getAnimalRegistration().getValue().setHoldingGroundEid(code);
        }
    }

    public void setMoveOnEstablishment(String code) {
        if (getAnimalRegistration().getValue() != null) {
            getAnimalRegistration().getValue().setEstablishmentEid(code);
        }
    }

    public LiveData<List<Animal>> getAnimals() {
        return animalList;
    }

    public LiveData<List<CategoryValue>> getSpeciesList() {
        return speciesList;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private Application application;
        private long id;

        public Factory(Application application, long id) {
            this.id = id;
            this.application = application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AnimalRegViewModel(application, id);
        }
    }
}
