package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Дефолтная реализация {@link IMapDiskManager}
 *
 * @author Alexandr Kutashov
 *         on 02.04.2018
 */

public class DefaultMapDiskManager implements IMapDiskManager {

    private static final String BASE_NAME = "tile";
    private static final String EXTENSTION = ".jpg";
    private static final String TILE_DIRECTORY_NAME = "TestMapTiles";

    private static final int MAX_FILES_ON_DISK = 2500;
    private static final int FILES_TO_REMOVE = MAX_FILES_ON_DISK / 2;

    private Context mContext;

    private int mFileCount = 0;

    public DefaultMapDiskManager(@NonNull Context applicationContext) {
        mContext = applicationContext;
        Executor.getInstance().forBackgroundTasks().submit(this::tryToDeleteOldFiles);
    }

    @Override
    public boolean containsInCache(@NonNull Tile tile) {
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir(TILE_DIRECTORY_NAME, Context.MODE_PRIVATE);
        String fileName = PathFormatter.format(BASE_NAME, tile.x, tile.y, EXTENSTION);
        File filePath = new File(directory, fileName);
        return filePath.exists();
    }

    @Override
    public void saveToDisk(@NonNull Tile tile, @NonNull Bitmap bitmap) {
        if (mFileCount > MAX_FILES_ON_DISK) {
            tryToDeleteOldFiles();
        }

        saveBitmapToInternalStorage(tile, bitmap);
    }

    @Override
    @Nullable
    public Bitmap getFromDisk(@NonNull Tile tile) {
        try {
            ContextWrapper cw = new ContextWrapper(mContext);
            File directory = cw.getDir(TILE_DIRECTORY_NAME, Context.MODE_PRIVATE);
            File f = new File(directory, PathFormatter.format(BASE_NAME, tile.x, tile.y, EXTENSTION));
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveBitmapToInternalStorage(@NonNull Tile tile, @NonNull Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(mContext.getApplicationContext());
        File directory = cw.getDir(TILE_DIRECTORY_NAME, Context.MODE_PRIVATE);
        String fileName = PathFormatter.format(BASE_NAME, tile.x, tile.y, EXTENSTION);
        File filePath = new File(directory, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            ++mFileCount;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void tryToDeleteOldFiles() {
        ContextWrapper cw = new ContextWrapper(mContext.getApplicationContext());
        File directory = cw.getDir(TILE_DIRECTORY_NAME, Context.MODE_PRIVATE);
        File[] listFiles = directory.listFiles();

        mFileCount = listFiles.length;
        if (mFileCount > MAX_FILES_ON_DISK) {
            List<File> files = Arrays.asList(listFiles);
            Collections.sort(files, (o1, o2) -> {
                if (o1.lastModified() > o2.lastModified()) {
                    return 1;
                } else {
                    return -1;
                }
            });
            int i = files.size() - 1;
            int count = FILES_TO_REMOVE;
            while (count > 0) {
                if (files.get(i).delete()) {
                    --count;
                }
                --i;
            }
        }
    }

    public static class PathFormatter {

        private static final String DELIM = "-";

        public static String format(@NonNull String baseUrl, int x, int y, @NonNull String extension) {
            return baseUrl + DELIM + x + DELIM + y + extension;
        }
    }
}
