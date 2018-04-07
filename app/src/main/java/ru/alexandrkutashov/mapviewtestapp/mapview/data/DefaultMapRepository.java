package ru.alexandrkutashov.mapviewtestapp.mapview.data;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.api.IMapApiMapper;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.disk.IMapDiskManager;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.memory.IMapMemoryManager;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.TileBitmap;
import ru.alexandrkutashov.mapviewtestapp.mapview.ext.Executor;
import ru.alexandrkutashov.mapviewtestapp.mapview.ext.TileDownloadRunnable;

/**
 * Дефолтная реализация {@link IMapRepository}
 *
 * @author Alexandr Kutashov
 * on 02.04.2018
 */

public class DefaultMapRepository implements IMapRepository {

    private final IMapMemoryManager mMapMemoryManager;
    private final IMapApiMapper mMapApiMapper;
    private final IMapDiskManager mMapDiskManager;
    private final BitmapEmitter mBitmapEmitter;
    private final Map<Tile, Future> mLoading;

    public DefaultMapRepository(IMapMemoryManager mapMemoryManager, @NonNull IMapApiMapper mapApiMapper, IMapDiskManager mapDiskManager) {
        this.mMapMemoryManager = mapMemoryManager;
        this.mMapApiMapper = mapApiMapper;
        this.mMapDiskManager = mapDiskManager;
        mBitmapEmitter = new BitmapEmitter();
        mLoading = new ConcurrentHashMap<>();
    }

    @Override
    @Nullable
    public Bitmap getTile(@NonNull Tile tile) {
        Bitmap result = mMapMemoryManager.getFromMemory(tile);
        if (result == null) {
            getFromDisk(tile);
        }
        return result;
    }

    @Override
    public void setCacheSize(int cacheSize) {
        mMapMemoryManager.setSize(cacheSize);
    }

    @Override
    @NonNull
    public Observable getBitmaps() {
        return mBitmapEmitter;
    }

    private void getFromDisk(Tile tile) {
        if (mLoading.get(tile) == null) {
            mLoading.put(tile, Executor.getInstance().forBackgroundTasks().submit(new TileDownloadRunnable() {
                @Override
                public void run() {
                    if (isSpoiled()) {
                        mLoading.remove(tile);
                        return;
                    }

                    Bitmap result = mMapDiskManager.getFromDisk(tile);
                    if (result == null) {
                        downloadTile(tile);
                    } else {
                        mLoading.remove(tile);

                        mMapMemoryManager.saveToMemory(tile, result);
                        mBitmapEmitter.emit(tile, result);
                    }
                }
            }));
        }
    }

    private void downloadTile(Tile tile) {
        final Bitmap result = mMapApiMapper.getTile(tile);
        if (result == null) {
            mLoading.remove(tile);
            getFromDisk(tile);
            return;
        }

        mLoading.remove(tile);

        mMapMemoryManager.saveToMemory(tile, result);
        mMapDiskManager.saveToDisk(tile, result);
        mBitmapEmitter.emit(tile, result);
    }

    /**
     * Класс-помощник для передачи загруженных фрагментов карты
     */
    private static final class BitmapEmitter extends Observable {

        public void emit(Tile tile, Bitmap bitmap) {
            setChanged();
            notifyObservers(new TileBitmap(tile, bitmap));
        }
    }
}
