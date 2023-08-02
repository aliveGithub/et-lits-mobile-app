package org.moa.etlits.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.moa.etlits.LanguageActivity;
import org.moa.etlits.R;
import org.moa.etlits.api.RetrofitClient;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button btnShowAnimals, btnAPICall;
    private Context context;
    private Resources resources;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnShowAnimals = findViewById(R.id.btnAnimals);
        btnAPICall = findViewById(R.id.btn_call_api);
        drawerLayout = findViewById(R.id.main_drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnShowAnimals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnimalListActivity.class);
                startActivity(intent);
            }
        });

        btnAPICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitClient.getConfigService("test@liferay.com", "aQJR8C2V");
            }
        });
    }

    //public static final int LANGUAGE_MENU_ITEM = R.id.nav_language;
    public static final int CCC = 1000012;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if  (item.getItemId() == R.id.nav_language) {
            Intent intent = new Intent(MainActivity.this, LanguageActivity.class);
            startActivity(intent);
            return true;
        }
        /*switch(item.getItemId()){
            case R.id.nav_language:
                Intent intent = new Intent(MainActivity.this, LanguageActivity.class);
                startActivity(intent);
                return true;
            default:
                break;
        }*/
        return super.onOptionsItemSelected(item);
    }
}