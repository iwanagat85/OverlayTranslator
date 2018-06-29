package jp.iwanagat85.overlaytranslator.domain;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.nio.ByteBuffer;

import static android.app.Activity.RESULT_OK;

public class ScreenManagerImpl implements ScreenManager {

    private static final String TAG = ScreenManagerImpl.class.getSimpleName();

    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;

    private int mScreenDensity, mDisplayWidth, mDisplayHeight;
    private ImageReader mImageReader;
    private VirtualDisplay mVirtualDisplay;

    public ScreenManagerImpl(Context context) {
        mMediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public void init(Intent data, DisplayMetrics realMetrics) {
        Log.d(TAG, "init");

        setupMediaProjection(RESULT_OK, data);
        setupVirtualDisplay(realMetrics);
    }

    private void setupMediaProjection(int code, Intent intent) {
        Log.d(TAG, "setupMediaProjection");

        mMediaProjection = mMediaProjectionManager.getMediaProjection(code, intent);
    }

    private void setupVirtualDisplay(DisplayMetrics realMetrics) {
        Log.d(TAG, "setupVirtualDisplay");

        mScreenDensity = realMetrics.densityDpi;
        mDisplayWidth = realMetrics.widthPixels;
        mDisplayHeight = realMetrics.heightPixels;
        mImageReader = ImageReader.newInstance(
                mDisplayWidth, mDisplayHeight,
                PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                mDisplayWidth, mDisplayHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    public Bitmap getScreenBitmap() {
        Log.d(TAG, "getScreenBitmap");

        try (Image image = mImageReader.acquireLatestImage()) {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();

            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * mDisplayWidth;

            int width = mDisplayWidth + rowPadding / pixelStride;
            int height = mDisplayHeight;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);

            return bitmap;
        }
    }

    public void destroy() {
        if (mVirtualDisplay != null)
            mVirtualDisplay.release();
        if (mMediaProjection != null)
            mMediaProjection.stop();
    }
}
