package jp.iwanagat85.overlaytranslator.domain;

import android.graphics.Bitmap;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.TextAnnotation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import jp.iwanagat85.overlaytranslator.BuildConfig;

public class CloudVisionApiServiceImpl implements CloudVisionApiService {

    static final String TAG = CloudVisionApiServiceImpl.class.getSimpleName();

    public Single<TextAnnotation> getTextAnnotation(Bitmap bitmap) {

        Vision.Builder visionBuilder = new Vision.Builder(new NetHttpTransport(), new AndroidJsonFactory(), null);
        visionBuilder.setVisionRequestInitializer(new VisionRequestInitializer(BuildConfig.CLOUD_VISION_API_KEY));
        Vision vision = visionBuilder.build();

        Feature desiredFeature = new Feature();
        desiredFeature.setType("TEXT_DETECTION");

        AnnotateImageRequest request = new AnnotateImageRequest();
        request.setImage(getEncodedImage(bitmap));
        request.setFeatures(Collections.singletonList(desiredFeature));

        BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
        batchRequest.setRequests(Collections.singletonList(request));

        return Single.create(emitter -> {
            try {
                BatchAnnotateImagesResponse batchResponse = vision
                        .images()
                        .annotate(batchRequest)
                        .execute();

                TextAnnotation textAnnotation = batchResponse.getResponses()
                        .get(0)
                        .getFullTextAnnotation();

                emitter.onSuccess(textAnnotation);
            } catch (IOException e) {
                emitter.onError(e);
            }
        });
    }

    private com.google.api.services.vision.v1.model.Image getEncodedImage(Bitmap bitmap) {
        com.google.api.services.vision.v1.model.Image base64EncodedImage = new com.google.api.services.vision.v1.model.Image();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }
}
