package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Менеджер по управлению файлами на внутренней памяти
 *
 * @author Alexandr Kutashov
 *         on 02.04.2018
 */

public interface IMapDiskManager {

    boolean containsInCache(@NonNull Tile tile);

    @Nullable Bitmap getFromDisk(@NonNull Tile tile);

    void saveToDisk(@NonNull Tile tile, @NonNull Bitmap bitmap);
}
