package com.bawp.virustracker.workers;

import static com.bawp.virustracker.Constants.CLEANING;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CleanupWorker extends Worker {
    public CleanupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = new Data.Builder()
                .putString(CLEANING, "Cleaning...")
                .build();

        return Result.success(data);
    }
}
