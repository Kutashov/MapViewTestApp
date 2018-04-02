package ru.alexandrkutashov.mapviewtestapp.mapview.ext;

import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Вспомогательный класс для работы с потоками. Используется тред-пул для работы с сетью
 * и екзекьютор для выполнения на основном потоке.
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public class Executor {

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private BlockingQueue<Runnable> mTaskQueue = new LinkedBlockingStack<>();

    private static Executor sInstance;

    private ExecutorService mExecutorService = new ThreadPoolExecutor(NUMBER_OF_CORES,
            NUMBER_OF_CORES * 2,
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            mTaskQueue,
            new BackgroundThreadFactory());

    @NonNull
    public static Executor getInstance() {
        if (sInstance == null) {
            synchronized (Executor.class) {
                sInstance = new Executor();
            }
        }
        return sInstance;
    }

    public ExecutorService forBackgroundTasks() {
        return mExecutorService;
    }

    private static class BackgroundThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setPriority(THREAD_PRIORITY_BACKGROUND);
            return thread;
        }
    }
}
