package ru.alexandrkutashov.mapviewtestapp.mapview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;
import java.util.Map;

import static java.lang.Math.abs;

/**
 * Класс для работы с картами
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public class MapView extends SurfaceView implements IOnBitmapLoadedListener {

    /**
     * Размер ячеек
     */
    private static final int DEFAULT_TILE_WIDTH = 256;
    private static final int DEFAULT_TILE_HEIGHT = 256;

    /**
     * Количество тайлов на карте
     */
    private static final int DEFAULT_MAP_WIDTH = 100;
    private static final int DEFAULT_MAP_HEIGHT = 100;

    /**
     * Количество кешируемых элементов, находящихся вне границ видимости
     */
    private static final int DEFAULT_CACHED_HIDDEN_TILES = 2;

    /**
     * Множитель для количества кешируемых ячеек. Выбран опытным путем, может быть изменен
     */
    private static final int DEFAULT_CACHED_TILE_FACTOR = 4;

    /**
     * Неизменный установленный центр карты
     */
    private Tile centralTile;

    /**
     * Координаты последнего касания
     */
    private float mLastTouchX;
    private float mLastTouchY;

    /**
     * Координаты действующего центра
     */
    private float mAnchorX = 0;
    private float mAnchorY = 0;

    /**
     * Действующие высота и ширина тайлов
     */
    private int mTileWidth;
    private int mTileHeight;

    private IMapInteractor mMapInteractor;

    private Map<Tile, WeakReference<Bitmap>> mTiles = new ArrayMap<>();

    public MapView(Context context) {
        super(context);
        init();
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mAnchorX = getWidth() / 2;
        mAnchorY = getHeight() / 2;
        setWillNotDraw(false);

        mMapInteractor.setCacheSize(DEFAULT_CACHED_TILE_FACTOR *
                (getWidth() / DEFAULT_TILE_WIDTH) * getHeight() / DEFAULT_TILE_HEIGHT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (centralTile == null) {
            return;
        }

        drawTiles(canvas);
    }

    @Override
    public void onBitmapLoaded(Tile tile, Bitmap bitmap) {
        mTiles.put(tile, new WeakReference<>(bitmap));

        drawTile(null, tile, bitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                float dx = event.getX() - mLastTouchX;
                float dy = event.getY() - mLastTouchY;
                mAnchorX = mAnchorX + dx;
                mAnchorY = mAnchorY + dy;

                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                invalidate();
                break;
        }
        return true;
    }

    public void setCentralTile(int x, int y) {
        centralTile = new Tile(x, y);

        invalidate();
    }

    private void init() {
        mTileHeight = DEFAULT_TILE_HEIGHT;
        mTileWidth = DEFAULT_TILE_WIDTH;

        mMapInteractor = new DefaultMapInteractor(new DefaultMapApiMapper());
    }

    private void drawTiles(Canvas canvas) {
        Tile topLeft = getTopLeftVisibleTile();
        Tile bottomRight = getBottomRightVisibleTile();

        Tile tile;
        Bitmap bitmap;

        for (int i = topLeft.x; i <= bottomRight.x; i++) {
            for (int j = topLeft.y; j <= bottomRight.y; j++) {
                tile = new Tile(i, j);
                bitmap = getCachedBitmap(tile);
                if (bitmap == null) {
                    mMapInteractor.getTile(tile, new WeakReference<>(this));
                } else {
                    drawTile(canvas, tile, bitmap);
                }

            }
        }
    }

    private void drawTile(Canvas canvas, Tile tile, Bitmap bitmap) {
        float left = mAnchorX - (centralTile.x - tile.x) * mTileWidth;
        float top = mAnchorY - (centralTile.y - tile.y) * mTileHeight;

        if (canvas == null) {
            Rect rect = new Rect((int) left, (int) top, (int) left + bitmap.getWidth(),
                    (int) top + bitmap.getHeight());
            Canvas temp = getHolder().lockCanvas(rect);
            temp.drawBitmap(bitmap, left, top, null);
            getHolder().unlockCanvasAndPost(temp);
        } else {
            canvas.drawBitmap(bitmap, left, top, null);
        }
    }

    private Bitmap getCachedBitmap(Tile tile) {
        WeakReference<Bitmap> reference = mTiles.get(tile);
        if (reference == null) {
            return null;
        } else {
            Bitmap result = reference.get();
            if (result == null) {
                mTiles.remove(tile);
            }
            return result;
        }
    }

    private Tile getTopLeftVisibleTile() {
        int xTilesDiff = (int) (mAnchorX / mTileWidth + DEFAULT_CACHED_HIDDEN_TILES);
        if (abs(xTilesDiff) > DEFAULT_MAP_WIDTH / 2) {
            xTilesDiff = DEFAULT_MAP_WIDTH / 2;
        }

        int yTilesDiff = (int) (mAnchorY / mTileHeight + DEFAULT_CACHED_HIDDEN_TILES);
        if (abs(yTilesDiff) > DEFAULT_MAP_HEIGHT / 2) {
            yTilesDiff = DEFAULT_MAP_HEIGHT / 2;
        }

        return new Tile(centralTile.x - xTilesDiff,
                centralTile.y - yTilesDiff);
    }

    private Tile getBottomRightVisibleTile() {
        int xTilesDiff = (int) ((getWidth() - mAnchorX) / mTileWidth + DEFAULT_CACHED_HIDDEN_TILES);
        if (abs(xTilesDiff) > DEFAULT_MAP_WIDTH / 2) {
            xTilesDiff = DEFAULT_MAP_WIDTH / 2;
        }

        int yTilesDiff = (int) ((getHeight() - mAnchorY) / mTileHeight + DEFAULT_CACHED_HIDDEN_TILES);
        if (abs(yTilesDiff) > DEFAULT_MAP_HEIGHT / 2) {
            yTilesDiff = DEFAULT_MAP_HEIGHT / 2;
        }

        return new Tile(centralTile.x + xTilesDiff,
                centralTile.y + yTilesDiff);
    }
}
