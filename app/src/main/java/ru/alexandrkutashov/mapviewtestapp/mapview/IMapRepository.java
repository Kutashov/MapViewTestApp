package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Класс, отвечающий за получение тайлов для карты различными способами:
 * с диска, по сети и др.
 *
 * @author Alexandr Kutashov
 *         on 02.04.2018
 */

public interface IMapRepository {

    /**
     * Загружает необходимый тайл
     * @return загруженный битмап тайла
     */
    @Nullable
    Bitmap getTile(@NonNull Tile tile);

    /**
     * Устанавливает размер кеша в памяти
     * @param cacheSize размер кеша в шутках
     */
    void setCacheSize(int cacheSize);
}
