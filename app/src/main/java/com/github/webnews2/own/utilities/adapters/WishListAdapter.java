package com.github.webnews2.own.utilities.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;

import com.github.webnews2.own.R;
import com.github.webnews2.own.utilities.DBHelper;
import com.github.webnews2.own.utilities.DataHolder;
import com.github.webnews2.own.utilities.Title;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.List;
import java.util.stream.Collectors;

public class WishListAdapter extends BaseAdapter {

    private static class ViewHolder {
        AppCompatImageButton ibActionLeft;
        AppCompatEditText etAction;
        AppCompatImageButton ibActionRight;

        public ViewHolder(View p_view) {
            //
            ibActionLeft = p_view.findViewById(R.id.ibActionLeft);
            etAction = p_view.findViewById(R.id.etAction);
            ibActionRight = p_view.findViewById(R.id.ibActionRight);
        }
    }

    private List<Title> lsTitles;
    private Context context;

    public WishListAdapter(List<Title> p_lsTitles, Context p_context) {
        lsTitles = p_lsTitles;
        context = p_context;
    }

    @Override
    public int getCount() {
        return lsTitles.size();
    }

    @Override
    public Title getItem(int position) {
        return lsTitles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_action_secondary, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolder) convertView.getTag();
        }

        final Title t = getItem(position);
        DBHelper dbh = DBHelper.getInstance();

        // Hide left button on recycled views if it is visible
        if (vh.ibActionLeft.getVisibility() == View.VISIBLE) vh.ibActionLeft.setVisibility(View.INVISIBLE);

        // Set click method for button on left side of input UI > deletes wish list title
        vh.ibActionLeft.setOnClickListener(v -> {
            // Show alert dialog informing user about deletion of possible connections with platforms and title itself
            new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.wishlist_delete_dialog_title)
                    .setMessage(R.string.platforms_delete_dialog_msg)
                    .setPositiveButton(R.string.lbl_delete, (dialog, which) -> {
                        // Delete title
                        boolean deleted = dbh.deleteTitle(t.getId(), t.isOnWishList());

                        // If db operation was successful > reload wish list and reset input UI
                        if (deleted) {
                            DataHolder.getInstance().updateWishList(this);

                            vh.etAction.clearFocus();
                            UIUtil.hideKeyboard(context, vh.etAction);
                        }
                        // Something went wrong while deleting title > inform user
                        else Snackbar.make(v, R.string.wishlist_delete_error, Snackbar.LENGTH_LONG).show();
                    })
                    .setNegativeButton(R.string.lbl_cancel, (dialog, which) -> {
                        // Reset input UI
                        vh.etAction.clearFocus();
                        UIUtil.hideKeyboard(context, v);
                    })
            .show();
        });

        vh.etAction.setText(t.getName());
        // Set focus change method > just handles UI changes
        vh.etAction.setOnFocusChangeListener((v, hasFocus) -> {
            // If input field is focuses > enable update/delete UI
            if (hasFocus) {
                vh.ibActionLeft.setVisibility(View.VISIBLE);

                vh.ibActionRight.setImageResource(R.drawable.ic_check_white_24dp);
                vh.ibActionRight.setColorFilter(context.getResources().getColor(R.color.color_green, null));
            }
            // Input field is not focused > reset UI to initial state
            else {
                vh.ibActionLeft.setVisibility(View.INVISIBLE);

                vh.etAction.setText(t.getName());

                vh.ibActionRight.setImageResource(R.drawable.ic_edit_white_24dp);
                vh.ibActionRight.setColorFilter(context.getResources().getColor(R.color.color_light_grey));
            }
        });

        // Set soft input action button (bottom right) to done (tick)
        vh.etAction.setImeOptions(EditorInfo.IME_ACTION_DONE);
        // Set label for input action button, only visible in landscape mode
        vh.etAction.setImeActionLabel(context.getResources().getString(R.string.lbl_update), EditorInfo.IME_ACTION_DONE);
        // Set click method for input action button
        vh.etAction.setOnEditorActionListener((v, actionId, event) -> {
            // FIXME: Doesn't work when pressing ENTER on a hardware keyboard, low priority
            // If pressed button is defined input action button > call click method of update button (right side)
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                vh.ibActionRight.performClick();
                return true;
            }
            return false;
        });

        // Set click method for button on right side of input UI > updated title or reset input UI
        vh.ibActionRight.setOnClickListener(v -> {
            // If delete button is visible and input field is focused
            if (vh.ibActionLeft.getVisibility() == View.VISIBLE && vh.etAction.isFocused()) {
                /* CHECK: There seems to be a bug. Sometimes when there are at least 2 trailing whitespaces
                          getText() returns an empty string. Somehow this gets fixed by using the trim() method. */
                String input = vh.etAction.getText().toString().trim();

                // If input field is empty > inform user
                if (TextUtils.isEmpty(input)) {
                    Snackbar.make(v, R.string.msg_field_required, Snackbar.LENGTH_LONG).show();
                }
                // If input is current title > reset input UI
                else if (input.equals(t.getName())) {
                    vh.etAction.clearFocus();
                    UIUtil.hideKeyboard(context, v);
                }
                else {
                    // Check if title already exists (no matter if as wish list or normal title) > gets added to list if true
                    List<Title> lsContained = DataHolder.getInstance().getWishList().stream()
                            .filter(title -> title.getName().equals(input)).collect(Collectors.toList());

                    // Title doesn't exist
                    if (lsContained.size() < 1) {
                        // Change title name
                        t.setName(input);

                        // Update wish list title
                        boolean updated = dbh.updateTitle(t);

                        // If db operation was successful > update wish list and reset input UI
                        if (updated) {
                            DataHolder.getInstance().updateWishList(this);

                            vh.etAction.clearFocus();
                            UIUtil.hideKeyboard(context, v);
                        }
                        // Something went wrong while updating db > inform user
                        else Snackbar.make(v, R.string.wishlist_update_error, Snackbar.LENGTH_LONG).show();
                    }
                    // Title already exists > inform user
                    else {
                        // TODO: Change when new data structure is implemented
                        // Get title out of list, there should only be one entry
                        Title w = lsContained.get(0);

                        // Distinguish between wish list and normal titles
                        if (w.isOnWishList()) Snackbar.make(v, R.string.wishlist_title_exists, Snackbar.LENGTH_LONG).show();
                        else Snackbar.make(v, R.string.wishlist_title_owned, Snackbar.LENGTH_LONG).show();
                    }
                }
            }
            // Enable input UI
            else {
                vh.etAction.requestFocus();
                UIUtil.showKeyboard(context, vh.etAction);
            }
        });

        return convertView;
    }
}
