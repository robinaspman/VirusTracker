package com.bawp.virustracker;

import static com.bawp.virustracker.Constants.DATA_OUTPUT;

import android.annotation.SuppressLint;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class VirusWidget extends AppWidgetProvider {
    private static VirusViewModel virusViewModel;
    private static String infectedNum;
    private static String deathNum;
    private static String recoveredNum;
    private static Observer<List<WorkInfo>> widgetObserver;

    static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        virusViewModel =
                new ViewModelProvider.AndroidViewModelFactory(
                        (Application) context.getApplicationContext()
                ).create(VirusViewModel.class);

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        widgetObserver = new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                }
                WorkInfo workInfo = workInfos.get(0);
                boolean finished = workInfo.getState().isFinished();
                if (!finished) {

                } else {
                    Data outputData = workInfo.getOutputData();
                    String outputDataString = outputData.getString(DATA_OUTPUT);
                    if (!TextUtils.isEmpty(outputDataString)) {
                        virusViewModel.setOutputData(outputDataString);
                    }
                    try {
                        setUI(context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        };

        virusViewModel.getOutputWorkInfo().observeForever(widgetObserver);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.virus_widget);
        views.setTextViewText(R.id.widget_title, widgetText);
        views.setTextViewText(R.id.total_infected, infectedNum);
        views.setTextViewText(R.id.total_deaths, deathNum);
        views.setTextViewText(R.id.recovered, recoveredNum);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @SuppressLint("ResourceType")
    private static void setUI(Context context) throws JSONException {
        String outputData = virusViewModel.getOutputData();
        JSONObject jsonObject = new JSONObject(outputData);
        JSONArray resultArray = jsonObject.getJSONArray("results");
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject res = resultArray.getJSONObject(i);
            infectedNum = String.format(context.getString(R.string.infected),
                    res.getLong("total_cases"));
            deathNum = String.format(context.getString(R.string.deaths),
                    res.getLong("total_deaths"));
            recoveredNum = String.format(context.getString(R.string.recovered),
                    res.getLong("total_cases"));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        virusViewModel.getOutputWorkInfo().removeObserver(widgetObserver);
    }
}