package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Дефолтный интерактор-помощник для вью карты
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public class DefaultMapInteractor implements IMapInteractor {

    private static final int DEFAULT_CACHE_SIZE = 500;

    private final Map<Tile, Bitmap> mTiles = new ArrayMap<>();
    private final IMapApiMapper mMapApiMapper;
    private final Map<Tile, Future> mLoading = new ArrayMap<>();

    private int mCacheSize = DEFAULT_CACHE_SIZE;

    public DefaultMapInteractor(@NonNull IMapApiMapper mapApiMapper) {
        mMapApiMapper = mapApiMapper;
    }

    @Override
    public void getTile(@NonNull Tile tile, @NonNull final WeakReference<IOnBitmapLoadedListener> listener) {
        Bitmap result = mTiles.get(tile);
        if (result != null && listener.get() != null) {
            listener.get().onBitmapLoaded(tile, result);
            return;
        }

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
                    final Bitmap bitmap = mMapApiMapper.getTile(tile.x, tile.y);

                    synchronized (DefaultMapInteractor.this) {
                        if (bitmap == null) {
                            mLoading.remove(tile);
                            getTile(tile, listener);
                            return;
                        }

                        if (mTiles.size() > mCacheSize) {
                            mTiles.clear();
                        }

                        mTiles.put(tile, bitmap);
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
        mCacheSize = cacheSize;
    }

    @Override
    public void onDestroy() {
        for (Future future : mLoading.values()) {
            future.cancel(true);
        }
    }
}
