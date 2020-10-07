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

import com.bumptech.glide.Glide;
import com.github.webnews2.own.R;
import com.github.webnews2.own.utilities.Title;

import java.util.List;

/**
 * This adapter class is used to display the user's games list. It extends the {@link BaseAdapter} to provide basic
 * functionality. It always checks the status of the "read from external storage" permission.
 *
 * @author Kevin Kleiber (m26675)
 * @version 1.0
 */
public class GamesAdapter extends BaseAdapter {
    /**
     * Holds the views of this adapter class and is mainly used to improve the performance.
     */
    private static class ViewHolder {
        // Views of the adapter
        private ImageView ivThumbnail;
        private TextView tvGameName;

        /**
         * Constructs a ViewHolder object which uses the provided view to find and access the views of this adapter's
         * layout.
         *
         * @param p_view view to use for finding the layout views
         */
        public ViewHolder(View p_view) {
            ivThumbnail = p_view.findViewById(R.id.ivThumbnail);
            tvGameName = p_view.findViewById(R.id.tvGameName);
        }
    }

    // Tag for (logcat) information logging
    private static final String TAG = GamesAdapter.class.getSimpleName();

    // Fields of the adapter
    private List<Title> lsGames;
    private Context context;

    /**
     * Constructs a new GamesAdapter object which uses the ViewHolder pattern to improve the performance. It provides
     * display functionality for entries of the provided games list. The context is used for various operations like
     * checking the storage access permission.
     *
     * @param p_lsGames list which contains the user's games list
     * @param p_context context to use for operations like layout inflation, image loading, etc.
     */
    public GamesAdapter(List<Title> p_lsGames, Context p_context) {
        lsGames = p_lsGames;
        context = p_context;
    }

    /**
     * How many items are in the data set represented by this adapter.
     *
     * @return count of items
     */
    @Override
    public int getCount() {
        return lsGames.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position position of item whose data we want within adapter's data set
     * @return data at the specified position
     */
    @Override
    public Title getItem(int position) {
        return lsGames.get(position);
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
     * checks the storage access permission each time, as it could have changed during the usage of the app. This method
     * makes use of the ViewHolder pattern for adapters to improve the overall performance.
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
            convertView = LayoutInflater.from(context).inflate(R.layout.row_title, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }
        // Use the ViewHolder if this view was previously created
        else {
            vh = (ViewHolder) convertView.getTag();
        }

        // Get title object for this games list entry
        final Title t = getItem(position);

        // Get path of thumbnail and permission status
        String thumbPath = t.getThumbnail();
        boolean permGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        // If thumbnail path is not set > reset displayed image
        if (TextUtils.isEmpty(thumbPath)) vh.ivThumbnail.setImageBitmap(null);
        // If permission is granted and image exists > load image
        else
            if (permGranted) Glide.with(context).load(Uri.parse(thumbPath)).into(vh.ivThumbnail);

        vh.tvGameName.setText(t.getName());

        // Return new or recycled view containing all relevant data
        return convertView;
    }
}
