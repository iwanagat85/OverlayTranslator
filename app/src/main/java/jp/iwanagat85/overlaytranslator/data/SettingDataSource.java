package jp.iwanagat85.overlaytranslator.data;

import android.content.Context;

import io.reactivex.Single;
import jp.iwanagat85.overlaytranslator.Constant;
import jp.iwanagat85.overlaytranslator.R;
import jp.iwanagat85.overlaytranslator.utils.SharedPrefUtils;

public class SettingDataSource {

    public Single<String> getSourceLanguage(Context context) {
        String[] languages = context.getResources().getStringArray(R.array.values_language_list);
        String sourceLanguage = SharedPrefUtils.getString(context, Constant.KEY_TRANS_SOURCE_LNG);
        if (sourceLanguage == null) {
            SharedPrefUtils.setString(context, Constant.KEY_TRANS_SOURCE_LNG, languages[0]);
            sourceLanguage = languages[0];
        }
        return Single.just(sourceLanguage);
    }

    public Single<String> getTargetLanguage(Context context) {
        String[] languages = context.getResources().getStringArray(R.array.values_language_list);
        String targetLanguage = SharedPrefUtils.getString(context, Constant.KEY_TRANS_TARGET_LNG);
        if (targetLanguage == null) {
            SharedPrefUtils.setString(context, Constant.KEY_TRANS_TARGET_LNG, languages[1]);
            targetLanguage = languages[1];
        }
        return Single.just(targetLanguage);
    }
}
