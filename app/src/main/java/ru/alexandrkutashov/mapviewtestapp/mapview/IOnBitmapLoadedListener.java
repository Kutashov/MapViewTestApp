package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.graphics.Bitmap;

/**
 * Слушатель статуса загрузки картинки
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

interface IOnBitmapLoadedListener {

    /**
     * Уведомляет, что тайл загружен
     * @param tile тайл загружаемой позиции
     * @param bitmap ресурс картинки
     */
    void onBitmapLoaded(Tile tile, Bitmap bitmap);
}