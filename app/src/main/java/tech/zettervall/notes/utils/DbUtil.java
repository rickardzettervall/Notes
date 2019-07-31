package tech.zettervall.notes.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class DbUtil {

    /**
     * Generic method for retrieving raw db objects,
     * for when you don't want to use an observable.
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
            obj = future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            future.cancel(true);
        }
        shutdownExecutorService(executorService);
        return obj;
    }

    /**
     * Shutdown ExecutorService.
     */
    private static void shutdownExecutorService(ExecutorService service) {
        service.shutdown();
        try {
            if (!service.awaitTermination(300, TimeUnit.MILLISECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
        }
    }
}
