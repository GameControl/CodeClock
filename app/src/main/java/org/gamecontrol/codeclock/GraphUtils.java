package org.gamecontrol.codeclock;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * Created by Tony on 11/28/2014.
 *
 * Graph Utility Object
 */
public class GraphUtils {
    private final static String TAG = "org.gamecontrol.codeclock.GraphUtils";
    private final static int SUNDAY = 0;
    private final static int MONDAY = 1;
    private final static int TUESDAY = 2;
    private final static int WEDNESDAY = 3;
    private final static int THURSDAY = 4;
    private final static int FRIDAY = 5;
    private final static int SATURDAY = 6;

    public static GraphView getWeeklyFrequencyGraph(Context c, ArrayList<Long> startTimes, ArrayList<Long> runningTimes) {
        Long[] timePerDay = new Long[7];

        for (int i = 0; i < timePerDay.length; i++) {
            timePerDay[i] = (long) 0; //(long) i*10;
        }

        Long[] stArray = new Long[startTimes.size()];
        startTimes.toArray(stArray);

        Long[] rtArray = new Long[runningTimes.size()];
        runningTimes.toArray(rtArray);

        for(int i = 0; i < stArray.length; i++) {
            long time = stArray[i];
            String day = DateUtils.formatDateTime(c, time, DateUtils.FORMAT_SHOW_WEEKDAY);

            Log.d(TAG, "time: " + time + " day: " + day);

            rtArray[i] = (rtArray[i] / (1000 * 60));

            if (day.equals("Sunday")) {
                Log.d(TAG, "Sunday <--- " + rtArray[i]);
                timePerDay[SUNDAY] += rtArray[i];
            }
            else if (day.equals("Monday")) {
                Log.d(TAG, "Monday <--- " + rtArray[i]);
                timePerDay[MONDAY] += rtArray[i];
            }
            else if (day.equals("Tuesday")) {
                Log.d(TAG, "Tuesday <--- " + rtArray[i]);
                timePerDay[TUESDAY] += rtArray[i];
            }
            else if (day.equals("Wednesday")) {
                Log.d(TAG, "Wednesday <--- " + rtArray[i]);
                timePerDay[WEDNESDAY] += rtArray[i];
            }
            else if (day.equals("Thursday")) {
                Log.d(TAG, "Thursday <--- " + rtArray[i]);
                timePerDay[THURSDAY] += rtArray[i];
            }
            else if (day.equals("Friday")) {
                Log.d(TAG, "Friday <--- " + rtArray[i]);
                timePerDay[FRIDAY] += rtArray[i];
            }
            else if (day.equals("Saturday")) {
                Log.d(TAG, "Saturday <--- " + rtArray[i]);
                timePerDay[SATURDAY] += rtArray[i];
            }
        }

        GraphView.GraphViewData[] data = new GraphView.GraphViewData[7];

        for (int i = 0; i < 7; i++) {
            data[i] = new GraphView.GraphViewData(i, timePerDay[i]);
        }

        BarGraphView weeklyGraph =  new BarGraphView(c, "Weekday Frequency Graph");
        weeklyGraph.addSeries(new GraphViewSeries(data));

        weeklyGraph.getGraphViewStyle().setNumHorizontalLabels(7);

        weeklyGraph.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.HORIZONTAL);
        weeklyGraph.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    int intVal = (int) Math.round(value);
                    switch (intVal) {
                        case 0:
                            return "Sun";
                        case 1:
                            return "Mon";
                        case 2:
                            return "Tue";
                        case 3:
                            return "Wed";
                        case 4:
                            return "Thu";
                        case 5:
                            return "Fri";
                        case 6:
                            return "Sat";
                    }
                }
                // it is a y-value
                value = value /60.0;
                return String.format("%.1f hrs", value);
            }
        });

        weeklyGraph.setManualYMinBound(0);
        return weeklyGraph;
    }

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
