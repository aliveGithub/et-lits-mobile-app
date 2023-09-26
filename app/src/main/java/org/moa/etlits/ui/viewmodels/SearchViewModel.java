package org.moa.etlits.ui.viewmodels;

import android.app.Application;

import org.moa.etlits.data.models.Establishment;
import org.moa.etlits.data.repositories.EstablishmentRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {
    private EstablishmentRepository establishmentRepository;
    private LiveData<List<Establishment>> establishments;

    public LiveData<List<Establishment>> getEstablishments() {
        return establishments;
    }

  public SearchViewModel(Application application) {
        this.establishmentRepository = new EstablishmentRepository(application);
        this.establishments = establishmentRepository.getAll();
    }
}