package ru.alexandrkutashov.mapviewtestapp.mapview.domain;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

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
     * @param listener слушатель обработчика ответа
     */
    void getTile(@NonNull Tile tile, @NonNull WeakReference<IOnBitmapLoadedListener> listener);

    /**
     * Устанавливает размер кеша элементов ответов
     * @param cacheSize размер кеша
     */
    void setCacheSize(int cacheSize);

    /**
     * Обработчик уничтожения объекта
     */
    void onDestroy();
}
