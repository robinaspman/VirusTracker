package com.bawp.virustracker.workers;

import static com.bawp.virustracker.Constants.DATA_OUTPUT;
import static com.bawp.virustracker.Constants.STRING_URL;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DownloadJsonWorker extends Worker {
    public DownloadJsonWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String inputUrl = getInputData().getString(STRING_URL);
        Data outputData = new Data.Builder()
                .putString(DATA_OUTPUT, VirusWorkerUtils.processJson(inputUrl))
                .build();
        return Result.success(outputData);
    }
}
