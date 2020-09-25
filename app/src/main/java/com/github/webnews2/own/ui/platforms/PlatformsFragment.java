package com.github.webnews2.own.ui.platforms;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.webnews2.own.MainActivity;
import com.github.webnews2.own.R;
import com.github.webnews2.own.utilities.ActionRowAdapter;
import com.github.webnews2.own.utilities.DBHelper;
import com.github.webnews2.own.utilities.Platform;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlatformsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlatformsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlatformsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActionListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlatformsFragment newInstance(String param1, String param2) {
        PlatformsFragment fragment = new PlatformsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_platforms, container, false);

        getActivity().findViewById(R.id.nav_view).setVisibility(View.INVISIBLE);

        ListView lvPlatforms = root.findViewById(R.id.lvPlatforms);
        lvPlatforms.addHeaderView(inflater.inflate(R.layout.row_action_primary, lvPlatforms, false), null, false);
        lvPlatforms.setAdapter(new ActionRowAdapter(new HashMap<>(MainActivity.lsPlatforms.stream().collect(Collectors.toMap(Platform::getId, Platform::getName))), getContext()));

        AppCompatImageButton ibActionFirstLeft = lvPlatforms.findViewById(R.id.ibActionFirstLeft);
        AppCompatEditText etActionFirst = lvPlatforms.findViewById(R.id.etActionFirst);
        AppCompatImageButton ibActionFirstRight = lvPlatforms.findViewById(R.id.ibActionFirstRight);

        // TODO: Open and close keyboard according to focus state
        ibActionFirstLeft.setOnClickListener(v -> {
            // If the text field is focused directly a click clears the focus by calling onFocusChange
            if (etActionFirst.isFocused()) etActionFirst.clearFocus();
            // Otherwise the text field will be focused
            else etActionFirst.requestFocus();
        });

        etActionFirst.setHint("Add a new platform");
        etActionFirst.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ibActionFirstLeft.setImageResource(R.drawable.ic_close_white_24dp);
                    ibActionFirstLeft.setColorFilter(Color.parseColor("#EF5350"));

                    ibActionFirstRight.setVisibility(View.VISIBLE);
                }
                else {
                    ibActionFirstLeft.setImageResource(R.drawable.ic_add_white_24dp);
                    ibActionFirstLeft.setColorFilter(Color.parseColor("#60000000"));

                    etActionFirst.setError(null);
                    etActionFirst.setHint("Add a new platform");
                    etActionFirst.setText(null);

                    ibActionFirstRight.setVisibility(View.INVISIBLE);
                }
            }
        });

        ibActionFirstRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getVisibility() == View.VISIBLE && etActionFirst.isFocused()) {
                    if (TextUtils.isEmpty(etActionFirst.getText())) etActionFirst.clearFocus();
                    else {
                        // Check if platform already exists, if not add to platform list
                        List<Platform> lsContained = MainActivity.lsPlatforms.stream().filter(platform -> platform.getName().equals(etActionFirst.getText().toString())).collect(Collectors.toList());

                        if (lsContained.size() < 1) {
                            // Add to db
                            DBHelper dbh = DBHelper.getInstance(getContext());
                            long platformID = dbh.addPlatform(new Platform(-1, etActionFirst.getText().toString().trim()));

                            // If platformID doesn't equal -1 > success otherwise error
                        }
                        // Otherwise set error hint
                        else {
                            etActionFirst.setError("The platform was already added.");
                        }
                    }
                }
            }
        });

        return root;
    }
}