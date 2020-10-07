package com.github.webnews2.own.ui.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.webnews2.own.R;
import com.github.webnews2.own.utilities.DBHelper;
import com.github.webnews2.own.utilities.DataHolder;
import com.github.webnews2.own.utilities.Title;
import com.github.webnews2.own.utilities.adapters.GamesAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * This fragment is used to display a list of owned games. The user can add, delete and view game title information. At
 * the moment one can only add and delete games. The missing view function will be implemented later on.
 *
 * @author Kevin Kleiber (m26675)
 * @version 1.0
 */
public class GamesFragment extends Fragment {
    /**
     * Constructs a new GamesFragment which can be used for displaying the user's owned games list and manipulate it.
     */
    public GamesFragment() {
        // Required empty public constructor
    }

    /**
     * Handles what happens when the fragment's views and therefore the user interface is going to be created.
     *
     * @param inflater used to inflate the fragment's UI
     * @param container view that the fragment's UI should be attached to
     * @param savedInstanceState if non-null, fragment can be re-constructed from previous saved state
     * @return view of the fragment's UI
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_titles, container, false);

        // CHECK: There has to be a more elegant way of showing the bottom navigation
        // Show bottom navigation on this page
        getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);

        // Set up adapter for list view
        GamesAdapter gamesAdapter = new GamesAdapter(DataHolder.getInstance().getGames(), getContext());

        // Set up list view
        ListView lvGames = root.findViewById(R.id.lvGames);
        lvGames.setAdapter(gamesAdapter);

        // Set item long click method which automatically enables long click
        lvGames.setOnItemLongClickListener((parent, view, position, id) -> {
            // Get title object and instance of DBHelper for this games list entry
            Title t = (Title) parent.getItemAtPosition(position);
            DBHelper dbh = DBHelper.getInstance();

            // Show alert dialog informing user about deletion of possible connections with platforms and game itself
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.games_delete_dialog_title)
                    .setMessage(R.string.games_delete_dialog_msg)
                    .setPositiveButton(R.string.lbl_delete, (dialog, which) -> {
                        // Delete title
                        boolean deleted = dbh.deleteTitle(t.getID(), t.isOnWishList());

                        // If db operation was successful > reload games
                        if (deleted) DataHolder.getInstance().updateGames(gamesAdapter);
                        // Something went wrong while deleting game > inform user
                        else Snackbar.make(view, R.string.games_delete_error, Snackbar.LENGTH_LONG).show();
                    })
                    .setNegativeButton(R.string.lbl_cancel, (dialog, which) -> dialog.dismiss())
            .show();

            // Callback consumed long click
            return true;
        });

        // Find FAB and set click method
        FloatingActionButton fab = root.findViewById(R.id.fabAddGame);
        fab.setOnClickListener(view -> {
            // Open new dialog fragment for adding a game title
            AddEditViewGameFragment newTitle = new AddEditViewGameFragment();
            newTitle.setOnDismissListener(dialog -> DataHolder.getInstance().updateGames(gamesAdapter));
            newTitle.show(getChildFragmentManager(), "");
        });

        // Enable options menu for this fragment
        setHasOptionsMenu(true);

        // Return inflated UI view
        return root;
    }

    /**
     * Handles what happens when the options menu for this fragment is created. In this case the options menu is getting
     * inflated by using a menu resource file.
     *
     * @param menu options menu where the menu items will be added to
     * @param inflater used to inflate the options menu
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.manage_platforms_menu, menu);
    }

    /**
     * Handles what happens when a options menu item was clicked. In this case the fragment will navigate to the
     * platforms fragment using the new navigation component.
     *
     * @param item selected menu item
     * @return true - to consume the callback, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_manage_platforms) {
            Navigation.findNavController(getView()).navigate(R.id.action_navigation_games_to_platformsFragment);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}