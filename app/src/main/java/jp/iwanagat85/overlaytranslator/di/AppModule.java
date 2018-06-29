package jp.iwanagat85.overlaytranslator.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import jp.iwanagat85.overlaytranslator.MainActivity;
import jp.iwanagat85.overlaytranslator.MediaProjectionActivity;
import jp.iwanagat85.overlaytranslator.OverlayTranslatorApp;
import jp.iwanagat85.overlaytranslator.OverlayTranslatorService;
import jp.iwanagat85.overlaytranslator.di.scope.ActivityScope;
import jp.iwanagat85.overlaytranslator.di.scope.ServiceScope;
import jp.iwanagat85.overlaytranslator.domain.OverlayManager;
import jp.iwanagat85.overlaytranslator.domain.OverlayManagerImpl;
import jp.iwanagat85.overlaytranslator.domain.ScreenManager;
import jp.iwanagat85.overlaytranslator.domain.ScreenManagerImpl;

@Module(includes = {AndroidSupportInjectionModule.class})
public abstract class AppModule {

    @Binds
    @Singleton
    abstract Application application(OverlayTranslatorApp application);

    @Provides
    @Singleton
    public static Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    public static OverlayManager provideOverlayManager() {
        return new OverlayManagerImpl();
    }

    @Provides
    @Singleton
    public static ScreenManager provideScreenManager(Context context) {
        return new ScreenManagerImpl(context);
    }

    @ActivityScope
    @ContributesAndroidInjector(modules = {MainActivityModule.class})
    abstract MainActivity mainActivityInjector();

    @ActivityScope
    @ContributesAndroidInjector(modules = {MainActivityModule.class})
    abstract MediaProjectionActivity mediaProjectionActivityInjector();

    @ServiceScope
    @ContributesAndroidInjector(modules = {OverlayTranslatorModule.class})
    abstract OverlayTranslatorService overlayTranslatorInjector();

}
