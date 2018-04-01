package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.graphics.Bitmap;
import android.util.ArrayMap;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private final Set<Tile> mLoading = new HashSet<>();

    private int mCacheSize = DEFAULT_CACHE_SIZE;

    public DefaultMapInteractor(IMapApiMapper mapApiMapper) {
        mMapApiMapper = mapApiMapper;
    }

    @Override
    public void getTile(Tile tile, final WeakReference<IOnBitmapLoadedListener> listener) {
        Bitmap result = mTiles.get(tile);
        if (result != null && listener.get() != null) {
            listener.get().onBitmapLoaded(tile, result);
            return;
        }

        if (!mLoading.contains(tile)) {
            mLoading.add(tile);
            Executor.getInstance().forBackgroundTasks().submit(() -> {
                final Bitmap bitmap = mMapApiMapper.getTile(tile.x, tile.y);

                synchronized (DefaultMapInteractor.this) {
                    if (mTiles.size() > mCacheSize) {
                        mTiles.clear();
                    }

                    mTiles.put(tile, bitmap);
                    mLoading.remove(tile);
                }

                Executor.getInstance().forMainThreadTasks().execute(() -> {
                    if (listener.get() != null) {
                        listener.get().onBitmapLoaded(tile, bitmap);
                    }
                });

            });
        }

    }

    @Override
    public void setCacheSize(int cacheSize) {
        mCacheSize = cacheSize;
    }
}
