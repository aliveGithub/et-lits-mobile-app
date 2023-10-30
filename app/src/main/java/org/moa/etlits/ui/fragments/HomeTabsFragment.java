package org.moa.etlits.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.moa.etlits.R;
import org.moa.etlits.ui.activities.EstablishmentSummaryActivity;
import org.moa.etlits.ui.activities.SyncActivity;
import org.moa.etlits.ui.activities.LoginActivity;
import org.moa.etlits.ui.activities.MainActivity;
import org.moa.etlits.utils.Constants;
import org.moa.etlits.utils.EncryptedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class HomeTabsFragment extends Fragment {
    private Fragment activeFragment;
    private Fragment homeFragment;
    private Fragment syncFragment;
    private Fragment moveFragment;
    private Fragment animalsFragment;

    private AlertDialog.Builder builder;

    private TextView selectedEstablishment;

    private EncryptedPreferences encryptedPreferences;

    private SharedPreferences sharedPreferences;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_tabs, container, false);

        initializeBottomNavigation(v, savedInstanceState);
        return v;
    }

    private void initializeBottomNavigation(View v, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.menu_home);

        BottomNavigationView bottomNavigationView = v.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_btm_home) {
                loadFragment(homeFragment);
                getActivity().setTitle(R.string.menu_home);
                return true;
            } else if(item.getItemId() == R.id.navigation_sync) {
                Intent intent = new Intent(getActivity(), SyncActivity.class);
                intent.putExtra("syncType", Constants.SyncType.ALL_DATA.toString());
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.navigation_move) {
                loadFragment(moveFragment);
                getActivity().setTitle(R.string.menu_move);
                return true;
            } else if (item.getItemId() == R.id.navigation_animals) {
                loadFragment(animalsFragment);
                getActivity().setTitle(R.string.menu_animals);
                return true;
            }

            return false;
        });

        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            syncFragment = new SyncFragment();
            moveFragment = new MoveFragment();
            animalsFragment = new AnimalsFragment();
            activeFragment = homeFragment;

            getChildFragmentManager().beginTransaction().add(R.id.home_fragment_container, homeFragment, "home").commit();
            getChildFragmentManager().beginTransaction().add(R.id.home_fragment_container, syncFragment, "sync").hide(syncFragment).commit();
            getChildFragmentManager().beginTransaction().add(R.id.home_fragment_container, animalsFragment, "animals").hide(animalsFragment).commit();
            getChildFragmentManager().beginTransaction().add(R.id.home_fragment_container, moveFragment, "move").hide(moveFragment).commit();
        } else {
            homeFragment = getChildFragmentManager().findFragmentByTag("home");
            syncFragment = getChildFragmentManager().findFragmentByTag("sync");
            animalsFragment = getChildFragmentManager().findFragmentByTag("animals");
            moveFragment = getChildFragmentManager().findFragmentByTag("move");
        }

    }

    private void loadFragment(Fragment fragment) {
        if (activeFragment != fragment) {
            getChildFragmentManager().beginTransaction().hide(activeFragment).show(fragment).commit();
            activeFragment = fragment;
        }
    }

    private void logout() {
        builder.setMessage(R.string.sign_out_confirmation_prompt);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Sign Out", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                encryptedPreferences.remove(Constants.USERNAME);
                encryptedPreferences.remove(Constants.PASSWORD);
                encryptedPreferences.remove(Constants.IS_USER_LOGGED_IN);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        builder = new AlertDialog.Builder(getActivity());
        selectedEstablishment = ((MainActivity) getActivity()).findViewById(R.id.tv_selected_establishment);
        sharedPreferences = ((MainActivity) getActivity()).getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        encryptedPreferences = new EncryptedPreferences(getActivity());

        selectedEstablishment.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(selectedEstablishment.getText())) {
                String code = selectedEstablishment.getText().toString().trim();
                Intent intent = new Intent(getActivity(), EstablishmentSummaryActivity.class);
                intent.putExtra("code", code.trim());
                intent.putExtra("isViewMode", true);
                startActivity(intent);
            }
        });

        NavigationView navigationView = ((MainActivity) getActivity()).findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_logout) {
                    logout();
                }

                // Close the navigation drawer when an item is selected.
                DrawerLayout drawerLayout = ((MainActivity) getActivity()).findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawers();

                return true;
            }
        });


    }
    @Override
    public void onResume() {
        super.onResume();
        String defaultEstablishment = sharedPreferences.getString(Constants.DEFAULT_ESTABLISHMENT, "");
        selectedEstablishment.setText(defaultEstablishment);
    }
}





