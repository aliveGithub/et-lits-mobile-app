package org.moa.etlits.ui.viewmodels;

import android.app.Application;
import android.util.Log;

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

    private LiveData<List<CategoryValue>> breedList;

    private Calendar dateMoveOn;
    private Calendar dateMoveOff;

    private Calendar dateIdentification;

    public LiveData<List<CategoryValue>> getBreedList() {
        return breedList;
    }

    public AnimalRegViewModel(Application application, long id) {
        super(application);
        animalRegistrationRepository = new AnimalRegistrationRepository(application);
        animalRepository = new AnimalRepository(application);
        establishmentRepository = new EstablishmentRepository(application);
        treatmentRepository = new TreatmentRepository(application);
        categoryValueRepository = new CategoryValueRepository(application);

        treatmentTypeList = categoryValueRepository.loadByType(Constants.CATEGORY_KEY_TREATMENT_TYPE);
        breedList = this.categoryValueRepository.loadByType(Constants.CATEGORY_KEY_BREEDS);
        populateSpeciesList();

        animalRegistration = animalRegistrationRepository.loadById(id);
        establishmentList = establishmentRepository.getAll();
        animalList = animalRepository.getByAnimalRegistrationId(id);
        treatmentList = treatmentRepository.getByAnimalRegistrationId(id);


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

    public void initAnimalRegistration(String eid) {
        Calendar yesterdayCal = Calendar.getInstance();
        yesterdayCal.add(Calendar.DAY_OF_MONTH, -1);

        AnimalRegistration newAnimalRegistration = new AnimalRegistration();
        newAnimalRegistration.setDateIdentification(yesterdayCal.getTime());
        newAnimalRegistration.setDateMoveOff(yesterdayCal.getTime());
        newAnimalRegistration.setDateMoveOn(Calendar.getInstance().getTime());
        newAnimalRegistration.setEstablishmentEid(eid);

        animalRegistration = new MutableLiveData<>(newAnimalRegistration);
    }



    public void moveNext() {
        if (currentStepIsValid() && currentStep.getValue() < Constants.AnimalRegStep.values().length - 1) {
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
        if (currentStepIsValid() && currentStep.getValue() < Constants.AnimalRegStep.values().length - 1){
            currentStep.setValue(Constants.AnimalRegStep.values().length - 1);
        }
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

    private boolean currentStepIsValid() {
        switch (getCurrentStep()) {
            case REGISTRATION:
                return animalRegistrationStepIsValid();
            case MOVE_EVENTS:
                return moveEventsStepIsValid();
            default:
                return false;
        }
    }

    public LiveData<AnimalRegistration> getAnimalRegistration() {
        return animalRegistration;
    }

    public void insert() {
        animalRegistrationRepository.insert(getAnimalRegistration().getValue(), getAnimals().getValue(), getTreatmentList().getValue());
    }

    public void update() {
        animalRegistrationRepository.update(getAnimalRegistration().getValue(),
                getAnimals().getValue(), getTreatmentList().getValue());
    }

    public void save() {
        if (getAnimalRegistration().getValue().getId() == 0) {
            insert();
        } else {
            update();
        }
    }

    public void removeAnimal(int position) {
        getAnimals().getValue().remove(position);
    }

    public void removeTreatment(String appliedTreatment) {
        List<Treatment> treatments = treatmentList.getValue();
        for (int i = 0; i < treatments.size(); i++) {
            Treatment treatment = treatments.get(i);
            if (treatment.getTreatmentApplied().equals(appliedTreatment)) {
                getTreatmentList().getValue().remove(i);
                break;
            }
        }
    }

    public void addTreatment(String treatmentTypeId) {
        if (getTreatmentList().getValue() == null) {
            return;
        }
        Treatment treatment = new Treatment();
        treatment.setTreatmentApplied(treatmentTypeId);
        getTreatmentList().getValue().add(treatment);
    }

    public void loadById(long id) {
        animalRegistrationRepository.loadById(id);
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

    private boolean moveEventsStepIsValid() {
        validateMoveEvents();
        return getAnimalRegFormState().getValue().isDataValid();
    }

    public boolean animalRegistrationStepIsValid() {
      List<Animal> animals = getAnimals().getValue();
      return (animals != null && !animals.isEmpty());
    }

    public LiveData<List<Animal>> getAnimals() {
        return animalList;
    }

    public LiveData<List<Treatment>> getTreatmentList() {
        return treatmentList;
    }


    public List<String> getSelectedTreatmentTypes() {
        List<String> selectedTreatmentTypes = new ArrayList<>();
        List<Treatment> treatments = getTreatmentList().getValue();
        if (treatments == null) {
            return selectedTreatmentTypes;
        }
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
