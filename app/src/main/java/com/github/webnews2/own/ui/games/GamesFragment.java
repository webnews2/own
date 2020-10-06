package com.github.webnews2.own.ui.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

// TODO: Add details view, maybe as floating cardview
// TODO: Add possibility to edit titles

public class GamesFragment extends Fragment {

    public GamesFragment() {
        // Required empty public constructor
    }

    public View onCreateView( LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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

        lvGames.setLongClickable(true);
        lvGames.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Title t = (Title) parent.getItemAtPosition(position);
                DBHelper dbh = DBHelper.getInstance();

                // Show alert dialog informing user about deletion of possible connections with platforms and title itself
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.games_delete_dialog_title)
                        .setMessage(R.string.games_delete_dialog_msg)
                        .setPositiveButton(R.string.lbl_delete, (dialog, which) -> {
                            // Delete title
                            boolean deleted = dbh.deleteTitle(t.getID(), t.isOnWishList());

                            // If db operation was successful > reload games
                            if (deleted) {
                                DataHolder.getInstance().updateGames(gamesAdapter);
                            }
                            // Something went wrong while deleting game > inform user
                            else Snackbar.make(view, R.string.games_delete_error, Snackbar.LENGTH_LONG).show();
                        })
                        .setNegativeButton(R.string.lbl_cancel, (dialog, which) -> {
                            dialog.dismiss();
                        })
                .show();

                return true;
            }
        });

        // Find FAB and set click method
        FloatingActionButton fab = root.findViewById(R.id.fabAddGame);
        fab.setOnClickListener(view -> {
            // Open new dialog fragment for adding a game title
            AddEditGameFragment newTitle = new AddEditGameFragment();
            newTitle.setOnDismissListener(dialog -> DataHolder.getInstance().updateGames(gamesAdapter));
            newTitle.show(getChildFragmentManager(), "");
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.manage_platforms_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_manage_platforms) {
            Navigation.findNavController(getView()).navigate(R.id.action_navigation_games_to_platformsFragment);

            Snackbar.make(getView(), "Clicked", Snackbar.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}