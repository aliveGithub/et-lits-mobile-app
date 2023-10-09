package org.moa.etlits.ui.viewmodels;

import android.app.Application;

import org.moa.etlits.data.models.Animal;
import org.moa.etlits.data.models.AnimalRegistration;
import org.moa.etlits.data.models.CategoryValue;
import org.moa.etlits.data.models.Establishment;
import org.moa.etlits.data.models.Treatment;
import org.moa.etlits.data.repositories.AnimalRegistrationRepository;
import org.moa.etlits.data.repositories.AnimalRepository;
import org.moa.etlits.data.repositories.CategoryValueRepository;
import org.moa.etlits.data.repositories.EstablishmentRepository;
import org.moa.etlits.data.repositories.TreatmentRepository;
import org.moa.etlits.ui.validation.AnimalRegFormState;
import org.moa.etlits.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class AnimalRegViewModel extends AndroidViewModel {

    private AnimalRegistrationRepository animalRegistrationRepository;
    private AnimalRepository animalRepository;

    private TreatmentRepository treatmentRepository;
    private EstablishmentRepository establishmentRepository;

    private CategoryValueRepository categoryValueRepository;
    private final MutableLiveData<Integer> currentStep = new MutableLiveData<>(0);

    private LiveData<AnimalRegistration> animalRegistration = new MutableLiveData<>(new AnimalRegistration());

    private MutableLiveData<AnimalRegFormState> animalRegFormState = new MutableLiveData<>();
    private LiveData<List<Establishment>> establishmentList;

    private LiveData<List<Animal>> animalList;

   private LiveData<List<Treatment>> treatmentList;

    private LiveData<List<CategoryValue>> speciesList;

    private LiveData<List<CategoryValue>> treatmentTypeList;

    private Calendar dateMoveOn;
    private Calendar dateMoveOff;

    private Calendar dateIdentification;

    public AnimalRegViewModel(Application application, long id) {
        super(application);
        animalRegistrationRepository = new AnimalRegistrationRepository(application);
        animalRepository = new AnimalRepository(application);
        establishmentRepository = new EstablishmentRepository(application);
        treatmentRepository = new TreatmentRepository(application);
        categoryValueRepository = new CategoryValueRepository(application);

        animalRegistration = animalRegistrationRepository.loadById(id);
        establishmentList = establishmentRepository.getAll();
        animalList = animalRepository.getByAnimalRegistrationId(id);
        treatmentList = treatmentRepository.getByAnimalRegistrationId(id);

        treatmentTypeList = categoryValueRepository.loadByType(Constants.CATEGORY_KEY_TREATMENT_TYPE);

        populateSpeciesList();
        dateMoveOn = Calendar.getInstance();
        dateMoveOff = Calendar.getInstance();
        dateIdentification = Calendar.getInstance();
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

    public void moveNext() {
        if (currentStep.getValue() < Constants.AnimalRegStep.values().length - 1) {
            currentStep.setValue(currentStep.getValue() + 1);
        }
    }

    public void movePrev() {
        if (currentStep.getValue() > 0) {
            currentStep.setValue(currentStep.getValue() - 1);
        }
    }

    public void moveFirst() {
        currentStep.setValue(0);
    }

    public void moveLast() {
        currentStep.setValue(Constants.AnimalRegStep.values().length - 1);
    }

    public boolean isLast(){
        return currentStep.getValue() == Constants.AnimalRegStep.values().length - 1;
    }

    public boolean isFirst(){
        return currentStep.getValue() == 0;
    }


    public Constants.AnimalRegStep getCurrentStep() {
        return Constants.AnimalRegStep.values()[currentStep.getValue()];
    }

    public LiveData<AnimalRegistration> getAnimalRegistration() {
        return animalRegistration;
    }

    public void insert() {
        animalRegistrationRepository.insert(getAnimalRegistration().getValue(), getAnimals().getValue(), getTreatmentList().getValue());
    }

    public void update() {
        animalRegistrationRepository.update(getAnimalRegistration().getValue(), getAnimals().getValue(), getTreatmentList().getValue());
    }

    public void save() {
        if (getAnimalRegistration().getValue().getId() == 0) {
            insert();
        } else {
            update();
        }
    }

    public void loadById(long id) {
        animalRegistrationRepository.loadById(id);
    }

    public void initAnimalRegistration() {
        AnimalRegistration newAnimalRegistration = new AnimalRegistration();
        Date today = Calendar.getInstance().getTime();
        newAnimalRegistration.setDateIdentification(today);
        newAnimalRegistration.setDateMoveOff(today);
        newAnimalRegistration.setDateMoveOn(today);
        animalRegistration = new MutableLiveData<>(newAnimalRegistration);
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

    public void validateMoveEvents() {
        AnimalRegistration animalRegistration = getAnimalRegistration().getValue();
        if (animalRegistration == null) {
            return;
        }
        AnimalRegFormState newAnimalRegFormState = new AnimalRegFormState();
        newAnimalRegFormState.validateMoveEvents(animalRegistration);
        animalRegFormState.setValue(newAnimalRegFormState);
    }

    private boolean moveEventsIsValid() {
        return getAnimalRegFormState().getValue().isDataValid();
    }

    public LiveData<List<Animal>> getAnimals() {
        return animalList;
    }

    public LiveData<List<Treatment>> getTreatmentList() {
        return treatmentList;
    }
    public List<Treatment> getTreatments() {
        if (treatmentList.getValue() == null) {
            treatmentList = new MutableLiveData<>(new ArrayList<>());
        }
        return treatmentList.getValue();
    }

    public List<String> getSelectedTreatmentTypes() {
        List<Treatment> treatments = getTreatments();
        List<String> selectedTreatmentTypes = new ArrayList<>();
        for (int i = 0; i < treatments.size(); i++) {
            Treatment treatment = treatments.get(i);
            selectedTreatmentTypes.add(treatment.getTreatmentApplied());
        }
        return selectedTreatmentTypes;
    }

    public LiveData<List<CategoryValue>> getSpeciesList() {
        return speciesList;
    }

    public LiveData<List<CategoryValue>> getTreatmentTypeList() {
        return treatmentTypeList;
    }

    public MutableLiveData<AnimalRegFormState> getAnimalRegFormState() {
        return animalRegFormState;
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
