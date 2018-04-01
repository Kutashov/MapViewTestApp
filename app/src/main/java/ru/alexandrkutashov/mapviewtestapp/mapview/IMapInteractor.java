package ru.alexandrkutashov.mapviewtestapp.mapview;

import java.lang.ref.WeakReference;

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
     * @param listener слушатель обработчика ответа
     */
    void getTile(Tile tile, WeakReference<IOnBitmapLoadedListener> listener);

    /**
     * Устанавливает размер кеша элементов ответов
     * @param cacheSize размер кеша
     */
    void setCacheSize(int cacheSize);
}
