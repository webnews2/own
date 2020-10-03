package com.github.webnews2.own.ui.wish_list;

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
import com.github.webnews2.own.utilities.Title;
import com.github.webnews2.own.utilities.adapters.WishListAdapter;
import com.google.android.material.snackbar.Snackbar;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.List;
import java.util.stream.Collectors;


public class WishListFragment extends Fragment {

    public WishListFragment() {
        // Required empty public constructor
    }

    public View onCreateView( LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_wish_list, container, false);

        // Set up adapter for list view
        WishListAdapter wishListAdapter = new WishListAdapter(MainActivity.lsTitles, getContext());

        // TODO: see PlatformsFragment for more information
        // Set up list view by adding header and adapter
        ListView lvWishList = root.findViewById(R.id.lvWishList);
        lvWishList.addHeaderView(inflater.inflate(R.layout.row_action_primary, lvWishList, false), null, false);
        lvWishList.setAdapter(wishListAdapter);

        // Find views in order to manipulate them
        AppCompatImageButton ibActionFirstLeft = lvWishList.findViewById(R.id.ibActionFirstLeft);
        AppCompatEditText etActionFirst = lvWishList.findViewById(R.id.etActionFirst);
        AppCompatImageButton ibActionFirstRight = lvWishList.findViewById(R.id.ibActionFirstRight);

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
        etActionFirst.setHint(R.string.wishlist_add);
        // Set focus change method > just handles UI changes
        etActionFirst.setOnFocusChangeListener((v, hasFocus) -> {
            // If input field is focused > enable "Add a new wish list title" UI
            if (hasFocus) {
                ibActionFirstLeft.setImageResource(R.drawable.ic_close_white_24dp);
                ibActionFirstLeft.setColorFilter(getResources().getColor(R.color.color_red, null));

                ibActionFirstRight.setVisibility(View.VISIBLE);
            }
            // Input field is not focused > reset UI to initial state
            else {
                ibActionFirstLeft.setImageResource(R.drawable.ic_add_white_24dp);
                ibActionFirstLeft.setColorFilter(getResources().getColor(R.color.color_light_grey));

                etActionFirst.setHint(R.string.wishlist_add);
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

        // Set click method for button on right side of input UI > adds new wish list title to db or resets input UI
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
                    // Check if title already exists (no matter if as wish list or normal title) > gets added to list if true
                    List<Title> lsContained = MainActivity.lsTitles.stream()
                            .filter(title -> title.getName().equals(input)).collect(Collectors.toList());

                    // Title doesn't exist
                    if (lsContained.size() < 1) {
                        // Add title to wish list
                        DBHelper dbh = DBHelper.getInstance(getContext());
                        long titleID = dbh.addTitle(new Title(-1, input, null, true, null));

                        // If db operation was successful > update wish list and reset input UI
                        if (titleID != -1) {
                            MainActivity.updateTitles(getContext());
                            wishListAdapter.notifyDataSetChanged();

                            etActionFirst.clearFocus();
                            UIUtil.hideKeyboard(getContext(), etActionFirst);
                        }
                        // Something went wrong while adding to db > inform user
                        else Snackbar.make(root, R.string.wishlist_add_error, Snackbar.LENGTH_LONG).show();
                    }
                    // Title already exists > inform user
                    else {
                        // TODO: Change when new data structure is implemented
                        // Get title out of list, there should only be one entry
                        Title t = lsContained.get(0);

                        // Distinguish between wish list and normal titles
                        if (t.isOnWishList()) Snackbar.make(root, R.string.wishlist_title_exists, Snackbar.LENGTH_LONG).show();
                        else Snackbar.make(root, R.string.wishlist_title_owned, Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        return root;
    }
}