package com.github.webnews2.own.ui.games;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.github.webnews2.own.R;
import com.github.webnews2.own.utilities.DBHelper;
import com.github.webnews2.own.utilities.DataHolder;
import com.github.webnews2.own.utilities.Platform;
import com.github.webnews2.own.utilities.Title;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;

/*
 * TODO List:
 *  - new fragment structure > trigger different UIs depending on action id
 *  - add details view, maybe as floating card view
 *  - make titles editable, add delete option when editing
 */

/**
 * This fragment is used to add, edit, delete and display game title information. At the moment one can only add titles
 * using this fragment. The missing functions will be added later on. It extends the {@link DialogFragment} and displays
 * the layout as a full-screen modal window.
 *
 * @author Kevin Kleiber (m26675)
 * @version 1.0
 */
public class AddEditViewGameFragment extends DialogFragment {
    // Listener which can be used to add additional functionality to the dismiss method of this dialog
    private DialogInterface.OnDismissListener listener;

    /**
     * Sets the listener to the provided one which can be used to add additional functionality to the
     * {@link #onDismiss(DialogInterface)} method call of this dialog.
     *
     * @param p_listener listener to use for providing additional functionality when closing dialog
     */
    public void setOnDismissListener(DialogInterface.OnDismissListener p_listener) {
        listener = p_listener;
    }

    /**
     * Handles what happens when the event to dismiss the dialog and fragment is triggered. Calls the super class as
     * well as the custom method (if the listener was previously set).
     *
     * @param dialog for which the event should be triggered
     */
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if (listener != null) {
            listener.onDismiss(dialog);
        }
    }

    // Tag for (logcat) information logging
    private static final String TAG = AddEditViewGameFragment.class.getSimpleName();

    // Request codes
    private static final int PICKED_IMAGE = 1;
    private static final int STORAGE_PERMISSION = 1;

    // Views of the add game dialog
    private ImageView ivThumbnail;
    private Uri uriThumbnail;
    private TextInputLayout ilGameTitle;
    private TextInputEditText etGameTitle;
    private TextInputEditText etLocation;
    private ChipGroup cgPlatforms;

    /**
     * Handles what happens when the event to create the dialog and fragment is triggered. Calls the super class method
     * and sets the style for this dialog.
     *
     * @param savedInstanceState if fragment is being re-created from a previous saved state, this is the state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    /**
     * Handles what happens when the dialog and fragment are going to be visible to the user. Calls the super class
     * method and changes some style attributes.
     */
    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    /**
     * Handles what happens when the fragment's views and therefore the user interface is going to be created.
     *
     * @param inflater used to inflate the fragment's UI views
     * @param container view that the fragment's UI should be attached to
     * @param savedInstanceState if non-null, fragment can be re-constructed from previous saved state
     * @return view of the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_title, container, false);

        // Get toolbar and set click functions for closing the dialog and saving the data
        Toolbar toolbar = root.findViewById(R.id.toolbarAddTitles);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setOnMenuItemClickListener(item -> {
            boolean ok = saveData(root);
            if (ok) dismiss();
            return ok;
        });

        // Find UI views in layout
        ivThumbnail = root.findViewById(R.id.ivThumbnail);
        ImageButton ibChooseThumbnail = root.findViewById(R.id.ibChooseThumbnail);
        ilGameTitle = root.findViewById(R.id.ilGameTitle);
        etGameTitle = (TextInputEditText) ilGameTitle.getEditText();
        TextInputLayout ilLocation = root.findViewById(R.id.ilLocation);
        etLocation = (TextInputEditText) ilLocation.getEditText();
        MaterialButton btnChoosePlatforms = root.findViewById(R.id.btnChoosePlatforms);
        cgPlatforms = root.findViewById(R.id.cgPlatforms);

        // Set up choose thumbnail functionality
        ibChooseThumbnail.setOnClickListener(v -> {
            // If permission to access external storage has been granted
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Let user do selection
                selectThumbnail();
            }
            // Permission has not been granted
            else {
                // If user previously denied permission but tries to access the feature again
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain why permission is necessary
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle(R.string.perms_request_dialog_title)
                            .setMessage(R.string.perms_request_dialog_storage_descr)
                            .setPositiveButton(R.string.lbl_ok, (dialog, which) -> requestPermissions(
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    STORAGE_PERMISSION)
                            )
                            .setNegativeButton(R.string.lbl_cancel, (dialog, which) -> dialog.dismiss())
                    .show();
                }
                // Permission was never requested before or was denied permanently
                else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
                }
            }
        });

        // TODO: Think of a good but not limiting input validation for game titles
        /* For now there won't be a real validation of the entered string because of those sometimes very strange and
           very long titles */
        etGameTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                /* CHECK: There seems to be a bug. Sometimes when there are at least 2 trailing whitespaces
                          getText() returns an empty string. Somehow this gets fixed by using the trim() method. */
                // If text is empty > set error message
                if (TextUtils.isEmpty(etGameTitle.getText().toString().trim()))
                    ilGameTitle.setError(getString(R.string.msg_field_required));
                // Text is not empty > clear error message
                else ilGameTitle.setError(null);
            }
        });

        // Store platform names in array, set up array for checked ones and a map for comparison
        String[] arrPlatforms = DataHolder.getInstance().getPlatforms().stream().map(Platform::getName)
                .toArray(String[]::new);
        boolean[] checkedPlatforms = new boolean[arrPlatforms.length]; // Gets updated automatically
        HashMap<Integer, Boolean> mRecently = new HashMap<>();

        // Set click method for choose platforms button
        btnChoosePlatforms.setOnClickListener(v -> {
            // If there is at least one platform in the array
            if (arrPlatforms.length >= 1) {
                // Show dialog for choosing platforms to connect the new game to
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.platforms_choose_dialog_title)
                        .setMultiChoiceItems(arrPlatforms, checkedPlatforms, (dialog, which, isChecked) -> {
                            // If current platform and state are not in map add them
                            if (!mRecently.containsKey(which)) mRecently.put(which, isChecked);
                            // Otherwise override state of current platform
                            else mRecently.replace(which, isChecked);
                        })
                        .setPositiveButton(R.string.lbl_ok, (dialog, which) -> {
                            // Add/Remove platform to/from chip group according to selection
                            for (int i = 0; i < checkedPlatforms.length; i++) {
                                // Find platform chip by tag
                                View v1 = cgPlatforms.findViewWithTag(arrPlatforms[i]);

                                // Platform checked and chip doesn't exist > add to group
                                if (checkedPlatforms[i] && v1 == null) {
                                    Chip platform = new Chip(getContext());
                                    platform.setCheckable(false);
                                    platform.setClickable(false);
                                    platform.setCloseIconVisible(false);
                                    platform.setText(arrPlatforms[i]);
                                    platform.setTag(arrPlatforms[i]);
                                    cgPlatforms.addView(platform);
                                }
                                // Platform unchecked and chip exists > remove from group
                                else if (!checkedPlatforms[i] && v1 != null) {
                                    cgPlatforms.removeView(v1);
                                }
                            }

                            mRecently.clear();
                        })
                        .setNegativeButton(R.string.lbl_cancel, (dialog, which) -> {
                            // Reset current operation
                            for (Map.Entry<Integer, Boolean> e : mRecently.entrySet()) {
                                checkedPlatforms[e.getKey()] = !e.getValue();
                            }

                            mRecently.clear();
                        })
                        .setNeutralButton(R.string.lbl_clear_all, (dialog, which) -> {
                            // Reset selection and clear chip group
                            Arrays.fill(checkedPlatforms, false);
                            mRecently.clear();
                            cgPlatforms.removeAllViews();
                        })
                .show();
            }
            // No platforms > inform user
            else Snackbar.make(v, R.string.games_no_platforms, Snackbar.LENGTH_LONG).show();
        });

        // Return inflated UI view
        return root;
    }

    /**
     * Handles what happens when a previously called intent returns data. In this case the path of the selected image
     * for the current game gets extracted and the image loaded.
     *
     * @param requestCode used to identify where result came from
     * @param resultCode result returned by the activity that was triggered before
     * @param data intent which contains the result data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If request comes from "select image" action and wasn't cancelled
        if (requestCode == PICKED_IMAGE && resultCode == RESULT_OK) {
            // Store uri of image and set image of image view
            uriThumbnail = data.getData();
            if (uriThumbnail != null) Glide.with(this).load(uriThumbnail).into(ivThumbnail);
        }
    }

    /**
     * Handles what happens when a permission request was submitted. In this case the permission for accessing the
     * external storage is checked.
     *
     * @param requestCode used to identify where result came from
     * @param permissions requested permissions
     * @param grantResults contains result for permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request comes from "select image" permission check
        if (requestCode == STORAGE_PERMISSION) {
            // Permission was granted > let user select thumbnail
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) selectThumbnail();
            // Permission was denied > inform user
            else Snackbar.make(getView(), R.string.perms_denied, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Creates a chooser intent with the possibility to pick an app for selecting the thumbnail of the current game.
     */
    private void selectThumbnail() {
        // Create action to get image from an external app activity
        Intent get = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        get.setType("image/*");

        // This will enable the user to pick one of the available image providing applications
        Intent pick = new Intent(Intent.ACTION_PICK);
        pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"); // external to access shareable media

        // Creates intent which lets user choose the app for selecting an image (looks a lot like a modal)
        Intent choose = Intent.createChooser(get, getString(R.string.games_select_image));
        choose.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pick});

        // Check if there are activities which can handle the request
        if (choose.resolveActivity(getActivity().getPackageManager()) != null)
            startActivityForResult(choose, PICKED_IMAGE);
        // No activity found > inform user
        else Snackbar.make(getView(), R.string.games_no_app_found, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Inserts new games and the chosen platforms for it into the database. If this was successful the method will
     * return true to close the dialog and fragment.
     *
     * @param p_view view to use for showing the snack bars
     * @return true - if inserting data into database was successful, false otherwise
     */
    private boolean saveData(View p_view) {
        /* CHECK: There seems to be a bug. Sometimes when there are at least 2 trailing whitespaces
                  getText() returns an empty string. Somehow this gets fixed by using the trim() method. */
        String input = etGameTitle.getText().toString().trim();

        // If input field is empty > set error
        if (TextUtils.isEmpty(input)) ilGameTitle.setError(getString(R.string.msg_field_required));
        else {
            // Check if game title already exists > gets added to list if true
            List<Title> lsContained = DataHolder.getInstance().getTitles().stream()
                    .filter(title -> title.getName().equals(input)).collect(Collectors.toList());

            // Game title doesn't exist
            if (lsContained.size() < 1) {
                DBHelper dbh = DBHelper.getInstance();

                /* CHECK: There seems to be a bug. Sometimes when there are at least 2 trailing whitespaces
                          getText() returns an empty string. Somehow this gets fixed by using the trim() method. */
                String location = etLocation.getText().toString().trim();

                // Add game to owned games
                long titleID = dbh.addTitle(new Title(
                        -1,
                        etGameTitle.getText().toString().trim(),
                        (uriThumbnail == null) ? null : uriThumbnail.toString(),
                        false,
                        TextUtils.isEmpty(location) ? null : location
                ));

                // If game was successfully added > connect it with selected platforms
                if (titleID != -1) {
                    // If at least one platform was selected
                    if (cgPlatforms.getChildCount() >= 1) {
                        List<Platform> lsPlatforms = DataHolder.getInstance().getPlatforms();
                        List<Platform> lsConnectTo = new ArrayList<>();

                        // Connect game and platforms, using chips from chip group
                        for (int i = 0; i < cgPlatforms.getChildCount(); i++) {
                            Chip c = (Chip) cgPlatforms.getChildAt(i);
                            String tag = (String) c.getTag();
                            Platform p = lsPlatforms.stream()
                                    .filter(platform -> platform.getName().equals(tag)).findFirst().orElse(null);
                            if (p != null) lsConnectTo.add(p);
                        }

                        // Connect game and platforms
                        long connResult = dbh.connectTitleAndPlatforms(titleID, lsConnectTo);

                        // If platforms where successfully connected > everything's fine
                        if (connResult != -1) return true;
                        // Something went wrong while adding connections to db > inform user
                        else Snackbar.make(p_view, R.string.games_connect_error, Snackbar.LENGTH_LONG).show();
                    }
                    // No platform selected > everything's fine
                    else return true;
                }
                // Something went wrong while adding to db > inform user
                else Snackbar.make(p_view, R.string.games_add_error, Snackbar.LENGTH_LONG).show();
            }
            else {
                // Get title out of list, there should only be one
                Title t = lsContained.get(0);

                // Distinguish between wish list and normal title
                if (t.isOnWishList()) Snackbar.make(p_view, R.string.title_on_wishlist, Snackbar.LENGTH_LONG).show();
                else Snackbar.make(p_view, R.string.title_owned, Snackbar.LENGTH_LONG).show();
            }
        }
        return false;
    }
}