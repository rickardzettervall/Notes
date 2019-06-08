package tech.zettervall.notes;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application. This avoids the effects
 * of task starvation (e.g. disk reads don't wait behind webservice requests).
 */
public class AppExecutor {

    private static final int NETWORK_THREAD_COUNT = 3;
    private static AppExecutor INSTANCE;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;
    private final ExecutorService executorService;

    private AppExecutor(Executor diskIO, Executor networkIO, Executor mainThread,
                        ExecutorService executorService) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
        this.executorService = executorService;
    }

    public static AppExecutor getExecutor() {
        if (INSTANCE == null) {
            synchronized (AppExecutor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppExecutor(Executors.newSingleThreadExecutor(), // DiskIO
                            Executors.newFixedThreadPool(NETWORK_THREAD_COUNT), // NetworkIO
                            new MainThreadExecutor(), // Main Thread
                            Executors.newSingleThreadExecutor()); // ExecutorService
                }
            }
        }
        return INSTANCE;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public ExecutorService executorService() {
        return executorService;
    }

    // Executor for Main Thread
    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}