package com.github.webnews2.own.ui.titles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.github.webnews2.own.R;
import com.github.webnews2.own.ui.platforms.PlatformsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

// TODO: Add details view, maybe as floating cardview
// TODO: Add possibility to edit titles

public class TitlesFragment extends Fragment {

    private List<String> lGames;

    private ListView lvGames = null;
    private ArrayAdapter<String> aaGames;

    public TitlesFragment() {
        // Required empty public constructor
    }

    public View onCreateView( LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        lGames = new ArrayList<>();
        lGames.add("Fortnite");
        lGames.add("Fallout 3");
        lGames.add("Grand Theft Auto 5");

        View root = inflater.inflate(R.layout.fragment_titles, container, false);

        getActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);

        lvGames = root.findViewById(R.id.lvGames);

        // TODO: New adapter necessary
//        aaGames = new ArrayAdapter<>(getContext(), R.layout.row, lGames);
//
//        lvGames.setAdapter(aaGames);

        FloatingActionButton fab = root.findViewById(R.id.fabAddGame);
        fab.setOnClickListener(view -> {
//                lGames.add("Test");
//                aaGames.notifyDataSetChanged();
//                Title test = new Title("Fortnite", null, false, null);
//
//                if (DBHelper.getInstance(getContext()).addGame(test)) {
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }

            AddTitleFragment newTitle = AddTitleFragment.newInstance("", "");
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