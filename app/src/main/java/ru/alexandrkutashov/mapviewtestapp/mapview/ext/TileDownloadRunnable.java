package ru.alexandrkutashov.mapviewtestapp.mapview.ext;

/**
 * Помощник для запросов по загрузке тайлов
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public abstract class TileDownloadRunnable implements Runnable {

    /**
     * Время протухания запроса
     */
    private static final long SPOILED_TIME_MS = 10*1000; //10 секунд

    private final long mStartTime;

    protected TileDownloadRunnable() {
        mStartTime = System.currentTimeMillis();
    }

    protected boolean isSpoiled() {
        return System.currentTimeMillis() > mStartTime + SPOILED_TIME_MS;
    }
}
