package ru.alexandrkutashov.mapviewtestapp.mapview.data.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Структура, соединяющая {@link ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile}
 * и {@link android.graphics.Bitmap}
 *
 * @author Alexandr Kutashov
 * on 07.04.2018
 */
public class TileBitmap {

    public final Tile tile;
    public final Bitmap bitmap;

    public TileBitmap(@NonNull Tile tile, @NonNull Bitmap bitmap) {
        this.tile = tile;
        this.bitmap = bitmap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TileBitmap)) {
            return false;
        }

        TileBitmap that = (TileBitmap) o;

        return tile.equals(that.tile) && bitmap.equals(that.bitmap);
    }

    @Override
    public int hashCode() {
        int result = tile.hashCode();
        result = 31 * result + bitmap.hashCode();
        return result;
    }
}
