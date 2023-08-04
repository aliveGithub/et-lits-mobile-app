package org.moa.etlits.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;

import org.moa.etlits.R;
import org.moa.etlits.ui.activities.AnimalListActivity;
import org.moa.etlits.ui.activities.MainActivity;

public class HomeFragment extends Fragment {

    private NavigationView navigationView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);


        NavigationView navigationView = ((MainActivity) getActivity()).findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                if (item.getItemId() == R.id.nav_settings) {
                    Intent intent = new Intent(getActivity(), AnimalListActivity.class);
                    startActivity(intent);
                }

                // Close the navigation drawer when an item is selected.
                DrawerLayout drawerLayout = ((MainActivity) getActivity()).findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawers();

                return true;
            }
        });

        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((MainActivity) getActivity()).enableDrawer();
    }
}





