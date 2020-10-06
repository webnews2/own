package com.github.webnews2.own.utilities.adapters;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.bumptech.glide.Glide;
import com.github.webnews2.own.R;
import com.github.webnews2.own.utilities.Title;

import java.util.List;

public class TitlesAdapter extends BaseAdapter {

    private static class ViewHolder {
        private ImageView ivThumbnail;
        private TextView tvGameName;

        public ViewHolder(View p_view) {
            //
            ivThumbnail = p_view.findViewById(R.id.ivThumbnail);
            tvGameName = p_view.findViewById(R.id.tvGameName);
        }
    }

    // Tag for (logcat) information logging
    private static final String TAG = TitlesAdapter.class.getSimpleName();

    private List<Title> lsTitles;
    private Context context;

    public TitlesAdapter(List<Title> p_lsTitles, Context p_context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.row_title, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolder) convertView.getTag();
        }

        final Title t = getItem(position);

        // Get path of thumbnail and permission status
        String thumbPath = t.getThumbnail();
        boolean permGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        // Load image is permission is granted and image exists
        if (permGranted && !TextUtils.isEmpty(thumbPath)) Glide.with(context).load(Uri.parse(thumbPath)).into(vh.ivThumbnail);

        vh.tvGameName.setText(t.getName());

        return convertView;
    }
}
