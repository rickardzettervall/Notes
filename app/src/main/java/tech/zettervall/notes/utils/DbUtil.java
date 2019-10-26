package tech.zettervall.notes.utils;

import android.content.Context;
import android.os.Environment;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import tech.zettervall.notes.data.AppDb;

public abstract class DbUtil {

    public static final String DB_BACKUP_TRAIL_PATH = "/backup/simple_notes/db/";

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

    /**
     * Copy database files to input destination path.
     *
     * @param destination Directory path to copy database files to
     * @return True when copy of main db file was successful
     */
    public static boolean backupDb(Context context, String destination) {
        return FileUtil.copyDirectoryContent(getDbDirPath(context), destination);
    }

    /**
     * Restore database files from input source path.
     *
     * @param source Source path to copy database files from
     * @return True when copy of main db file was successful
     */
    public static boolean restoreDb(Context context, String source) {
        return FileUtil.copyDirectoryContent(source, getDbDirPath(context));
    }

    /**
     * Get database directory path.
     */
    private static String getDbDirPath(Context context) {
        StringBuilder dbDirPath = new StringBuilder(context.getDatabasePath(AppDb.DB_NAME).getPath());
        dbDirPath.delete(dbDirPath.lastIndexOf(AppDb.DB_NAME), dbDirPath.length());
        return dbDirPath.toString();
    }

    /**
     * Get default database backup directory path.
     */
    public static String getDefaultBackupDirPath() {
        return Environment.getExternalStorageDirectory().getPath() + DB_BACKUP_TRAIL_PATH;
    }
}
