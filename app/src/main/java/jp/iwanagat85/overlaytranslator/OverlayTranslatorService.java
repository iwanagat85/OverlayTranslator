package jp.iwanagat85.overlaytranslator;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.api.services.vision.v1.model.TextAnnotation;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.iwanagat85.overlaytranslator.domain.CloudVisionApiService;
import jp.iwanagat85.overlaytranslator.domain.OverlayManager;
import jp.iwanagat85.overlaytranslator.domain.ScreenManager;
import jp.iwanagat85.overlaytranslator.domain.api.GoogleAppsScriptService;
import jp.iwanagat85.overlaytranslator.domain.api.TranslateResponse;
import jp.iwanagat85.overlaytranslator.util.BitmapUtils;
import jp.iwanagat85.overlaytranslator.util.ClipboardUtils;

public class OverlayTranslatorService extends Service {

    private static final String TAG = OverlayTranslatorService.class.getSimpleName();

    public static final String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";
    public static final String EXTRA_RESULT_INTENT = "EXTRA_RESULT_INTENT";

    private boolean mIsLongClick = false;

    @Inject
    ScreenManager mScreenManager;
    @Inject
    OverlayManager mOverlayManager;
    @Inject
    CloudVisionApiService mCloudVisionApiService;
    @Inject
    GoogleAppsScriptService mGoogleAppsScriptService;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);

        super.onCreate();
        Log.d(TAG, "Service create.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        super.startForeground(startId, createNotification());

        Log.d(TAG, "onStartCommand");

        // Overlay
        mOverlayManager.init(this);

        // ScreenCapture
        Bundle extras = intent.getExtras();
        int resultCode = extras.getInt(EXTRA_RESULT_CODE);
        Intent data = extras.getParcelable(EXTRA_RESULT_INTENT);

        if (resultCode != Activity.RESULT_OK)
            throw new IllegalArgumentException();

        DisplayMetrics realMetrics = new DisplayMetrics();
        Display display = mOverlayManager.getDisplay();
        display.getRealMetrics(realMetrics);

        mScreenManager.init(data, realMetrics);

        //
        setupOverlay();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mOverlayManager.removeOverlay();
        mScreenManager.destroy();
        super.onDestroy();
    }

    private Notification createNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String id = "overlay_translator_foreground";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager != null && manager.getNotificationChannel(id) == null) {
                String name = "Start OverlayTranslator";
                NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
            }
        }

        return new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();
    }

    private void setupOverlay() {
        mOverlayManager.setOverlayEventListener(new OverlayManager.OverlayEventListener() {
            @Override
            public void onReloadButtonClicked(View v, Rect rect) {
                Bitmap screenBmp = mScreenManager.getScreenBitmap();
                Bitmap cropBmp = BitmapUtils.crop(screenBmp, rect.left, rect.top, rect.width(), rect.height());
                mOverlayManager.startProgress();

                Single.just(cropBmp)
                        //.flatMap(bitmap -> debug())
                        .flatMap(bitmap -> mCloudVisionApiService.getTextAnnotation(bitmap))
                        .flatMap(textAnnotation -> mGoogleAppsScriptService.translate(textAnnotation.getText(), "en", "ja"))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<TranslateResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "onSubscribe");
                            }

                            @Override
                            public void onSuccess(TranslateResponse response) {
                                String text = response.getData().getTranslatedText();
                                Log.d(TAG, "onSuccess");
                                Log.d(TAG, "TextAnnotation: " + text);
                                mOverlayManager.setTranslatedText(text);
                                mOverlayManager.stopProgress();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError", e);
                                mOverlayManager.stopProgress();
                            }
                        });
            }

            @Override
            public void onCaptureToClipButtonClicked(View v, String text) {
                ClipboardUtils.putToClipboard(getApplicationContext(), text);
                Toast.makeText(getApplicationContext(), R.string.send_to_clipboard_message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSettingsButtonClicked(View v) {
            }
        });
        mOverlayManager.showOverlay();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                Log.d(TAG, "Orientation: PORTRAIT");
                mOverlayManager.setOrientationIcon(R.drawable.ic_stay_primary_portrait);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                Log.d(TAG, "Orientation: LANDSCAPE");
                mOverlayManager.setOrientationIcon(R.drawable.ic_stay_primary_landscape);
                break;
        }
    }

    // DEBUG
    private Single<TextAnnotation> debug() {
        return Single.create(emitter -> {
            TextAnnotation textAnnotation = new TextAnnotation();
            textAnnotation.setText("Hello");
            emitter.onSuccess(textAnnotation);
        });
    }

}
