package com.github.webnews2.own.ui.titles;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.webnews2.own.R;
import com.github.webnews2.own.utilities.DataHolder;
import com.github.webnews2.own.utilities.adapters.TitlesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

// TODO: Add details view, maybe as floating cardview
// TODO: Add possibility to edit titles

public class TitlesFragment extends Fragment {

    public TitlesFragment() {
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
        TitlesAdapter titlesAdapter = new TitlesAdapter(DataHolder.getInstance().getGames(), getContext());

        // Set up list view
        ListView lvGames = root.findViewById(R.id.lvGames);
        lvGames.setAdapter(titlesAdapter);

        // Find FAB and set click method
        FloatingActionButton fab = root.findViewById(R.id.fabAddGame);
        fab.setOnClickListener(view -> {
            // Open new dialog fragment for adding a game title
            AddTitleFragment newTitle = new AddTitleFragment();
            newTitle.setOnDismissListener(dialog -> titlesAdapter.notifyDataSetChanged());
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