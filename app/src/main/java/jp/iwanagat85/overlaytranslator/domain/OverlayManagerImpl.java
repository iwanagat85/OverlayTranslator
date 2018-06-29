package jp.iwanagat85.overlaytranslator.domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import jp.iwanagat85.overlaytranslator.R;

public class OverlayManagerImpl implements OverlayManager {

    static final String TAG = OverlayManagerImpl.class.getSimpleName();

    private WindowManager mWindowManager;

    private View mOverlayView;

    private View mCaptureArea;

    private View mTextContainer;
    private TextView mTranslatedText;
    private ImageView mOrientationIcon;

    private Button mReloadButton;
    private Button mCaptureToClipButton;
    private Button mMoveButton;
    private Button mSettingsButton;

    private WindowManager.LayoutParams mParams = null;

    private OverlayEventListener mListener;
    private Animation mRotateAnimation;

    @Inject
    public OverlayManagerImpl() {

    }

    @Override
    public ImageView getOrientationIcon() {
        return mOrientationIcon;
    }

    @Override
    public void init(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mOverlayView = LayoutInflater.from(context).inflate(R.layout.view_translator, null);

        mCaptureArea = mOverlayView.findViewById(R.id.capture_area);
        mOrientationIcon = mOverlayView.findViewById(R.id.icon_orientation);
        switch (context.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                mOrientationIcon.setImageResource(R.drawable.ic_stay_primary_landscape);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                mOrientationIcon.setImageResource(R.drawable.ic_stay_primary_portrait);
                break;
            default:
                mOrientationIcon.setImageResource(R.drawable.ic_stay_primary_portrait);
                break;
        }
        mReloadButton = mOverlayView.findViewById(R.id.button_reload);
        mCaptureToClipButton = mOverlayView.findViewById(R.id.button_to_clip);

        mTextContainer = mOverlayView.findViewById(R.id.text_container);
        mTranslatedText = mOverlayView.findViewById(R.id.text_translated);

        mSettingsButton = mOverlayView.findViewById(R.id.button_settings);
        mMoveButton = mOverlayView.findViewById(R.id.button_move);

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        mRotateAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_rotate);
        mRotateAnimation.setRepeatMode(Animation.INFINITE);

        setupEventListener();
    }

    @Override
    public Display getDisplay() {
        return mWindowManager.getDefaultDisplay();
    }

    @Override
    public void showOverlay() {
        mWindowManager.addView(mOverlayView, mParams);
    }

    @Override
    public void removeOverlay() {
        mWindowManager.removeView(mOverlayView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupEventListener() {
        mMoveButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    Point size = new Point();
                    getDisplay().getRealSize(size);
                    Point displaySize = new Point(size);
                    float centerX = event.getRawX() - (displaySize.x / 2);
                    float centerY = event.getRawY() - (displaySize.y / 2);
                    float testX = mOverlayView.getWidth() / 2;
                    float testY = mOverlayView.getHeight() / 2;
                    mParams.x = (int) (centerX - testX);
                    mParams.y = (int) (centerY + testY);
                    mWindowManager.updateViewLayout(mOverlayView, mParams);
                    break;
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        });

        mReloadButton.setOnClickListener(v -> {
            if (mListener != null) {
                mTextContainer.setVisibility(View.GONE);
                mTranslatedText.setText(null);
                Single.timer(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSingleObserver<Long>() {
                            @Override
                            public void onSuccess(Long aLong) {
                                int[] location = new int[2];
                                mCaptureArea.getLocationOnScreen(location);
                                int left = location[0];
                                int top = location[1];
                                int right = left + mCaptureArea.getMeasuredWidth();
                                int bottom = top + mCaptureArea.getMeasuredHeight();
                                Rect rect = new Rect(left, top, right, bottom);
                                mListener.onReloadButtonClicked(v, rect);

                                mTextContainer.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        });
            }
        });

        mCaptureToClipButton.setOnClickListener(v -> {
            if (mListener != null) {
                String text = mTranslatedText.getText().toString();
                mListener.onCaptureToClipButtonClicked(v, text);
            }
        });

        mSettingsButton.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onSettingsButtonClicked(v);
        });
    }

    public void setOverlayEventListener(OverlayEventListener listener) {
        this.mListener = listener;
    }

    @Override
    public void setOrientationIcon(@DrawableRes int id) {
        mOrientationIcon.setImageResource(id);
    }

    @Override
    public void setTranslatedText(String text) {
        mTranslatedText.setText(text);
    }

    @Override
    public void startProgress() {
        mReloadButton.setEnabled(false);
        mReloadButton.startAnimation(mRotateAnimation);
    }

    @Override
    public void stopProgress() {
        mReloadButton.setEnabled(true);
        mReloadButton.clearAnimation();
    }

}
