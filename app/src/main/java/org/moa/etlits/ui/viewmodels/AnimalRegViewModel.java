package org.moa.etlits.ui.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.core.util.Supplier;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

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


public class AnimalRegViewModel extends AndroidViewModel {

    private AnimalRegistrationRepository animalRegistrationRepository;
    private AnimalRepository animalRepository;

    private TreatmentRepository treatmentRepository;
    private EstablishmentRepository establishmentRepository;

    private CategoryValueRepository categoryValueRepository;
    private final MutableLiveData<Integer> currentStep = new MutableLiveData<>(0);

    private LiveData<AnimalRegistration> animalRegistration;

    private MutableLiveData<AnimalRegFormState> animalRegFormState = new MutableLiveData<>();
    private LiveData<List<Establishment>> establishmentList;

    private LiveData<List<Animal>> animalList;

    private LiveData<List<Treatment>> treatmentList;

    private LiveData<List<CategoryValue>> speciesList;

    private LiveData<List<CategoryValue>> treatmentTypeList;

    private LiveData<List<CategoryValue>> breedList;

    private final MediatorLiveData<Pair<AnimalRegistration, List<Establishment>>> animalRegEstablismentsCombined = new MediatorLiveData<>();

    private Calendar dateMoveOn;
    private Calendar dateMoveOff;

    private Calendar dateIdentification;

    public LiveData<List<CategoryValue>> getBreedList() {
        return breedList;
    }

    private SharedPreferences sharedPreferences;

    public AnimalRegViewModel(Application application, long id, SavedStateHandle savedStateHandle) {
        super(application);
        sharedPreferences = application.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        animalRegistrationRepository = new AnimalRegistrationRepository(application);
        animalRepository = new AnimalRepository(application);
        establishmentRepository = new EstablishmentRepository(application);
        treatmentRepository = new TreatmentRepository(application);
        categoryValueRepository = new CategoryValueRepository(application);
        treatmentTypeList = categoryValueRepository.loadByType(Constants.CATEGORY_KEY_TREATMENT_TYPE);
        breedList = this.categoryValueRepository.loadByType(Constants.CATEGORY_KEY_BREEDS);

        populateSpeciesList();

        animalRegistration = getRoomLiveData(() -> id == 0 ?
                        createAnimalRegistration() :
        animalRegistrationRepository.loadById(id),
                savedStateHandle, "animalRegistration");

        establishmentList = establishmentRepository.getAll();
        animalList = getRoomLiveData(() -> animalRepository.getByAnimalRegistrationId(id),
                savedStateHandle, "animalList");
        treatmentList = getRoomLiveData(() -> treatmentRepository.getByAnimalRegistrationId(id),
                savedStateHandle, "treatmentList");

        animalRegEstablismentsCombined.addSource(animalRegistration, animalReg -> {
            combineAnimalRegistrationAndEstablishments(animalReg, establishmentList.getValue());
        });

        animalRegEstablismentsCombined.addSource(establishmentList, establishments -> {
            combineAnimalRegistrationAndEstablishments(animalRegistration.getValue(), establishments);
        });

        dateMoveOn = Calendar.getInstance();
        dateMoveOff = Calendar.getInstance();
        dateIdentification = Calendar.getInstance();
    }

    private MutableLiveData<AnimalRegistration> createAnimalRegistration() {
        MutableLiveData<AnimalRegistration> animalRegistration = new MutableLiveData<>();
        Calendar yesterdayCal = Calendar.getInstance();
        yesterdayCal.add(Calendar.DAY_OF_MONTH, -1);

        AnimalRegistration newAnimalRegistration = new AnimalRegistration();
        newAnimalRegistration.setDateIdentification(yesterdayCal.getTime());
        newAnimalRegistration.setDateMoveOff(yesterdayCal.getTime());
        newAnimalRegistration.setDateMoveOn(Calendar.getInstance().getTime());
        String eid = sharedPreferences.getString(Constants.DEFAULT_ESTABLISHMENT, "");
        newAnimalRegistration.setEstablishmentEid(eid);

        animalRegistration.setValue(newAnimalRegistration);
        return animalRegistration;
    }

    private void combineAnimalRegistrationAndEstablishments(AnimalRegistration animalRegistration,
                                                            List<Establishment> establishmentList) {
        if (animalRegistration != null && establishmentList != null) {
              animalRegEstablismentsCombined.setValue(new Pair<>(animalRegistration, establishmentList));
       }
    }

    public LiveData<Pair<AnimalRegistration, List<Establishment>>> getAnimalRegEstablismentsCombined() {
        return animalRegEstablismentsCombined;
    }

    private <T> LiveData<T> getRoomLiveData(Supplier<LiveData<T>> roomLiveDataSupplier,
                SavedStateHandle savedStateHandle, String key) {

        if (!savedStateHandle.contains(key)) {
            LiveData<T> roomLiveData = roomLiveDataSupplier.get();
            roomLiveData.observeForever(new Observer<T>() {
                @Override
                public void onChanged(T value) {
                    savedStateHandle.set(key, value);
                    roomLiveData.removeObserver(this);
                }
            });
        }
        return savedStateHandle.getLiveData(key);
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

    public static class Factory extends AbstractSavedStateViewModelFactory {
        private final Application application;
        private final long id;

        public Factory(Application application, long id) {
            this.id = id;
            this.application = application;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        protected <T extends ViewModel> T create(@NonNull String s, @NonNull Class<T> aClass,
                                                 @NonNull SavedStateHandle savedStateHandle) {
            return (T) new AnimalRegViewModel(application, id, savedStateHandle);
        }
    }
}
