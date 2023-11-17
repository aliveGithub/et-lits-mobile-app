package org.moa.etlits.data.repositories;

import android.app.Application;

import org.moa.etlits.api.response.TypeObjectUnmovable;
import org.moa.etlits.data.dao.AppDatabase;
import org.moa.etlits.data.dao.EstablishmentDao;
import org.moa.etlits.data.models.Establishment;
import org.moa.etlits.utils.Constants;

import java.util.List;
import java.util.Set;

import androidx.lifecycle.LiveData;

public class EstablishmentRepository {
    private EstablishmentDao establishmentDao;

    private LiveData<List<Establishment>> establishments;

    public EstablishmentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        establishmentDao = db.establishmentDao();
        establishments = establishmentDao.getAll();
    }

    public LiveData<List<Establishment>> getAll() {
        return establishments;
    }

    public LiveData<Establishment> loadByCode(String code) {
        return establishmentDao.loadByCode(code);
    }

    public void insert(Establishment establishment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            establishmentDao.insert(establishment);
        });
    }

    public void insert(TypeObjectUnmovable unmovable, Set<String> productionTypes, String[] coordinates) {
        Establishment establishment = new Establishment();
        establishment.setCode(unmovable.getKey());
        establishment.setType(Constants.UNMOVABLE_ESTABLISHMENT);
        establishment.setCategory(unmovable.getCategory());
        establishment.setProductionTypes(productionTypes);
        if (unmovable.getEstablishment() != null) {
            establishment.setName(unmovable.getEstablishment().getName());
        }

        if (unmovable.getPhysicalLocation() != null) {
            establishment.setCity(unmovable.getPhysicalLocation().getCity());
            establishment.setRegion(unmovable.getPhysicalLocation().getRegion());
            establishment.setWoreda(unmovable.getPhysicalLocation().getWoreda());
            establishment.setZone(unmovable.getPhysicalLocation().getZone());
        }

        if (unmovable.getContactDetails() != null) {
            establishment.setTelephoneNumber(unmovable.getContactDetails().getTelephoneNumber());
            establishment.setMobileNumber(unmovable.getContactDetails().getMobilePhoneNumber());
            establishment.setEmail(unmovable.getContactDetails().getEmailAddress());
        }
        if (unmovable.getPostalLocation() != null) {
            String address =  unmovable.getPostalLocation().getCity() + " " + unmovable.getPostalLocation().getPostcode();
            establishment.setAlternativePostalAddress(address);
        }

        if (coordinates != null) {
            establishment.setLatitude(coordinates[1]);
            establishment.setLongitude(coordinates[0]);
        }

        insert(establishment);
    }

    public void update(Establishment establishment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            establishmentDao.update(establishment);
        });
    }

}
