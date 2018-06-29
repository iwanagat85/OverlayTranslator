package jp.iwanagat85.overlaytranslator.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import jp.iwanagat85.overlaytranslator.data.SettingDataSource;
import jp.iwanagat85.overlaytranslator.data.SettingRepository;
import jp.iwanagat85.overlaytranslator.data.SettingRepositoryImpl;
import jp.iwanagat85.overlaytranslator.di.scope.ServiceScope;
import jp.iwanagat85.overlaytranslator.domain.CloudVisionApiService;
import jp.iwanagat85.overlaytranslator.domain.CloudVisionApiServiceImpl;
import jp.iwanagat85.overlaytranslator.domain.api.GoogleAppsScriptService;
import jp.iwanagat85.overlaytranslator.domain.api.GoogleAppsScriptServiceImpl;
import okhttp3.OkHttpClient;

@Module
public abstract class OverlayTranslatorModule {

    @ServiceScope
    @Provides
    public static OkHttpClient provideOkHttpClient() {
        return new OkHttpClient().newBuilder()
                .followRedirects(true)
                .build();
    }

    @ServiceScope
    @Provides
    public static CloudVisionApiService provideCloudVisionApiService() {
        return new CloudVisionApiServiceImpl();
    }

    @ServiceScope
    @Provides
    public static GoogleAppsScriptService provideGoogleAppsScriptService(OkHttpClient okHttpClient) {
        return new GoogleAppsScriptServiceImpl(okHttpClient);
    }

    @ServiceScope
    @Provides
    public static SettingRepository provideSettingRepository(Context context) {
        return new SettingRepositoryImpl(context, new SettingDataSource());
    }

}
