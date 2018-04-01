package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.graphics.Bitmap;

/**
 * Дефолтный интерактор-помощник для вью карты
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public class DefaultMapInteractor implements IMapInteractor {

    private final IMapApiMapper mMapApiMapper;

    public DefaultMapInteractor(IMapApiMapper mapApiMapper) {
        mMapApiMapper = mapApiMapper;
    }

    @Override
    public Bitmap getTile(int x, int y) {
        return mMapApiMapper.getTile(x, y);
    }
}
