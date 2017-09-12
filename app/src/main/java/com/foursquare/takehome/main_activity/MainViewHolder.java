package com.foursquare.takehome.main_activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.foursquare.takehome.R;

/**
 * Created by paul on 8/24/17.
 */

public class MainViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView text;

    public MainViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        text = (TextView) itemView.findViewById(R.id.text);
    }

}
