package com.github.webnews2.own.utilities;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;

import com.github.webnews2.own.MainActivity;
import com.github.webnews2.own.R;
import com.google.android.material.snackbar.Snackbar;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.List;
import java.util.stream.Collectors;

// TODO: Fix UI state on scroll
// DONE: Improve performance, https://guides.codepath.com/android/Using-a-BaseAdapter-with-ListView
public class PlatformsAdapter extends BaseAdapter {

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

    private List<Platform> lsPlatforms;
    private Context context;

    public PlatformsAdapter(List<Platform> p_lsPlatforms, Context p_context) {
        lsPlatforms = p_lsPlatforms;
        context = p_context;
    }

    @Override
    public int getCount() {
        return lsPlatforms.size();
    }

    @Override
    public Platform getItem(int position) {
        return lsPlatforms.get(position); // necessary as db ids don't start with zero
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

        final Platform p = getItem(position);

        // Hide left button on recycled views if it is visible
        if (vh.ibActionLeft.getVisibility() == View.VISIBLE) vh.ibActionLeft.setVisibility(View.INVISIBLE);

        // Set click method for button on left side of input UI > deletes platforms
        vh.ibActionLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show alert dialog informing user about deletion of possible connections and the platform itself
            }
        });

        //vh.etAction.setTag(p);
        vh.etAction.setText(p.getName());
        // Set focus change method > just handles UI changes
        vh.etAction.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
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
            }
        });

        // Set soft input action button (bottom right) to done (tick)
        vh.etAction.setImeOptions(EditorInfo.IME_ACTION_DONE);
        // Set label for input action button, only visible in landscape mode
        vh.etAction.setImeActionLabel("Update", EditorInfo.IME_ACTION_DONE);
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

        // Set click method for button on right side of input UI > updates platforms or resets input UI
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
                    UIUtil.hideKeyboard(context, vh.etAction);
                }
                else {
                    // Check if platform already exists > gets added to list if true
                    List<Platform> lsContained = MainActivity.lsPlatforms.stream()
                            .filter(platform -> platform.getName().equals(input)).collect(Collectors.toList());

                    // Platform doesn't exist
                    if (lsContained.size() < 1) {
                        // Update platform
                        DBHelper dbh = DBHelper.getInstance(context);
                        boolean updated = dbh.updatePlatform(p.getId(), input);

                        // If db operation was successful > update platforms list and reset input UI
                        if (updated) {
                            MainActivity.updatePlatforms(context);
                            notifyDataSetChanged();

                            vh.etAction.clearFocus();
                            UIUtil.hideKeyboard(context, vh.etAction);
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

        return convertView;
    }
}
