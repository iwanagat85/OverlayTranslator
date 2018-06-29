package jp.iwanagat85.overlaytranslator.domain;

import android.graphics.Bitmap;

import com.google.api.services.vision.v1.model.TextAnnotation;

import io.reactivex.Single;

public interface CloudVisionApiService {

    Single<TextAnnotation> getTextAnnotation(Bitmap bitmap);

}
