package ru.alexandrkutashov.mapviewtestapp.mapview.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import ru.alexandrkutashov.mapviewtestapp.MapApp;
import ru.alexandrkutashov.mapviewtestapp.mapview.data.model.Tile;
import ru.alexandrkutashov.mapviewtestapp.mapview.domain.IMapInteractor;

import static java.lang.Math.abs;

/**
 * Класс для работы с картами.
 * Для работы необходимо установить центральную позицию с помощью {@link MapView#setCentralTile(int, int)}.
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */

public class MapView extends SurfaceView implements IOnBitmapLoadedListener, SurfaceHolder.Callback {

    //region parcelable flags
    private static final String PARCELABLE_SUPER = "superState";
    private static final String PARCELABLE_CENTRAL_TILE = "centralTile";
    private static final String PARCELABLE_LAST_TOUCH_X = "lastTouchX";
    private static final String PARCELABLE_LAST_TOUCH_Y = "lastTouchY";
    private static final String PARCELABLE_ANCHOR_X = "anchorX";
    private static final String PARCELABLE_ANCHOR_Y = "anchorY";
    //endregion

    /**
     * Отладочный фича-тоггл
     */
    public static final boolean DEBUG = false;

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
    private static final int DEFAULT_CACHED_HIDDEN_TILES = 1;

    /**
     * Множитель для количества кешируемых ячеек. Выбран опытным путем, может быть изменен
     */
    private static final int DEFAULT_CACHED_TILE_FACTOR = 8;

    /**
     * Неизменный установленный центр карты
     */
    private Tile mCentralTile;

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
    private Bitmap mStubBitmap;

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

    @SuppressLint("ClickableViewAccessibility")
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
                if (assertDiffOutWidthBounds(dx)) {
                    reDraw();
                    return false;
                }

                float dy = event.getY() - mLastTouchY;
                if (assertDiffOutHeightBounds(dy)) {
                    reDraw();
                    return false;
                }

                mAnchorX = mAnchorX + dx;
                mAnchorY = mAnchorY + dy;

                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                reDraw();
                break;
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        reDraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onBitmapLoaded(@NonNull Tile tile, @NonNull Bitmap bitmap) {
        drawTile(null, tile, bitmap);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mMapInteractor.setListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mMapInteractor.removeListener();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mAnchorX == 0) {
            mAnchorX = (getWidth() - mTileWidth) / 2;
        }
        if (mAnchorY == 0) {
            mAnchorY = (getHeight() - mTileHeight) / 2;
        }
        setWillNotDraw(false);

        mMapInteractor.setCacheSize(DEFAULT_CACHED_TILE_FACTOR *
                (getWidth() / DEFAULT_TILE_WIDTH) * getHeight() / DEFAULT_TILE_HEIGHT);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARCELABLE_SUPER, super.onSaveInstanceState());
        bundle.putSerializable(PARCELABLE_CENTRAL_TILE, mCentralTile);
        bundle.putFloat(PARCELABLE_ANCHOR_X, mAnchorX);
        bundle.putFloat(PARCELABLE_ANCHOR_Y, mAnchorY);
        bundle.putFloat(PARCELABLE_LAST_TOUCH_X, mLastTouchX);
        bundle.putFloat(PARCELABLE_LAST_TOUCH_Y, mLastTouchY);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCentralTile = (Tile) bundle.getSerializable(PARCELABLE_CENTRAL_TILE);
            mAnchorX = bundle.getFloat(PARCELABLE_ANCHOR_X);
            mAnchorY = bundle.getFloat(PARCELABLE_ANCHOR_Y);
            mLastTouchX = bundle.getFloat(PARCELABLE_LAST_TOUCH_X);
            mLastTouchY = bundle.getFloat(PARCELABLE_LAST_TOUCH_Y);
            state = bundle.getParcelable(PARCELABLE_SUPER);
        }
        super.onRestoreInstanceState(state);
    }

    public void setCentralTile(int x, int y) {
        mCentralTile = new Tile(x, y);

        if (getHolder().getSurface().isValid()) {
            reDraw();
        }
    }

    private Bitmap getStubBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(DEFAULT_TILE_WIDTH, DEFAULT_TILE_HEIGHT, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.GRAY);
        return bitmap;
    }

    private void reDraw(){
        if (mCentralTile == null) {
            return;
        }

        Canvas canvas = getHolder().lockCanvas();
        drawTiles(canvas);
        getHolder().unlockCanvasAndPost(canvas);
    }

    private boolean assertDiffOutWidthBounds(float dx) {
        return abs(mAnchorX + dx) / mTileWidth >= DEFAULT_MAP_WIDTH / 2
                || abs((getWidth() - mAnchorX - dx)) / mTileWidth >= DEFAULT_MAP_WIDTH / 2;
    }

    private boolean assertDiffOutHeightBounds(float dy) {
        return abs(mAnchorY + dy) / mTileHeight >= DEFAULT_MAP_HEIGHT / 2 ||
                abs((getHeight() - mAnchorY - dy)) / mTileHeight >= DEFAULT_MAP_HEIGHT / 2;
    }

    private void init() {
        mTileHeight = DEFAULT_TILE_HEIGHT;
        mTileWidth = DEFAULT_TILE_WIDTH;

        mStubBitmap = getStubBitmap();
        mMapInteractor = MapApp.getInstance().getMapInteractor();
        getHolder().addCallback(this);
    }

    private void drawTiles(@NonNull Canvas canvas) {
        Tile topLeft = getTopLeftVisibleTile();
        Tile bottomRight = getBottomRightVisibleTile();

        Tile tile;
        Bitmap bitmap;

        for (int i = topLeft.x; i <= bottomRight.x; i++) {
            for (int j = topLeft.y; j <= bottomRight.y; j++) {
                tile = new Tile(i, j);
                bitmap = mMapInteractor.getTile(tile);
                if (bitmap == null) {
                    drawTile(canvas, tile, mStubBitmap);
                } else {
                    drawTile(canvas, tile, bitmap);
                }

            }
        }
    }

    private void drawTile(@Nullable Canvas canvas, @NonNull Tile tile, @NonNull Bitmap bitmap) {
        float left = mAnchorX - (mCentralTile.x - tile.x) * mTileWidth;
        float top = mAnchorY - (mCentralTile.y - tile.y) * mTileHeight;

        if (canvas == null) {
            Rect rect = new Rect((int) left, (int) top, (int) left + bitmap.getWidth(),
                    (int) top + bitmap.getHeight());
            Canvas temp = getHolder().lockCanvas(rect);
            if (temp != null) {

                temp.drawBitmap(bitmap, left, top, null);
                if (DEBUG) {
                    drawGrid(temp, tile, bitmap, (int) left, (int) top);
                }

                getHolder().unlockCanvasAndPost(temp);
            }

        } else {
            canvas.drawBitmap(bitmap, left, top, null);
            if (DEBUG) {
                drawGrid(canvas, tile, bitmap, (int) left, (int) top);
            }

        }
    }

    private void drawGrid(@NonNull Canvas canvas, @NonNull Tile tile, @NonNull Bitmap bitmap, int left, int top) {
        Paint paint = new Paint();
        if (tile.equals(mCentralTile)) {
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(20);
        } else {
            paint.setColor(Color.RED);
            paint.setStrokeWidth(10);
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawRect(new Rect(left, top,
                left + bitmap.getWidth(), top + bitmap.getHeight()), paint);
    }

    @NonNull
    private Tile getTopLeftVisibleTile() {
        int xTilesDiff = (int) (mAnchorX / mTileWidth + DEFAULT_CACHED_HIDDEN_TILES);
        if (abs(xTilesDiff) > DEFAULT_MAP_WIDTH / 2) {
            xTilesDiff = DEFAULT_MAP_WIDTH / 2;
        }

        int yTilesDiff = (int) (mAnchorY / mTileHeight + DEFAULT_CACHED_HIDDEN_TILES);
        if (abs(yTilesDiff) > DEFAULT_MAP_HEIGHT / 2) {
            yTilesDiff = DEFAULT_MAP_HEIGHT / 2;
        }

        return new Tile(mCentralTile.x - xTilesDiff,
                mCentralTile.y - yTilesDiff);
    }

    @NonNull
    private Tile getBottomRightVisibleTile() {
        int xTilesDiff = (int) ((getWidth() - mAnchorX) / mTileWidth + DEFAULT_CACHED_HIDDEN_TILES);
        if (abs(xTilesDiff) > DEFAULT_MAP_WIDTH / 2) {
            xTilesDiff = DEFAULT_MAP_WIDTH / 2;
        }

        int yTilesDiff = (int) ((getHeight() - mAnchorY) / mTileHeight + DEFAULT_CACHED_HIDDEN_TILES);
        if (abs(yTilesDiff) > DEFAULT_MAP_HEIGHT / 2) {
            yTilesDiff = DEFAULT_MAP_HEIGHT / 2;
        }

        return new Tile(mCentralTile.x + xTilesDiff,
                mCentralTile.y + yTilesDiff);
    }
}
