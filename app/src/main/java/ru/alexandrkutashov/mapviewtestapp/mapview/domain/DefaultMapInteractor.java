package ru.alexandrkutashov.mapviewtestapp.mapview.domain;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.Observable;
import java.util.Observer;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.IMapRepository;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.TileBitmap;
import ru.alexandrkutashov.mapviewtestapp.mapview.ui.IOnBitmapLoadedListener;

/**
 * Дефолтный интерактор-помощник для вью карты
 *
 * @author Alexandr Kutashov
 * on 01.04.2018
 */

public class DefaultMapInteractor implements IMapInteractor, Observer {

    private final IMapRepository mMapRepository;
    private IOnBitmapLoadedListener mListener;

    public DefaultMapInteractor(@NonNull IMapRepository mapRepository) {
        mMapRepository = mapRepository;
        mMapRepository.getBitmaps().addObserver(this);
    }

    @Override
    public Bitmap getTile(@NonNull Tile tile) {
        return mMapRepository.getTile(tile);
    }

    @Override
    public void setCacheSize(int cacheSize) {
        mMapRepository.setCacheSize(cacheSize);
    }

    @Override
    public void setListener(@NonNull IOnBitmapLoadedListener listener) {
        mListener = listener;
    }

    @Override
    public void removeListener() {
        mListener = null;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (mListener != null) {
            TileBitmap tileBitmap = (TileBitmap) arg;
            mListener.onBitmapLoaded(tileBitmap.tile, tileBitmap.bitmap);
        }
    }
}
