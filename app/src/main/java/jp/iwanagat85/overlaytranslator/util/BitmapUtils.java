package jp.iwanagat85.overlaytranslator.util;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.NonNull;

import java.nio.ByteBuffer;

public class BitmapUtils {

    public static Bitmap convertToBitmap(@NonNull Image image, int width, int height) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
    }

    public static Bitmap crop(@NonNull Bitmap bitmap, int x, int y, int width, int height) {
        return Bitmap.createBitmap(bitmap, x, y, width, height);
    }

}
