package org.gamecontrol.codeclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by illwrath on 10/25/14.
 */
public class JobAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> jobNames;
    private ArrayList<Integer> statuses;

    public JobAdapter(Context c, ArrayList<String> names, ArrayList<Integer> statuses){
        mContext = c;
        jobNames = names;
        this.statuses = statuses;
    }

    @Override
    public int getCount() {
        return jobNames.size();
    }

    @Override
    public Object getItem(int position) {
        return jobNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listview;

        if(convertView == null){
            listview = new View(mContext);
        } else {
            listview = (View) convertView;
        }
        listview = inflater.inflate(R.layout.list_item_job, null);
        TextView textView = (TextView) listview.findViewById(R.id.list_item_name);
        textView.setText(jobNames.get(position));
        ImageView imageView = (ImageView) listview.findViewById(R.id.list_item_image);

        int drawableID;
        switch (statuses.get(position)){
            case CCUtils.STATE_RUNNING:
                drawableID = R.drawable.dial_running;
                break;
            case CCUtils.STATE_COMPLETE:
                drawableID = R.drawable.ic_done_white_24dp;
                break;
            default:
            drawableID = R.drawable.dial_stopped;
        }
        imageView.setBackground( mContext.getResources().getDrawable(drawableID));
        return listview;
    }
}
