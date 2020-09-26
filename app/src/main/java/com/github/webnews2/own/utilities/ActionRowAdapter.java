package com.github.webnews2.own.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;

import com.github.webnews2.own.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

// TODO: Improve performance, https://guides.codepath.com/android/Using-a-BaseAdapter-with-ListView
public class ActionRowAdapter extends BaseAdapter {

    private static class ViewHolder {
        AppCompatImageButton ibActionLeft;
        AppCompatEditText etAction;
        AppCompatImageButton ibActionRight;

        public ViewHolder(View p_view) {


            ibActionLeft = p_view.findViewById(R.id.ibActionLeft);
            etAction = p_view.findViewById(R.id.etAction);
            ibActionRight = p_view.findViewById(R.id.ibActionRight);
            View.OnClickListener myClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Clicked at " + v.getTag(), Snackbar.LENGTH_LONG).show();
                }
            };
            ibActionRight.setOnClickListener(myClickListener);
        }

    }

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
        ViewHolder vh;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_action_secondary, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.ibActionRight.setTag(position);
        vh.etAction.setText(getItem(position).toString());

        return convertView;
    }

    public void updateData(HashMap<Integer, String> p_mDataNew) {
        mData.clear();
        mData.putAll(p_mDataNew);
        notifyDataSetChanged();
    }
}
