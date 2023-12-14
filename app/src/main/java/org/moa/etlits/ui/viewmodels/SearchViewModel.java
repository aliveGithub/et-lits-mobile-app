package org.moa.etlits.ui.viewmodels;

import android.app.Application;

import org.moa.etlits.data.models.AnimalSearchResult;
import org.moa.etlits.data.models.Establishment;
import org.moa.etlits.data.repositories.AnimalRepository;
import org.moa.etlits.data.repositories.EstablishmentRepository;
import org.moa.etlits.ui.fragments.SearchFragment;
import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.EncryptedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {
    private EstablishmentRepository establishmentRepository;

    private AnimalRepository animalRepository;
    private LiveData<List<Establishment>> establishments;

    private LiveData<List<AnimalSearchResult>> animals;

    private MutableLiveData<String> searchView = new MutableLiveData<>(SearchFragment.ESTABLISHMENT_VIEW);

    private Set<String> roles;

    public LiveData<List<Establishment>> getEstablishments() {
        return establishments;
    }

  public SearchViewModel(Application application) {
        this.establishmentRepository = new EstablishmentRepository(application);
        this.animalRepository = new AnimalRepository(application);
        this.establishments = establishmentRepository.getAll();
        this.animals = new MutableLiveData<>(new ArrayList<>());
        EncryptedPreferences encryptedPreferences = new EncryptedPreferences(application);
        roles = encryptedPreferences.readSet(Constants.ROLES);
    }

    public LiveData<List<AnimalSearchResult>> searchAnimals(String query) {
        animals = animalRepository.searchAnimals(query);
        return animals;
    }

    public void switchView(String view) {
        searchView.setValue(view);
    }

    public LiveData<String> getSearchView() {
        return searchView;
    }

    public LiveData<List<AnimalSearchResult>> getAnimals() {
        return animals;
    }

    public Set<String> getRoles() {
        return roles;
    }
}