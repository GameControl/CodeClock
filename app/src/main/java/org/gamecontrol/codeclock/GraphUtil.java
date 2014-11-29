package org.gamecontrol.codeclock;

import android.content.Context;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * Created by Tony on 11/28/2014.
 *
 * Graph Utility Object
 */
public class GraphUtil {

    public static GraphView getTagFrequencyGraph(Context c) {
        GraphView tagGraph =  new BarGraphView(c, "Tag Frequency Graph");

        TagManager tagManager = TagManager.getTagManager(c);
        HashMap<String, Integer> tagMap = tagManager.getTagMap();
        Set<String> tagSet = tagMap.keySet();
        final String[] tagNameArray = tagSet.toArray(new String[tagSet.size()]);

        GraphView.GraphViewData[] data = new GraphView.GraphViewData[tagNameArray.length];

        for (int i = 0; i < tagNameArray.length; i++) {
            data[i] = new GraphView.GraphViewData(i, tagMap.get(tagNameArray[i]));
        }

        Random randObj = new Random();
        for (int i = 0; i < data.length; i++) {
            data[i] = new GraphView.GraphViewData(i, Math.floor(randObj.nextDouble()*100));
        }

        tagGraph.addSeries(new GraphViewSeries(data));
        //tagGraph.setHorizontalLabels(new String[] {"java", "c++", "python", "ruby"});
        // set view port, start=2, size=5
        tagGraph.setViewPort(0, 5);
        tagGraph.setScrollable(true);
        // optional - activate scaling / zooming
        //tagGraph.setScalable(true);
        //tagGraph.getGraphViewStyle().setNumVerticalLabels(5);
        tagGraph.getGraphViewStyle().setNumHorizontalLabels(5);
        tagGraph.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.HORIZONTAL);
        tagGraph.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return tagNameArray[(int) value];
                }
                return null; // let graphview generate Y-axis label for us
            }
        });

        return tagGraph;
    }
}
