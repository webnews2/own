package com.github.webnews2.own.ui.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.github.webnews2.own.R;

public class GamesFragment extends Fragment {

    private String[] games = new String[] {
            "Fortnite", "Fallout 3", "Grand Theft Auto 5"
    };

    private ListView lvGames = null;
    private ArrayAdapter<String> aaGames;

    public GamesFragment() {
        //
    }

    public View onCreateView( LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_games, container, false);

        lvGames = (ListView) root.findViewById(R.id.lvGames);

        // TODO: New adapter necessary
        aaGames = new ArrayAdapter<>(getContext(), R.layout.row, games);

        lvGames.setAdapter(aaGames);

        return root;
    }
}