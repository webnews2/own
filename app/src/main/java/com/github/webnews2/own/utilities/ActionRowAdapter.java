package com.github.webnews2.own.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;

import com.github.webnews2.own.R;

import java.util.HashMap;

public class ActionRowAdapter extends BaseAdapter {

    private HashMap<Integer, String> mData;
    private Context context;

    public ActionRowAdapter(HashMap<Integer, String> p_mData, Context p_context) {
        mData = p_mData;
        context = p_context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position + 1); // necessary as db ids don't start with zero
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_action_secondary, parent, false);
        }

        AppCompatImageButton ibActionLeft = convertView.findViewById(R.id.ibActionLeft);
        AppCompatEditText etAction = convertView.findViewById(R.id.etAction);
        AppCompatImageButton ibActionRight = convertView.findViewById(R.id.ibActionRight);

        etAction.setText(getItem(position).toString());

        return convertView;
    }
}
