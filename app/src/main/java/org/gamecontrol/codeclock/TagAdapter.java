package org.gamecontrol.codeclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by illwrath on 11/26/14.
 */
public class TagAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> tags;

    public TagAdapter(Context c, ArrayList<String> tagList) {
        mContext = c;
        tags = tagList;
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Object getItem(int position) {
        return tags.get(position);
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
        listview = inflater.inflate(R.layout.list_item_tag, null);
        TextView textView = (TextView) listview.findViewById(R.id.tagName);
        textView.setText(tags.get(position));
        return listview;
    }
}
