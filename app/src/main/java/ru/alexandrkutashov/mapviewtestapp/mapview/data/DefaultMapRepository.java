package ru.alexandrkutashov.mapviewtestapp.mapview.data;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutionException;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.api.IMapApiMapper;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.disk.IMapDiskManager;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.memory.IMapMemoryManager;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;
import ru.alexandrkutashov.mapviewtestapp.mapview.ext.Executor;

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
            try {
                result = Executor.getInstance().forIOTasks().submit(() -> mMapDiskManager.getFromDisk(tile)).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (result != null) {
                mMapMemoryManager.saveToMemory(tile, result);
            }
        }
        if (result == null) {
            result = mMapApiMapper.getTile(tile);
            if (result != null) {
                Bitmap finalResult = result;
                Executor.getInstance().forIOTasks().submit(() -> mMapDiskManager.saveToDisk(tile, finalResult));
                mMapMemoryManager.saveToMemory(tile, result);
            }
        }
        return result;
    }

    @Override
    public void setCacheSize(int cacheSize) {
        mMapMemoryManager.setSize(cacheSize);
    }
}
