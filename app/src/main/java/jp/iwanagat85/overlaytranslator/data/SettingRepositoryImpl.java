package jp.iwanagat85.overlaytranslator.data;

import android.content.Context;

import javax.inject.Inject;

import io.reactivex.Single;

public class SettingRepositoryImpl implements SettingRepository {

    private Context mContext;
    private SettingDataSource mDataSource;

    @Inject
    public SettingRepositoryImpl(Context context, SettingDataSource dataSource) {
        mContext = context;
        mDataSource = dataSource;
    }

    @Override
    public Single<String> getSourceLanguage() {
        return mDataSource.getSourceLanguage(mContext);
    }

    @Override
    public Single<String> getTargetLanguage() {
        return mDataSource.getTargetLanguage(mContext);
    }

}
