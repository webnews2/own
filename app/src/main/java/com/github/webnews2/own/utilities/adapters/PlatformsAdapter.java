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
import com.github.webnews2.own.utilities.Platform;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This adapter class is used to provide the functionality of updating, deleting and displaying the user's platforms. It
 * extends the {@link BaseAdapter} to provide basic functionality.
 *
 * @author Kevin Kleiber (m26675)
 * @version 1.0
 */
public class PlatformsAdapter extends BaseAdapter {
    /**
     * Holds the view of this adapter class and is mainly used to improve performance.
     */
    private static class ViewHolder {
        // Views of the adapter
        AppCompatImageButton ibActionLeft;
        AppCompatEditText etAction;
        AppCompatImageButton ibActionRight;

        /**
         * Constructs a ViewHolder object which uses the provided view to find and access the views of this adapter's
         * layout.
         *
         * @param p_view view to use for finding the layout views
         */
        public ViewHolder(View p_view) {
            ibActionLeft = p_view.findViewById(R.id.ibActionLeft);
            etAction = p_view.findViewById(R.id.etAction);
            ibActionRight = p_view.findViewById(R.id.ibActionRight);
        }

    }

    // Tag for (logcat) information logging
    private static final String TAG = PlatformsAdapter.class.getSimpleName();

    // Field of the adapter
    private List<Platform> lsPlatforms;
    private Context context;

    /**
     * Constructs a new PlatformsAdapter object which uses the ViewHolder pattern to improve the performance. It
     * provides update, delete and display functionality for entries of the provided platforms list. The context is used
     * for various operations like hiding and showing the keyboard.
     *
     * @param p_lsPlatforms list which contains the user's platforms
     * @param p_context context to use for operations like dialog building, color changes, etc.
     */
    public PlatformsAdapter(List<Platform> p_lsPlatforms, Context p_context) {
        lsPlatforms = p_lsPlatforms;
        context = p_context;
    }

    /**
     * How many items are in the data set represented by this adapter.
     *
     * @return count of items
     */
    @Override
    public int getCount() {
        return lsPlatforms.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position position of item whose data we want within adapter's data set
     * @return data at the specified position
     */
    @Override
    public Platform getItem(int position) {
        return lsPlatforms.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position position of item within adapter's data set whose row id we want
     * @return id of item at specified position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns a view which displays the data at the specified position of the list provided for this adapter. It also
     * provides functionality for updating and deleting single entries of the list which will be updated according to
     * the new data. This method makes use of the ViewHolder pattern for adapters to improve the overall performance.
     *
     * @param position position of item within adapter's data set of item whose view we want
     * @param convertView old view to reuse, if possible
     * @param parent parent that this view will eventually be attached to
     * @return view corresponding to the data at the specified position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        // Inflate view using the specified reusable layout if its empty and apply the ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_action_secondary, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }
        // Use the ViewHolder if this view was previously created
        else {
            vh = (ViewHolder) convertView.getTag();
        }

        // Get platform and instance of DBHelper for this platforms list entry
        final Platform p = getItem(position);
        DBHelper dbh = DBHelper.getInstance();

        // Hide left button on recycled views if it is visible
        if (vh.ibActionLeft.getVisibility() == View.VISIBLE) vh.ibActionLeft.setVisibility(View.INVISIBLE);

        // Set click method for button on left side of input UI > deletes platforms
        vh.ibActionLeft.setOnClickListener(v -> {
            // Show alert dialog informing user about deletion of possible connections and the platform itself
            new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.platforms_delete_dialog_title)
                    .setMessage(R.string.platforms_delete_dialog_msg)
                    .setPositiveButton(R.string.lbl_delete, (dialog, which) -> {
                        // Delete platform
                        boolean deleted = dbh.deletePlatform(p.getID());

                        // If db operation was successful > reload platforms list and reset input UI
                        if (deleted) {
                            DataHolder.getInstance().updatePlatforms(this);

                            vh.etAction.clearFocus();
                            UIUtil.hideKeyboard(context, v);
                        }
                        // Something went wrong while deleting platform > inform user
                        else Snackbar.make(v, R.string.platforms_delete_error, Snackbar.LENGTH_LONG).show();
                    })
                    .setNegativeButton(R.string.lbl_cancel, (dialog, which) -> {
                        // Reset input UI
                        vh.etAction.clearFocus();
                        UIUtil.hideKeyboard(context, v);
                    })
            .show();
        });

        vh.etAction.setText(p.getName());
        // Set focus change method > just handles UI changes
        vh.etAction.setOnFocusChangeListener((v, hasFocus) -> {
            // If input field is focused > enable update/delete UI
            if (hasFocus) {
                vh.ibActionLeft.setVisibility(View.VISIBLE);

                vh.ibActionRight.setImageResource(R.drawable.ic_check_white_24dp);
                vh.ibActionRight.setColorFilter(context.getResources().getColor(R.color.color_green, null));
            }
            // Input field is not focused > reset UI to initial state
            else {
                vh.ibActionLeft.setVisibility(View.INVISIBLE);

                vh.etAction.setText(p.getName());

                vh.ibActionRight.setImageResource(R.drawable.ic_edit_white_24dp);
                vh.ibActionRight.setColorFilter(context.getResources().getColor(R.color.color_light_grey, null));
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

        // Set click method for button on right side of input UI > update platform or reset input UI
        vh.ibActionRight.setOnClickListener(v -> {
            // If delete button is visible and input field is focused
            if (vh.ibActionLeft.getVisibility() == View.VISIBLE && vh.etAction.isFocused()) {
                /* CHECK: There seems to be a bug. Sometimes when there are at least 2 trailing whitespaces
                          getText() returns an empty string. Somehow this gets fixed by using the trim() method. */
                String input = vh.etAction.getText().toString().trim();

                // If input field is empty > inform user
                if (TextUtils.isEmpty(input))
                    Snackbar.make(v, R.string.msg_field_required, Snackbar.LENGTH_LONG).show();
                // If input is current platform > reset input UI
                else if (input.equals(p.getName())) {
                    vh.etAction.clearFocus();
                    UIUtil.hideKeyboard(context, v);
                }
                else {
                    // Check if platform already exists > gets added to list if true
                    List<Platform> lsContained = DataHolder.getInstance().getPlatforms().stream()
                            .filter(platform -> platform.getName().equals(input)).collect(Collectors.toList());

                    // Platform doesn't exist
                    if (lsContained.size() < 1) {
                        // Update platform
                        boolean updated = dbh.updatePlatform(p.getID(), input);

                        // If db operation was successful > update platforms list and reset input UI
                        if (updated) {
                            DataHolder.getInstance().updatePlatforms(this);

                            vh.etAction.clearFocus();
                            UIUtil.hideKeyboard(context, v);
                        }
                        // Something went wrong while updating db > inform user
                        else Snackbar.make(v, R.string.platforms_update_error, Snackbar.LENGTH_LONG).show();
                    }
                    // Platform already exists > inform user
                    else Snackbar.make(v, R.string.platforms_exists, Snackbar.LENGTH_LONG).show();
                }
            }
            // Enable input UI
            else {
                vh.etAction.requestFocus();
                UIUtil.showKeyboard(context, vh.etAction);
            }
        });

        // Return new or recycled view containing all relevant data
        return convertView;
    }
}
