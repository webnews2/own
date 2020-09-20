package com.github.webnews2.own.ui.titles;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.github.webnews2.own.R;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTitleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTitleFragment extends DialogFragment {

    // Tag for (logcat) information logging
    private static final String TAG = AddTitleFragment.class.getSimpleName();

    private static final int PICKED_IMAGE = 1;

    // Member vars for the ui elements
    private ImageView ivThumbnail;
    private ImageButton ibChooseThumbnail;
    private TextInputLayout ilGameTitle;
    private TextInputEditText etGameTitle;
    private TextInputLayout ilLocation;
    private TextInputEditText etLocation;
    private TextInputLayout ilPlatforms;
    private AutoCompleteTextView actvPlatforms;
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
            // TODO: Save data to db
            dismiss();
            return true;
        });

        // Set member vars of ui elements
        ivThumbnail = root.findViewById(R.id.ivThumbnail);
        ibChooseThumbnail = root.findViewById(R.id.ibChooseThumbnail);
        ilGameTitle = root.findViewById(R.id.ilGameTitle);
        etGameTitle = root.findViewById(R.id.etGameTitle);
        ilLocation = root.findViewById(R.id.ilLocation);
        etLocation = root.findViewById(R.id.etLocation);
        ilPlatforms = root.findViewById(R.id.ilPlatforms);
        actvPlatforms = root.findViewById(R.id.actvPlatforms);
        cgPlatforms = root.findViewById(R.id.cgPlatforms);

        // Set up choose thumbnail functionality
        ibChooseThumbnail.setOnClickListener(v -> {
            // TODO: Implement image choosing via user's gallery app
            Intent get = new Intent(Intent.ACTION_GET_CONTENT);
            get.setType("image/*");

            Intent pick = new Intent(Intent.ACTION_PICK);
            pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

            Intent choose = Intent.createChooser(get, "Select Image");
            choose.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pick});

            startActivityForResult(choose, PICKED_IMAGE);

            Snackbar.make(root, "ImageButton clicked!", Snackbar.LENGTH_LONG).show();
        });

        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == PICKED_IMAGE) {

                Uri uriImage = data.getData();
                String s = uriImage.getPath();
                ivThumbnail.setImageURI(uriImage); // only for local images
                Log.i(TAG, "{ojo} Image picked!");
            }
        }
    }
}