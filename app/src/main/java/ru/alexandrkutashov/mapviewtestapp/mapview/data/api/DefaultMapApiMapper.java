package ru.alexandrkutashov.mapviewtestapp.mapview.data.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.URL;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;

/**
 * Дефолтная реализация загрузчика тайлов
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public class DefaultMapApiMapper implements IMapApiMapper {

    private static final String EXTENSION = ".png";
    private static final String BASE_URL = "http://b.tile.opencyclemap.org/cycle/16/";

    @Override
    @Nullable
    public Bitmap getTile(@NonNull Tile tile) {
        try {
            URL url = new URL(BitmapUrlFormatter.format(BASE_URL, tile.x, tile.y, EXTENSION));
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final class BitmapUrlFormatter {

        private static final String DELIM = "/";

        private BitmapUrlFormatter() {
            throw new UnsupportedOperationException();
        }

        public static String format(@NonNull String baseUrl, int x, int y, @NonNull String extension) {
            return baseUrl + x + DELIM + y + extension;
        }
    }
}
