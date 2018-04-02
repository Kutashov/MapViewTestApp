package ru.alexandrkutashov.mapviewtestapp;

import org.junit.Test;

import ru.alexandrkutashov.mapviewtestapp.mapview.DefaultMapDiskManager;

import static junit.framework.Assert.assertEquals;

/**
 * Тест на {@link ru.alexandrkutashov.mapviewtestapp.mapview.DefaultMapDiskManager.PathFormatter}
 *
 * @author Alexandr Kutashov
 *         on 02.04.2018
 */

public class PathFormatterTest {

    private static final String BASE_URL = "someName";
    private static final String EXTENSTION = ".png";

    @Test
    public void formatePath() {

        String expected = "someName-500-1000.png";
        int x = 500;
        int y = 1000;

        String actual = DefaultMapDiskManager.PathFormatter.format(BASE_URL, x, y, EXTENSTION);

        assertEquals(expected, actual);
    }
}
