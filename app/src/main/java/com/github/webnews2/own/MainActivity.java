package com.github.webnews2.own;

import android.os.Bundle;

import com.github.webnews2.own.utilities.DBHelper;
import com.github.webnews2.own.utilities.Platform;
import com.github.webnews2.own.utilities.Title;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<Title> lsTitles;
    public static List<Platform> lsPlatforms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Get data from db
        DBHelper dbh = DBHelper.getInstance(this);
        lsTitles = new ArrayList<>(dbh.getTitles());
        lsPlatforms = new ArrayList<>(dbh.getPlatforms());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_wishlist, R.id.navigation_games, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}