package ru.alexandrkutashov.mapviewtestapp;

import android.app.Application;

import ru.alexandrkutashov.mapviewtestapp.mapview.DefaultMapApiMapper;
import ru.alexandrkutashov.mapviewtestapp.mapview.DefaultMapDiskManager;
import ru.alexandrkutashov.mapviewtestapp.mapview.DefaultMapInteractor;
import ru.alexandrkutashov.mapviewtestapp.mapview.DefaultMapRepository;
import ru.alexandrkutashov.mapviewtestapp.mapview.IMapInteractor;

/**
 * @author Alexandr Kutashov
 *         on 02.04.2018
 */

public class MapApp extends Application {

    private static MapApp sInstance;

    private IMapInteractor mMapInteractor;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }

    public static MapApp getInstance() {
        return sInstance;
    }

    public IMapInteractor getMapInteractor() {
        if (mMapInteractor == null) {
            mMapInteractor = new DefaultMapInteractor(new DefaultMapRepository(
                    new DefaultMapApiMapper(),
                    new DefaultMapDiskManager(getApplicationContext())));
        }
        return mMapInteractor;
    }
}
