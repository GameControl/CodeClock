package org.gamecontrol.codeclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by illwrath on 10/25/14.
 */
public class ProjectAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<String> projectNames;

    public ProjectAdapter(Context c, ArrayList<String> names){
        mContext = c;
        projectNames = names;
    }

    @Override
    public int getCount() {
        return projectNames.size();
    }

    @Override
    public Object getItem(int position) {
        return projectNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;

        if(convertView == null){
            gridView = new View(mContext);
            gridView = inflater.inflate(R.layout.list_item_project, null);
            TextView textView = (TextView) gridView.findViewById(R.id.grid_item_name);
            textView.setText(projectNames.get(position));
        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }
}
