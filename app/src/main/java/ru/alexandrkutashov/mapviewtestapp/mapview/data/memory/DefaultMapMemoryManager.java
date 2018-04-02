package ru.alexandrkutashov.mapviewtestapp.mapview.data.memory;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;

import java.util.Map;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;

/**
 * Дефолтная реализация {@link IMapMemoryManager}
 *
 * @author Alexandr Kutashov
 *         on 02.04.2018
 */

public class DefaultMapMemoryManager implements IMapMemoryManager {

    private static final int DEFAULT_CACHE_SIZE = 500;

    private final Map<Tile, Bitmap> mTiles = new ArrayMap<>();
    private int mCacheSize = DEFAULT_CACHE_SIZE;

    @Nullable
    @Override
    public Bitmap getFromMemory(@NonNull Tile tile) {
        return mTiles.get(tile);
    }

    @Override
    public void saveToMemory(@NonNull Tile tile, @NonNull Bitmap bitmap) {
        if (mTiles.size() > mCacheSize) {
            mTiles.clear();
        }

        mTiles.put(tile, bitmap);
    }

    @Override
    public void setSize(int cacheSize) {
        mCacheSize = cacheSize;
    }
}
