package jp.iwanagat85.overlaytranslator.domain.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Single;
import jp.iwanagat85.overlaytranslator.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleAppsScriptServiceImpl implements GoogleAppsScriptService {

    private static final String TAG = GoogleAppsScriptServiceImpl.class.getSimpleName();

    private OkHttpClient mOkHttpClient;

    @Inject
    public GoogleAppsScriptServiceImpl(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
    }

    @Override
    public Single<TranslateResponse> translate(@NonNull String text, @NonNull String source, @NonNull String target) {

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("script.google.com")
                .addPathSegments("macros/s")
                .addPathSegment(BuildConfig.GAS_TRANSLATOR_API_KEY)
                .addPathSegment("exec")
                .addQueryParameter("q", text)
                .addQueryParameter("source", source)
                .addQueryParameter("target", target)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return Single.create(emitter -> {
            Log.d(TAG, "Url: " + request.url().toString());
            Response response = mOkHttpClient.newCall(request).execute();
            if (!response.isSuccessful() || response.body() == null) {
                emitter.onError(new IOException("Failed"));
                return;
            }
            emitter.onSuccess(new Gson().fromJson(response.body().string(), TranslateResponse.class));
        });

    }

}
