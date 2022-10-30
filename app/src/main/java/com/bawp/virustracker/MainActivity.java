package com.bawp.virustracker;

import static com.bawp.virustracker.Constants.DATA_OUTPUT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;

import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private VirusViewModel virusViewModel;
    private ProgressBar progressBar;
    private TextView infected, deaths, recovered, countries, newCases, newDeaths, loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infected = findViewById(R.id.infected);
        deaths = findViewById(R.id.deaths);
        recovered = findViewById(R.id.recovered);
        countries = findViewById(R.id.countries);
        newCases = findViewById(R.id.new_cases);
        newDeaths = findViewById(R.id.new_deaths);
        progressBar = findViewById(R.id.progressBar);
        loading = findViewById(R.id.loading);

        virusViewModel = new ViewModelProvider.AndroidViewModelFactory((Application) getApplicationContext())
                .create(VirusViewModel.class);
    }

    private void getVirusStatus() {
        virusViewModel.getOutputWorkInfo().observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                }
                WorkInfo workInfo = workInfos.get(0);
                boolean finished = workInfo.getState().isFinished();
                if (!finished) {
                    showWorkInProgress();
                } else {
                    Data outputData = workInfo.getOutputData();
                    String outputDataString = outputData.getString(DATA_OUTPUT);
                    if (!TextUtils.isEmpty(outputDataString)) {
                        virusViewModel.setOutputData(outputDataString);
                    }
                    try {
                        populateUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        virusViewModel.downloadJson();
    }

    private void showWorkInProgress() {
        progressBar.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
    }

    private void populateUI() throws JSONException {
        String outputData = virusViewModel.getOutputData();

        JSONObject jsonObject = new JSONObject(outputData);
        Log.i(TAG, "populateUI: " + jsonObject.getString("result"));
        JSONArray resultArray = jsonObject.getJSONArray("result");
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject res = resultArray.getJSONObject(i);
            deaths.setText(String.format(getString(R.string.deaths), res.getLong("total_deaths")));
            deaths.setText(String.format(getString(R.string.recovered), res.getLong("recovered")));
            deaths.setText(String.format(getString(R.string.new_cases), res.getLong("new_cases")));
            deaths.setText(String.format(getString(R.string.new_deaths), res.getLong("new_deaths")));
            deaths.setText(String.format(getString(R.string.countries), res.getLong("countries")));
        }


        processingDone();
    }

    private void processingDone() {
        progressBar.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.virus_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                getVirusStatus();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}