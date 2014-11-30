package org.gamecontrol.codeclock;

import android.content.Context;
import android.text.format.Time;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * Created by Tony on 11/28/2014.
 *
 * Graph Utility Object
 */
public class GraphUtils {

    public static GraphView getTagFrequencyGraph(Context c, Boolean randomVals) {
        LineGraphView tagGraph =  new LineGraphView(c, "Tag Frequency Graph");
        tagGraph.setDrawDataPoints(true);

        TagManager tagManager = TagManager.getTagManager(c);
        HashMap<String, Integer> tagMap = tagManager.getTagMap();
        Set<String> tagSet = tagMap.keySet();
        final String[] tagNameArray = tagSet.toArray(new String[tagSet.size()]);

        GraphView.GraphViewData[] data = new GraphView.GraphViewData[tagNameArray.length];

        for (int i = 0; i < tagNameArray.length; i++) {
            data[i] = new GraphView.GraphViewData(i, tagMap.get(tagNameArray[i]));
        }

        if (randomVals) {
            Random randObj = new Random();
            for (int i = 0; i < data.length; i++) {
                int randNum = (int) Math.floor(randObj.nextDouble() * 100);
                data[i] = new GraphView.GraphViewData(i, randNum);
            }
        }

        tagGraph.addSeries(new GraphViewSeries(data));


        tagGraph.setViewPort(0, 4);
        tagGraph.getGraphViewStyle().setNumHorizontalLabels(5);

        tagGraph.setManualYAxisBounds(100, 0);
        tagGraph.setManualYAxis(true);

        tagGraph.setScrollable(true);

        tagGraph.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.HORIZONTAL);
        tagGraph.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return tagNameArray[(int) Math.round(value)];
                }
                return null; // let graphview generate Y-axis label for us
            }
        });

        return tagGraph;
    }
}
