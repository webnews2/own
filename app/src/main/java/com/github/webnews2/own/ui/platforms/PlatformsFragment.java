package com.github.webnews2.own.ui.platforms;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.github.webnews2.own.MainActivity;
import com.github.webnews2.own.R;
import com.github.webnews2.own.utilities.DBHelper;
import com.github.webnews2.own.utilities.Platform;
import com.github.webnews2.own.utilities.adapters.PlatformsAdapter;
import com.google.android.material.snackbar.Snackbar;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlatformsFragment extends Fragment {

    public PlatformsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_platforms, container, false);

        // CHECK: There has to be a more elegant way of hiding the bottom navigation
        // Hide bottom navigation on this page
        getActivity().findViewById(R.id.nav_view).setVisibility(View.GONE);

        // Set up adapter for list view
        PlatformsAdapter platformsAdapter = new PlatformsAdapter(MainActivity.lsPlatforms, getContext());

        // TODO: Remember adapter position > http://vikinghammer.com/2011/06/17/android-listview-maintain-your-scroll-position-when-you-refresh/
        // Set up list view by adding header and adapter
        ListView lvPlatforms = root.findViewById(R.id.lvPlatforms);
        lvPlatforms.addHeaderView(inflater.inflate(R.layout.row_action_primary, lvPlatforms, false), null, false);
        lvPlatforms.setAdapter(platformsAdapter);

        // Find views in order to manipulate them
        AppCompatImageButton ibActionFirstLeft = lvPlatforms.findViewById(R.id.ibActionFirstLeft);
        AppCompatEditText etActionFirst = lvPlatforms.findViewById(R.id.etActionFirst);
        AppCompatImageButton ibActionFirstRight = lvPlatforms.findViewById(R.id.ibActionFirstRight);

        // Set click method for button on left side of input UI > just handles UI changes
        ibActionFirstLeft.setOnClickListener(v -> {
            // If input field is focused > reset input UI by calling onFocusChange
            if (etActionFirst.isFocused()) {
                etActionFirst.clearFocus();
                UIUtil.hideKeyboard(getContext(), etActionFirst);
            }
            // Otherwise input field will be focused
            else {
                etActionFirst.requestFocus();
                UIUtil.showKeyboard(getContext(), etActionFirst);
            }
        });

        // Set hint > layout is used multiple times
        etActionFirst.setHint(R.string.platforms_add);
        // Set focus change method > just handles UI changes
        etActionFirst.setOnFocusChangeListener((v, hasFocus) -> {
            // If input field is focused > enable "Add a new platform" UI
            if (hasFocus) {
                ibActionFirstLeft.setImageResource(R.drawable.ic_close_white_24dp);
                ibActionFirstLeft.setColorFilter(getResources().getColor(R.color.color_red, null));

                ibActionFirstRight.setVisibility(View.VISIBLE);
            }
            // Input field is not focused > reset UI to initial state
            else {
                ibActionFirstLeft.setImageResource(R.drawable.ic_add_white_24dp);
                ibActionFirstLeft.setColorFilter(getResources().getColor(R.color.color_light_grey, null));

                etActionFirst.setHint(R.string.platforms_add);
                etActionFirst.setText(null);

                ibActionFirstRight.setVisibility(View.INVISIBLE);
            }
        });

        // Set soft input action button (bottom right) to done (tick)
        etActionFirst.setImeOptions(EditorInfo.IME_ACTION_DONE);
        // Set label for input action button, only visible in landscape mode
        etActionFirst.setImeActionLabel(getResources().getString(R.string.lbl_add), EditorInfo.IME_ACTION_DONE);
        // Set click method for input action button
        etActionFirst.setOnEditorActionListener((v, actionId, event) -> {
            // FIXME: Doesn't work when pressing ENTER on a hardware keyboard, low priority
            // If pressed button is defined input action button > call click method of add button (right side)
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                ibActionFirstRight.performClick();
                return true;
            }
            return false;
        });

        // Set click method for button on right side of input UI > adds new platforms to db or resets input UI
        ibActionFirstRight.setOnClickListener(v -> {
            // If check button is visible and input field is focused
            if (v.getVisibility() == View.VISIBLE && etActionFirst.isFocused()) {
                /* CHECK: There seems to be a bug. Sometimes when there are at least 2 trailing whitespaces
                          getText() returns an empty string. Somehow this gets fixed by using the trim() method. */
                String input = etActionFirst.getText().toString().trim();

                // If input field is empty > reset input UI
                if (TextUtils.isEmpty(input)) {
                    etActionFirst.clearFocus();
                    UIUtil.hideKeyboard(getContext(), etActionFirst);
                }
                else {
                    // Check if platform already exists > gets added to list if true
                    List<Platform> lsContained = MainActivity.lsPlatforms.stream()
                            .filter(platform -> platform.getName().equals(input)).collect(Collectors.toList());

                    // Platform doesn't exist
                    if (lsContained.size() < 1) {
                        // Add platform to db
                        DBHelper dbh = DBHelper.getInstance(getContext());
                        long platformID = dbh.addPlatform(new Platform(-1, input));

                        // If db operation was successful > update platforms list and reset input UI
                        if (platformID != -1) {
                            MainActivity.updatePlatforms(getContext());
                            platformsAdapter.notifyDataSetChanged();

                            etActionFirst.clearFocus();
                            UIUtil.hideKeyboard(getContext(), etActionFirst);
                        }
                        // Something went wrong while adding to db > inform user
                        else Snackbar.make(root, R.string.platforms_add_error, Snackbar.LENGTH_LONG).show();
                    }
                    // Platform already exists > inform user
                    else Snackbar.make(root, R.string.platforms_exists, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }
}