package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.graphics.Bitmap;

/**
 * Фасад для загрузки тайлов карты
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public interface IMapApiMapper {

    /**
     * Загружает необходимый тайл
     * @param x координата абсцисс
     * @param y координата ординат
     * @return загруженный битмап тайла
     */
    Bitmap getTile(int x, int y);
}
