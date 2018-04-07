package ru.alexandrkutashov.mapviewtestapp.mapview.domain;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;
import ru.alexandrkutashov.mapviewtestapp.mapview.ui.IOnBitmapLoadedListener;

/**
 * Интерактор-помощник для работы с картами
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public interface IMapInteractor {

    /**
     * Получает тайл по необходимой позиции
     * @param tile тайл позиции
     */
    Bitmap getTile(@NonNull Tile tile);

    /**
     * Устанавливает размер кеша элементов ответов
     * @param cacheSize размер кеша
     */
    void setCacheSize(int cacheSize);

    /**
     * Устанавливает слушателя уведомлений о загруженных фрагментах карты
     * @param listener слушатель
     */
    void setListener(@NonNull IOnBitmapLoadedListener listener);

    /**
     * Удаляет установленного слушателя уведомлений о загруженных фрагментах
     */
    void removeListener();
}
