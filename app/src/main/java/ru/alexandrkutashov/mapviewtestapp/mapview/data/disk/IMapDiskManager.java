package ru.alexandrkutashov.mapviewtestapp.mapview.data.disk;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;

/**
 * Менеджер по управлению файлами на внутренней памяти
 *
 * @author Alexandr Kutashov
 *         on 02.04.2018
 */

public interface IMapDiskManager {

    /**
     * Достает тайл с диска, если тот существует
     * @param tile тайл для поиска
     * @return найденая картинка тайла
     */
    @Nullable
    Bitmap getFromDisk(@NonNull Tile tile);

    /**
     * Сохраняет тайл на диск
     * @param tile тайл с индексами
     * @param bitmap картинка тайла
     */
    void saveToDisk(@NonNull Tile tile, @NonNull Bitmap bitmap);
}
