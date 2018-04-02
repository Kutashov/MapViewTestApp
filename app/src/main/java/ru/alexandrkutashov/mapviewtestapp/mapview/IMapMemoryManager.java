package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Менеджер по управлению кешем в оперативной памяти
 *
 * @author Alexandr Kutashov
 *         on 02.04.2018
 */

public interface IMapMemoryManager {

    /**
     * Достает тайл из кеша памяти
     * @param tile тайл для поиска
     * @return найденая картинка, если существует
     */
    @Nullable
    Bitmap getFromMemory(@NonNull Tile tile);

    /**
     * Сохраняет тайл в память
     * @param tile тайл с индексами
     * @param bitmap картинка тайла
     */
    void saveToMemory(@NonNull Tile tile, @NonNull Bitmap bitmap);

    /**
     * Устанавливает количество хранимых элементов
     * @param cacheSize размер кеша в штуках
     */
    void setSize(int cacheSize);
}
