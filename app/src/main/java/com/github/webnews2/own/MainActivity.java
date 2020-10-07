package com.github.webnews2.own;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.webnews2.own.utilities.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/* TODO: Remember adapter position in fragments
         http://vikinghammer.com/2011/06/17/android-listview-maintain-your-scroll-position-when-you-refresh/ */

/**
 * This class represents the entry point of the app. It controls the navigation events of the bottom navigation,
 * initializes the DBHelper class and inflates the start fragment.
 *
 * @author Kevin Kleiber (m26675)
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    // Used to manage behaviour of navigation button (upper-left corner)
    private AppBarConfiguration appBarConfiguration;

    /**
     * Handles what happens when the activity starts. In this case the user interface gets built up, the navigation
     * behaviour gets set and the DBHelper class init method gets called.
     *
     * @param savedInstanceState if non-null, activity can be re-constructed from previous saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Initialize object in order to be able to work with the db
        DBHelper.init(this);

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destination
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_wishlist, R.id.navigation_games, R.id.navigation_settings
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    /**
     * Handles what happens the user clicks on the navigation button. In most cases the button will act as a normal back
     * button.
     *
     * @return true if up navigation completed successfully and Activity was finished, false otherwise
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}