package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
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

    private BlockingQueue<Runnable> mTaskQueue = new LinkedBlockingQueue<>();

    /**
     * an instance of DefaultExecutorSupplier
     */
    private static Executor sInstance;

    private ExecutorService mExecutorService = new ThreadPoolExecutor(NUMBER_OF_CORES,
            NUMBER_OF_CORES * 2,
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            mTaskQueue,
            new BackgroundThreadFactory());

    private java.util.concurrent.Executor mMainThreadExecutor = new MainThreadExecutor();

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

    public java.util.concurrent.Executor forMainThreadTasks() {
        return mMainThreadExecutor;
    }

    private static class BackgroundThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setPriority(THREAD_PRIORITY_BACKGROUND);
            return thread;
        }
    }

    private static class MainThreadExecutor implements java.util.concurrent.Executor {

        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            handler.post(runnable);
        }
    }
}
