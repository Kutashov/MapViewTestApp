package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.graphics.Bitmap;

/**
 * Интерактор-помощник для работы с картами
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public interface IMapInteractor {

    Bitmap getTile(int x, int y);
}
