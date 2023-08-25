package org.moa.etlits.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.moa.etlits.R;
import org.moa.etlits.ui.activities.AnimalListActivity;
import org.moa.etlits.ui.activities.LoginActivity;
import org.moa.etlits.ui.activities.MainActivity;
import org.moa.etlits.utils.EncryptedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class HomeTabsFragment extends Fragment {
    private Fragment activeFragment;
    private Fragment dashboardFragment;
    private Fragment syncFragment;
    private Fragment moveFragment;
    private Fragment animalsFragment;

    private AlertDialog.Builder builder;

    private EncryptedPreferences encryptedPreferences;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_tabs, container, false);
        initializeBottomNavigation(v);
        return v;
    }

    private void initializeBottomNavigation(View v) {

        BottomNavigationView bottomNavigationView = v.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_btm_home) {
                loadFragment(dashboardFragment);
                return true;
            } else if(item.getItemId() == R.id.navigation_sync) {
                loadFragment(syncFragment);
                return true;
            } else if (item.getItemId() == R.id.navigation_move) {
                loadFragment(moveFragment);
                return true;
            } else if (item.getItemId() == R.id.navigation_animals) {
                loadFragment(animalsFragment);
                return true;
            }

            return false;
        });

        // Initialize fragments
        dashboardFragment = new HomeFragment();
        syncFragment = new SyncFragment();
        moveFragment = new MoveFragment();
        animalsFragment = new AnimalsFragment();
        activeFragment = dashboardFragment;


        // Load the default fragment (HomeChildFragment)
        getChildFragmentManager().beginTransaction().add(R.id.home_fragment_container, dashboardFragment, "home").commit();
        getChildFragmentManager().beginTransaction().add(R.id.home_fragment_container, syncFragment, "sync").hide(syncFragment).commit();
        getChildFragmentManager().beginTransaction().add(R.id.home_fragment_container, animalsFragment, "animals").hide(animalsFragment).commit();
        getChildFragmentManager().beginTransaction().add(R.id.home_fragment_container, moveFragment, "move").hide(moveFragment).commit();

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
                encryptedPreferences.remove("username");
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

        encryptedPreferences = new EncryptedPreferences(getActivity());
        NavigationView navigationView = ((MainActivity) getActivity()).findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                if (item.getItemId() == R.id.nav_settings) {
                    Intent intent = new Intent(getActivity(), AnimalListActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_logout) {
                    logout();
                }

                // Close the navigation drawer when an item is selected.
                DrawerLayout drawerLayout = ((MainActivity) getActivity()).findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawers();

                return true;
            }
        });
    }
}





