package jp.iwanagat85.overlaytranslator.di;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import jp.iwanagat85.overlaytranslator.OverlayTranslatorApp;

@Singleton
@Component(modules = {AppModule.class})
interface AppComponent extends AndroidInjector<OverlayTranslatorApp> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<OverlayTranslatorApp> {
    }

}