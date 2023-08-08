package org.moa.etlits.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;

import org.moa.etlits.R;
import org.moa.etlits.ui.activities.AnimalListActivity;
import org.moa.etlits.ui.activities.MainActivity;
import org.moa.etlits.ui.viewmodels.login.LoginResult;
import org.moa.etlits.ui.viewmodels.login.LoginViewModel;
import org.moa.etlits.ui.viewmodels.login.LoginViewModelFactory;

public class HomeFragment extends Fragment {

    private NavigationView navigationView;

    private LoginViewModel loginViewModel;

    private TextView homeMsg;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        return v;
    }

    private void navigateToLogin() {
        //FragmentManager fm = getFragmentManager();
        FragmentManager fm = this.getParentFragmentManager();
        if (fm != null) {
            fm.beginTransaction()
                    .replace(R.id.container, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        loginViewModel = new ViewModelProvider(getActivity(), new LoginViewModelFactory())
                .get(LoginViewModel.class);
        NavigationView navigationView = ((MainActivity) getActivity()).findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                if (item.getItemId() == R.id.nav_settings) {
                    Intent intent = new Intent(getActivity(), AnimalListActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_logout) {
                    loginViewModel.logout();
                    navigateToLogin();

                }

                // Close the navigation drawer when an item is selected.
                DrawerLayout drawerLayout = ((MainActivity) getActivity()).findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawers();

                return true;
            }
        });

        homeMsg = view.findViewById(R.id.tv_home_message);
        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {
                if (loginResult != null && loginResult.isLoggedIn()) {
                    homeMsg.setText("Welcome:" + loginResult.getUsername());
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((MainActivity) getActivity()).enableDrawer();
    }
}





