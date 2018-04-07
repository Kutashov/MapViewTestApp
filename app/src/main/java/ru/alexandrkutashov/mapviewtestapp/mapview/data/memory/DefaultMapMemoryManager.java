package ru.alexandrkutashov.mapviewtestapp.mapview.data.memory;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;

/**
 * Дефолтная реализация {@link IMapMemoryManager}
 *
 * @author Alexandr Kutashov
 *         on 02.04.2018
 */

public class DefaultMapMemoryManager implements IMapMemoryManager {

    private static final int DEFAULT_CACHE_SIZE = 500;

    private final Map<Tile, Bitmap> mBitmaps = new ArrayMap<>();
    private final Queue<Tile> mTiles = new LinkedList<>();
    private int mCacheSize = DEFAULT_CACHE_SIZE;

    @Nullable
    @Override
    public synchronized Bitmap getFromMemory(@NonNull Tile tile) {
        return mBitmaps.get(tile);
    }

    @Override
    public synchronized void saveToMemory(@NonNull Tile tile, @NonNull Bitmap bitmap) {
        if (mBitmaps.size() > mCacheSize) {
            for (int i = mCacheSize / 2; i > 0 ; --i) {
                Tile current = mTiles.poll();
                mBitmaps.remove(current);
            }
        }

        mTiles.add(tile);
        mBitmaps.put(tile, bitmap);
    }

    @Override
    public void setSize(int cacheSize) {
        mCacheSize = cacheSize;
    }
}
