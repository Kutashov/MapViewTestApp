package ru.alexandrkutashov.mapviewtestapp.mapview.domain;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.Future;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.IMapRepository;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;
import ru.alexandrkutashov.mapviewtestapp.mapview.ext.Executor;
import ru.alexandrkutashov.mapviewtestapp.mapview.ext.TileDownloadRunnable;
import ru.alexandrkutashov.mapviewtestapp.mapview.ui.IOnBitmapLoadedListener;

/**
 * Дефолтный интерактор-помощник для вью карты
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public class DefaultMapInteractor implements IMapInteractor {

    private final IMapRepository mMapRepository;
    private final Map<Tile, Future> mLoading = new ArrayMap<>();

    public DefaultMapInteractor(@NonNull IMapRepository mapRepository) {
        mMapRepository = mapRepository;
    }

    @Override
    public void getTile(@NonNull Tile tile, @NonNull final WeakReference<IOnBitmapLoadedListener> listener) {

        if (mLoading.get(tile) == null) {
            mLoading.put(tile, Executor.getInstance().forBackgroundTasks().submit(new TileDownloadRunnable() {
                @Override
                public void run() {
                    if (isSpoiled()) {
                        synchronized (DefaultMapInteractor.this) {
                            mLoading.remove(tile);
                        }
                        return;
                    }
                    final Bitmap bitmap = mMapRepository.getTile(tile);

                    synchronized (DefaultMapInteractor.this) {
                        if (bitmap == null) {
                            mLoading.remove(tile);
                            getTile(tile, listener);
                            return;
                        }

                        mLoading.remove(tile);

                        if (listener.get() != null) {
                            listener.get().onBitmapLoaded(tile, bitmap);
                        }
                    }
                }
            }));
        }
    }

    @Override
    public void setCacheSize(int cacheSize) {
        mMapRepository.setCacheSize(cacheSize);
    }

    @Override
    public void onDestroy() {
        //На данный момент сценарий использования карт неизвестен,
        //тем не менее отменить запросы можно.
        for (Future future : mLoading.values()) {
            future.cancel(true);
        }
    }
}
