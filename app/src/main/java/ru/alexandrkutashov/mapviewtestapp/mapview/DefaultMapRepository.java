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

    private final IMapApiMapper mMapApiMapper;
    private final IMapDiskManager mMapDiskManager;

    public DefaultMapRepository(@NonNull IMapApiMapper mapApiMapper, IMapDiskManager mapDiskManager) {
        this.mMapApiMapper = mapApiMapper;
        this.mMapDiskManager = mapDiskManager;
    }

    @Override
    @Nullable
    public Bitmap getTile(@NonNull Tile tile) {
        Bitmap result = null;
        if (mMapDiskManager.containsInCache(tile)) {
            System.out.println("found!");
            result = mMapDiskManager.getFromDisk(tile);
        }
        if (result == null) {
            result = mMapApiMapper.getTile(tile);
            if (result != null) {
                mMapDiskManager.saveToDisk(tile, result);
            }
        }
        return result;
    }
}
