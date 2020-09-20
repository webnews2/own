package com.github.webnews2.own.ui.titles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.webnews2.own.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

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

        return root;
    }
}