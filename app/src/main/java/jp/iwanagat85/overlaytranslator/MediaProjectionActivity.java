package jp.iwanagat85.overlaytranslator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import dagger.android.AndroidInjection;

public class MediaProjectionActivity extends Activity {

    private static final String TAG = MediaProjectionActivity.class.getSimpleName();

    private static final int SCREEN_CAPTURE_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        MediaProjectionManager mpm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (mpm != null) {
            startActivityForResult(mpm.createScreenCaptureIntent(), SCREEN_CAPTURE_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCREEN_CAPTURE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, OverlayTranslatorService.class);
                intent.putExtra(OverlayTranslatorService.EXTRA_RESULT_CODE, resultCode);
                intent.putExtra(OverlayTranslatorService.EXTRA_RESULT_INTENT, data);
                startService(intent);
            }
        }
        finish();
    }

    public static void start(Context context) {
        context.stopService(new Intent(context, OverlayTranslatorService.class));

        Intent intent = new Intent(context, MediaProjectionActivity.class);
        context.startActivity(intent);
    }
}
