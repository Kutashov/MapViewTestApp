package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Дефолтная реализация {@link IMapRepository}
 *
 * @author Alexandr Kutashov
 *         on 02.04.2018
 */

public class DefaultMapRepository implements IMapRepository {

    private final IMapMemoryManager mMapMemoryManager;
    private final IMapApiMapper mMapApiMapper;
    private final IMapDiskManager mMapDiskManager;

    public DefaultMapRepository(IMapMemoryManager mapMemoryManager, @NonNull IMapApiMapper mapApiMapper, IMapDiskManager mapDiskManager) {
        this.mMapMemoryManager = mapMemoryManager;
        this.mMapApiMapper = mapApiMapper;
        this.mMapDiskManager = mapDiskManager;
    }

    @Override
    @Nullable
    public Bitmap getTile(@NonNull Tile tile) {
        Bitmap result = mMapMemoryManager.getFromMemory(tile);
        if (result == null) {
            result = mMapDiskManager.getFromDisk(tile);
            if (result != null) {
                mMapMemoryManager.saveToMemory(tile, result);
            }
        }
        if (result == null) {
            result = mMapApiMapper.getTile(tile);
            if (result != null) {
                mMapMemoryManager.saveToMemory(tile, result);
                mMapDiskManager.saveToDisk(tile, result);
            }
        }
        return result;
    }

    @Override
    public void setCacheSize(int cacheSize) {
        mMapMemoryManager.setSize(cacheSize);
    }
}
