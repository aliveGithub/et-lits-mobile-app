package org.moa.etlits.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String SHARED_PREFERENCES = "et-lits-shared-prefs";
    public static final String SYNC_LOG_ID = "syncLogId";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String IS_USER_LOGGED_IN = "isUserLoggedIn";
    public static final String HAS_INITIALIZED = "hasInitialized";

    public static final String HAS_TERMS_OR_USE_ACCEPTED = "hasTermsOfUseAccepted";
    public static final String INITIAL_SYNC_STARTED = "initialSyncStarted";
    public static String DEFAULT_ESTABLISHMENT = "defaultEstablishment";


    //sync error codes
    public static String UNKNOWN_SYNC_ERROR = "UNKNOWN_SYNC_ERROR";
    public static String   SYNC_VALIDATION_ERROR = "SYNC_VALIDATION_ERROR";

    public static String SERVER_UNREACHABLE = "SERVER_UNREACHABLE";

    //Unmovable types
    public static String UNMOVABLE_ESTABLISHMENT = "ESTABLISHMENT";

    public static String CATEGORY_KEY_BREEDS = "csBreeds";
    public static String CATEGORY_KEY_SEX = "csSexForEntry";

    public static String CATEGORY_KEY_SPECIES = "csSpeciesForEntry";

    public static String CATEGORY_KEY_TREATMENT_TYPE = "csTypeTreatment";

    public static List<String> HOLDING_GROUND_ESTABLISHMENT_CATEGORIES = new ArrayList<>(Arrays.asList("kcLairageHoldingGround"));

    public static List<String> PRODUCTION_TYPE_ESTABLISHMENT_CATEGORIES = new ArrayList<>(Arrays.asList("kcFeedlot", "kcLairageQuarantineStation",
            "kcLairagePreQuarantineStation", "kcSlaughteringExportAbattoir"));


    public enum SyncType {
        CONFIG_DATA,
        ALL_DATA
    }

    public enum SyncStatus {
        INITIALIZING,
        IN_PROGRESS,
        STOPPING,
        STOPPED,
        PARTIAL,
        SUCCESSFUL,
        FAILED
    }

    public enum AnimalRegStep {
        MOVE_EVENTS,
        REGISTRATION,
        TREATMENTS
    }

    public static final List<AnimalRegStep> REGISTRATION_STEPS = Arrays.asList(AnimalRegStep.MOVE_EVENTS, AnimalRegStep.REGISTRATION, AnimalRegStep.TREATMENTS);
}

