package com.github.webnews2.own.ui.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.webnews2.own.R;

public class WishlistFragment extends Fragment {

    public WishlistFragment() {

    }

    public View onCreateView( LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_wishlist, container, false);


        return root;
    }
}