package jp.iwanagat85.overlaytranslator.domain;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

public interface OverlayManager {

    ImageView getOrientationIcon();

    void init(Context context);

    Display getDisplay();

    void showOverlay();

    void removeOverlay();

    void setOverlayEventListener(OverlayEventListener listener);

    void setOrientationIcon(@DrawableRes int id);

    void setTranslatedText(String text);

    void startProgress();

    void stopProgress();

    public static interface OverlayEventListener {
        void onReloadButtonClicked(View v, Rect rect);

        void onCaptureToClipButtonClicked(View v, String text);

        void onSettingsButtonClicked(View v);
    }

}
