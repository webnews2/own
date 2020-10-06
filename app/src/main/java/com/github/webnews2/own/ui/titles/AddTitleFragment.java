package com.github.webnews2.own.ui.titles;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;


public class AddTitleFragment extends DialogFragment {
    private DialogInterface.OnDismissListener listener;

    public void setOnDismissListener(DialogInterface.OnDismissListener p_listener) {
        listener = p_listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if (listener != null) {
            listener.onDismiss(dialog);
        }
    }

    // Tag for (logcat) information logging
    private static final String TAG = AddTitleFragment.class.getSimpleName();

    private static final int PICKED_IMAGE = 1;
    private static final int STORAGE_PERMISSION_CODE = 1;

    // Member vars
    private ImageView ivThumbnail;
    private Uri uriThumbnail;
    private ImageButton ibChooseThumbnail;
    private TextInputEditText etGameTitle;
    private TextInputEditText etLocation;
    private MaterialButton btnChoosePlatforms;
    private ChipGroup cgPlatforms;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

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

        // TODO: Implement events for text changes to prevent nulls
        // Set member vars of ui elements
        ivThumbnail = root.findViewById(R.id.ivThumbnail);
        ibChooseThumbnail = root.findViewById(R.id.ibChooseThumbnail);
        TextInputLayout ilGameTitle = root.findViewById(R.id.ilGameTitle);
        etGameTitle = (TextInputEditText) ilGameTitle.getEditText();
        TextInputLayout ilLocation = root.findViewById(R.id.ilLocation);
        etLocation = (TextInputEditText) ilLocation.getEditText();
        btnChoosePlatforms = root.findViewById(R.id.btnChoosePlatforms);
        cgPlatforms = root.findViewById(R.id.cgPlatforms);

        // Set up choose thumbnail functionality
        ibChooseThumbnail.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Let user do selection
                selectThumbnail();
            }
            else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // User denied permission before, now tries to access it again therefore explain why it's necessary
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle(R.string.perms_request_dialog_title)
                            .setMessage(R.string.perms_request_dialog_storage_descr)
                            .setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                                }
                            })
                            .setNegativeButton(R.string.lbl_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .show();
                }
                else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                }
            }
        });

        // TODO: Think of a good but not limiting input validation for game titles
        // For now there won't be a real validation of the entered string because of those sometimes very strange titles
        etGameTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(etGameTitle.getText().toString().trim())) { // CHECK: seems too obvious
                        // Show error message
                        ilGameTitle.setError(getString(R.string.msg_field_required));
                    } else {
                        // Clear error message
                        ilGameTitle.setError(null);
                    }
                }
            }
        });

        String[] arrPlatforms = DataHolder.getInstance().getPlatforms().stream().map(Platform::getName).toArray(String[]::new);
        boolean[] checkedPlatforms = new boolean[arrPlatforms.length];
        HashMap<Integer, Boolean> mRecently = new HashMap<>();

        btnChoosePlatforms.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.platforms_choose_dialog_title)
                .setMultiChoiceItems(arrPlatforms, checkedPlatforms, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (!mRecently.containsKey(which)) mRecently.put(which, isChecked);
                        else mRecently.replace(which, isChecked);
                    }
                })
                .setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Add/Remove platform to/from chip group according to selection
                        for (int i = 0; i < checkedPlatforms.length; i++) {
                            // Find platform chip by tag
                            View v = cgPlatforms.findViewWithTag(arrPlatforms[i]);

                            if (checkedPlatforms[i] && v == null) {
                                // Platform checked > add to group if not in it
                                Chip platform = new Chip(getContext());
                                platform.setCheckable(false);
                                platform.setClickable(false);
                                platform.setCloseIconVisible(false);
                                platform.setText(arrPlatforms[i]);
                                platform.setTag(arrPlatforms[i]);
                                cgPlatforms.addView(platform);
                            }
                            else if (!checkedPlatforms[i] && v != null) {
                                // Platform unchecked > remove from group if in it
                                cgPlatforms.removeView(v);
                            }
                        }

                        mRecently.clear();
                    }
                })
                .setNegativeButton(R.string.lbl_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Reset current operation
                        for (Map.Entry<Integer, Boolean> e : mRecently.entrySet()) {
                            checkedPlatforms[e.getKey()] = !e.getValue();
                        }

                        mRecently.clear();
                    }
                })
                .setNeutralButton(R.string.lbl_clear_all, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Reset selection and clear chip group
                        Arrays.fill(checkedPlatforms, false);
                        cgPlatforms.removeAllViews();
                    }
                })
            .show();
        });


        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If request comes from "select image" action and wasn't cancelled
        if (requestCode == PICKED_IMAGE && resultCode == RESULT_OK) {
            // Store uri of image and set image of image view
            uriThumbnail = data.getData();
            if (uriThumbnail != null) Glide.with(this).load(uriThumbnail).into(ivThumbnail);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                selectThumbnail();
            }
            else {
                // Permission was denied
                Snackbar.make(getView(), R.string.perms_denied, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void selectThumbnail() {
        // Create action to get image from an external app activity
        Intent get = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        get.setType("image/*");

        // This will enable the user to pick one of the available image providing applications
        Intent pick = new Intent(Intent.ACTION_PICK);
        pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"); // external to access shareable media

        // Creates intent which lets user choose the app for selecting an image (looks a lot like a modal)
        Intent choose = Intent.createChooser(get, "Select Image");
        choose.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pick});

        // Check if there are activities which can handle the request
        if (choose.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(choose, "Select Image"), PICKED_IMAGE);
        } else {
            Snackbar.make(getView(), "No suitable app was found.", Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean saveData(View p_view) {
        /* CHECK: There seems to be a bug. Sometimes when there are at least 2 trailing whitespaces
                  getText() returns an empty string. Somehow this gets fixed by using the trim() method. */
        String input = etGameTitle.getText().toString().trim();

        // If input field is empty
        if (TextUtils.isEmpty(input)) {
            etGameTitle.setError(getString(R.string.msg_field_required));
        }
        else {
            // Check if game title already exists > gets added to list if true
            List<Title> lsContained = DataHolder.getInstance().getTitles().stream()
                    .filter(title -> title.getName().equals(input)).collect(Collectors.toList());

            // Game title doesn't exist
            if (lsContained.size() < 1) {
                DBHelper dbh = DBHelper.getInstance();

                // Add game to owned games
                long titleID = dbh.addTitle(new Title(
                        -1,
                        etGameTitle.getText().toString().trim(),
                        (uriThumbnail == null) ? null : uriThumbnail.toString(),
                        false,
                        TextUtils.isEmpty(etLocation.getText().toString().trim()) ? null : etLocation.getText().toString().trim()
                ));

                // If game was successfully added > connect it with selected platforms
                if (titleID != -1) {
                    // TODO: Add check for added platforms
                    List<Platform> lsPlatforms = DataHolder.getInstance().getPlatforms();
                    List<Platform> lsConnectTo = new ArrayList<>();

                    // TODO: Find elegant solution for connecting games and platforms
                    // Connect game and platforms, using chips from chip group
                    for (int i = 0; i < cgPlatforms.getChildCount(); i++) {
                        Chip c = (Chip) cgPlatforms.getChildAt(i);
                        String tag = (String) c.getTag();
                        Platform p = lsPlatforms.stream().filter(platform -> platform.getName().equals(tag)).findFirst().orElse(null);
                        if (p != null) lsConnectTo.add(p);
                    }

                    long connResult = dbh.connectTitleAndPlatforms(titleID, lsConnectTo);

                    if (connResult != -1) {
                        return true;
                    }
                    // Something went wrong while adding to db > inform user
                    else Snackbar.make(p_view, R.string.games_connect_error, Snackbar.LENGTH_LONG).show();
                }
                // Something went wrong while adding to db > inform user
                else Snackbar.make(p_view, R.string.games_add_error, Snackbar.LENGTH_LONG).show();
            }
            else {
                // Get title out of list, there should only be one
                Title t = lsContained.get(0);

                // Distinguish between wish list and normal title
                if (t.isOnWishList()) Snackbar.make(p_view, R.string.wishlist_title_exists, Snackbar.LENGTH_LONG).show();
                else Snackbar.make(p_view, R.string.wishlist_title_owned, Snackbar.LENGTH_LONG).show();
            }
        }
        return false;
    }
}