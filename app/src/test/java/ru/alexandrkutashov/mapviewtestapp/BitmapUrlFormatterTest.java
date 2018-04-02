package ru.alexandrkutashov.mapviewtestapp;

import org.junit.Test;

import ru.alexandrkutashov.mapviewtestapp.mapview.DefaultMapApiMapper;

import static junit.framework.Assert.assertEquals;

/**
 * Тест на {@link DefaultMapApiMapper.BitmapUrlFormatter}
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public class BitmapUrlFormatterTest {

    private static final String BASE_URL = "http://baseurl.com/";
    private static final String EXTENSTION = ".png";

    @Test
    public void formateUrl() {

        String expected = "http://baseurl.com/500/1000.png";
        int x = 500;
        int y = 1000;

        String actual = DefaultMapApiMapper.BitmapUrlFormatter.format(BASE_URL, x, y, EXTENSTION);

        assertEquals(expected, actual);
    }
}
