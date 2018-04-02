package ru.alexandrkutashov.mapviewtestapp.mapview.data.api;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;

/**
 * Фасад для загрузки тайлов карты
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public interface IMapApiMapper {

    /**
     * Загружает необходимый тайл
     * @param tile тайл позиции
     * @return загруженный битмап тайла
     */
    @Nullable
    Bitmap getTile(@NonNull Tile tile);
}
