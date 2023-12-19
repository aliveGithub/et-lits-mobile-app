package org.moa.etlits.utils;

import android.content.Context;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PermissionsUtil {
    private EncryptedPreferences encryptedPreferences;
    private Set<String> roles = new HashSet<>();

    public PermissionsUtil(Context context) {
        encryptedPreferences = new EncryptedPreferences(context);
        roles = encryptedPreferences.readSet(Constants.ROLES);
    }

    private Set<String> VIEW_INDIVIDUAL_ANIMAL_ROLES = new HashSet<>(Arrays.asList("kcRoleSystemMemberAITUnit", "kcRoleSystemRegQuaranOfficer", "kcRolesSystemAbattoirOperator", "kcRoleSystemApprovedKeeper", "kcRoleSystemInspectorAbattoir", "kcRoleSystemApprovedPrivateVet"));

    private Set<String> REGISTER_INDIVIDUAL_ANIMAL_ROLES = new HashSet<>(Arrays.asList("kcRolesSystemAbattoirOperator", "kcRoleSystemApprovedKeeper", "kcRoleSystemInspectorAbattoir", "kcRoleSystemApprovedPrivateVet"));

    private Set<String> REGISTER_REPLACEMENT_EAR_TAG_ROLES = new HashSet<>(Arrays.asList("kcRoleSystemApprovedKeeper", "kcRoleSystemApprovedPrivateVet"));

    private Set<String> VIEW_REGISTRATION_EVENTS_ROLES = new HashSet<>(Arrays.asList("kcRoleSystemMemberAITUnit", "kcRoleSystemRegQuaranOfficer", "kcRoleSystemApprovedPrivateVet"));

    public boolean hasRegisterAnimalRole() {
        return !Collections.disjoint(REGISTER_INDIVIDUAL_ANIMAL_ROLES, roles);
    }

    public boolean hasViewAnimalRole() {
        return !Collections.disjoint(VIEW_INDIVIDUAL_ANIMAL_ROLES, roles);
    }

    public boolean hasReplaceTagRole() {
        return !Collections.disjoint(REGISTER_REPLACEMENT_EAR_TAG_ROLES, roles);
    }

    public boolean hasViewRegistrationEventsRole() {
        return !Collections.disjoint(VIEW_REGISTRATION_EVENTS_ROLES, roles);
    }

    public boolean hasAnyAnimalRoles() {
        return hasRegisterAnimalRole() || hasViewAnimalRole() || hasReplaceTagRole() || hasViewRegistrationEventsRole();
    }
}
