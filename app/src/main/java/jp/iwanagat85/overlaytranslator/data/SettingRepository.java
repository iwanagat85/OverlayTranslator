package jp.iwanagat85.overlaytranslator.data;

import io.reactivex.Single;

public interface SettingRepository {

    Single<String> getSourceLanguage();

    Single<String> getTargetLanguage();

}
