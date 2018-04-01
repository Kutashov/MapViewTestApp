package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.URL;

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
    public Bitmap getTile(int x, int y) {
        try {
            URL url = new URL(BitmapUrlFormatter.format(BASE_URL, x, y, EXTENSION));
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class BitmapUrlFormatter {

        private static final String DELIM = "/";

        public static String format(@NonNull String baseUrl, int x, int y, @NonNull String extension) {
            return baseUrl + x + DELIM + y + extension;
        }
    }
}
