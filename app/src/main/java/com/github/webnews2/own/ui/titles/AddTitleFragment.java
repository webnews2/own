package com.github.webnews2.own.ui.titles;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.github.webnews2.own.MainActivity;
import com.github.webnews2.own.R;
import com.github.webnews2.own.utilities.DBHelper;
import com.github.webnews2.own.utilities.Platform;
import com.github.webnews2.own.utilities.Title;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTitleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTitleFragment extends DialogFragment {

    // Tag for (logcat) information logging
    private static final String TAG = AddTitleFragment.class.getSimpleName();

    private static final int PICKED_IMAGE = 1;

    // Member vars
    private ImageView ivThumbnail;
    private Uri uriThumbnail;
    private ImageButton ibChooseThumbnail;
    private TextInputEditText etGameTitle;
    private TextInputEditText etLocation;
    private MaterialButton btnChoosePlatforms;
    private ChipGroup cgPlatforms;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddTitlesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTitleFragment newInstance(String param1, String param2) {
        AddTitleFragment fragment = new AddTitleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
            saveData();
            dismiss();
            return true;
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
            // Create action to get image from an external app activity
            Intent get = new Intent(Intent.ACTION_GET_CONTENT);
            get.setType("image/*");

            // This will enable the user to pick the application for choosing the image
            Intent pick = new Intent(Intent.ACTION_PICK);
            pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"); // external to access shareable media

            // TODO: Add comment
            Intent choose = Intent.createChooser(get, "Select Image");
            choose.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pick});

            // Check if there are activities which can handle the request
            if (choose.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(choose, PICKED_IMAGE);
            } else {
                Snackbar.make(root, "No suitable app was found.", Snackbar.LENGTH_LONG).show();
            }
        });

        // TODO: Think of a good but not limiting input validation for game titles
        // For now there won't be a real validation of the entered string because of those sometimes very strange titles
        etGameTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(etGameTitle.getText())) { // CHECK: seems too obvious
                        // Show error message
                        ilGameTitle.setError("Please enter a game title.");
                    } else {
                        // Clear error message
                        ilGameTitle.setError(null);
                    }
                }
            }
        });

        // CHECK: Add input chips combined with multi-autocomplete-textview instead of dialog
        // TODO: Setup selection dialog for platforms
        // Set autocomplete suggestion list for platforms
        DBHelper dbh = DBHelper.getInstance(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            getContext(),
            android.R.layout.simple_dropdown_item_1line,
            MainActivity.lsPlatforms.stream().map(Platform::getName).collect(Collectors.toList())
        );

        btnChoosePlatforms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String[] arrPlatforms = MainActivity.lsPlatforms.stream().map(Platform::getName).toArray(String[]::new);
                String[] arrPlatforms = new String[] {"Playstation 3", "Xbox 360"};
                boolean[] checkedPlatforms = new boolean[arrPlatforms.length];

                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Choose platforms")
                        .setMultiChoiceItems(arrPlatforms, checkedPlatforms, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                checkedPlatforms[which] = isChecked;

                                Chip platform = new Chip(getContext());
                                platform.setCheckable(false);
                                platform.setClickable(false);
                                platform.setCloseIconVisible(false);
                                platform.setText(arrPlatforms[which]);

                                cgPlatforms.addView(platform);
                            }
                        })
                        .show();


                Snackbar.make(root, "Choose button clicked.", Snackbar.LENGTH_LONG).show();
            }
        });


        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If request comes from "select image" action and wasn't cancelled
        if (requestCode == PICKED_IMAGE && resultCode == RESULT_OK) {
            // Store uri of image and set image of image view
            uriThumbnail = data.getData();
            ivThumbnail.setImageURI(uriThumbnail); // only for local images
        }
        else {
            Log.e(TAG, "{ojo} Something went wrong during the image selection!");
            return;
        }
    }


    private boolean saveData() {
        // TODO: Only save when game title is entered and UNIQUE, else inform user about existence

        DBHelper dbh = DBHelper.getInstance(getContext());
        long titleID = dbh.addTitle(new Title(
                -1,
                etGameTitle.getText().toString().trim(),
                uriThumbnail.toString(),
                false,
                TextUtils.isEmpty(etLocation.getText()) ? null : etLocation.getText().toString().trim()
        ));

        // TODO: For each chip in chipgroup > add to db if UNIQUE
        //long platformID = dbh.addPlatform(new Platform(-1, actvPlatforms.getText().toString().trim()));

        // TODO: Associate titles and platforms

        //return titleID != -1 && platformID != -1;
        return true;
    }
}