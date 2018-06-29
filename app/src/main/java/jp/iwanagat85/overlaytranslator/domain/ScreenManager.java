package jp.iwanagat85.overlaytranslator.domain;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

public interface ScreenManager {

    void init(Intent data, DisplayMetrics realMetrics) ;

    Bitmap getScreenBitmap() ;

    void destroy();

}
