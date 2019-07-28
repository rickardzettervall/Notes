package tech.zettervall.notes.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class DbUtil {

    /**
     * Generic method for retrieving raw db objects, use when you don't
     * want to use an observable. Waits on data from db before allowing
     * main thread to continue.
     *
     * @param callable Callable for retrieving db object
     * @param <T>      Return type
     * @return T
     */
    public static <T> T rawDB(Callable<T> callable) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<T> future = executorService.submit(callable);
        T obj = null;
        try {
            obj = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Shutdown ExecutorService
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        return obj;
    }
}
