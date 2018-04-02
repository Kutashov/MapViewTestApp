package ru.alexandrkutashov.mapviewtestapp.mapview.ui;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;

/**
 * Слушатель статуса загрузки картинки
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public interface IOnBitmapLoadedListener {

    /**
     * Уведомляет, что тайл загружен
     * @param tile тайл загружаемой позиции
     * @param bitmap ресурс картинки
     */
    void onBitmapLoaded(@NonNull Tile tile, @NonNull Bitmap bitmap);
}
