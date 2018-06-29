package jp.iwanagat85.overlaytranslator;

import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import jp.iwanagat85.overlaytranslator.domain.ScreenManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1001;

    @Inject
    ScreenManager mScreenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStart = findViewById(R.id.button_start);
        buttonStart.setOnClickListener(v -> {
            Log.d(TAG, "START Service");
            checkPermissions();
        });

        Button buttonStop = findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(v -> {
            Log.d(TAG, "STOP Service");
            stopTranslateService();
        });

        Button buttonSettings = findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

    }

    private void checkPermissions() {
        if (overlayPermissionIsGranted()) {
            MediaProjectionActivity.start(this);
        }
    }

    private boolean overlayPermissionIsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                return true;
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case OVERLAY_PERMISSION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    checkPermissions();
                }
                break;
        }
    }

    private void stopTranslateService() {
        Intent intent = new Intent(getApplication(), OverlayTranslatorService.class);
        stopService(intent);
    }

}
