package ru.alexandrkutashov.mapviewtestapp;

import android.app.Application;

import ru.alexandrkutashov.mapviewtestapp.mapview.data.api.DefaultMapApiMapper;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.disk.DefaultMapDiskManager;
import ru.alexandrkutashov.mapviewtestapp.mapview.domain.DefaultMapInteractor;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.memory.DefaultMapMemoryManager;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.DefaultMapRepository;
import ru.alexandrkutashov.mapviewtestapp.mapview.domain.IMapInteractor;

/**
 * @author Alexandr Kutashov
 * on 02.04.2018
 */

public class MapApp extends Application {

    private static MapApp sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }

    public static MapApp getInstance() {
        return sInstance;
    }

    public IMapInteractor getMapInteractor() {
        return new DefaultMapInteractor(new DefaultMapRepository(
                new DefaultMapMemoryManager(),
                new DefaultMapApiMapper(),
                new DefaultMapDiskManager(getApplicationContext())));
    }
}
