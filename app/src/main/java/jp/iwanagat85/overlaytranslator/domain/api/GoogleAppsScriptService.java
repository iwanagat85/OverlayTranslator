package jp.iwanagat85.overlaytranslator.domain.api;

import android.support.annotation.NonNull;

import io.reactivex.Single;

public interface GoogleAppsScriptService {

    Single<TranslateResponse> translate(@NonNull String text, @NonNull String source, @NonNull String target);

}
