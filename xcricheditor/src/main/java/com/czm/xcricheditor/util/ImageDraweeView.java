package com.czm.xcricheditor.util;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import java.lang.ref.WeakReference;

/**
 * Created by caizhiming on 15/12/18.
 */
public class ImageDraweeView extends SimpleDraweeView implements ImageDisplayer{

    private Uri uri;
    private boolean webpEnabled = false;
    private int state = -1;
    private WeakReference<OnImageChangeListener> onImageChangeListenerWeakReference;
    private ImageDisplayer.OnImageChangeListener onImageChangeListenerStrongReference;
    private ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFinalImageSet(
                String id,
                @Nullable ImageInfo imageInfo,
                @Nullable Animatable anim) {

            if (imageInfo == null) {
                return;
            }

            state = 0;

            notifyImageLoaded(imageInfo);
        }

        @Override
        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
//            FLog.d("Intermediate image received");
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
//            FLog.e(getClass(), throwable, "Error loading %s", id);

            state = 1;

            if (throwable != null) {

            }


            notifyImageError();
        }
    };


    public ImageDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public ImageDraweeView(Context context) {
        super(context);
    }

    public ImageDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static Uri getTransformedUri(Uri uri, ImageDraweeView imageView) {
        Uri ret = null;

        if (imageView.getWebPEnabled()) {//TODO
        }

        if (ret == null) {
            ret = Uri.parse(uri.toString());
        }

        return ret;
    }

    @Override
    public Uri getUri() {
        return uri;
    }

    public void setUri(final ImageRequest imageRequest) {
        setUri(imageRequest, false);
    }

    public void setUri(final Uri uri) {
        setUri(uri, true);
    }

    public void setUri(final Uri uri, final Postprocessor postprocessor) {
        if (uri == null || TextUtils.isEmpty(uri.toString())) {
            return;
        }
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(postprocessor)
                .build();

        setUri(imageRequest, false);
    }

    public void setUri(final Uri uri, boolean supportGif) {
        if ((uri == null || TextUtils.isEmpty(uri.toString()))) {
            return;
        }

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).build();

        setUri(imageRequest, supportGif);
    }

    public void setUri(ImageRequest imageRequest, boolean supportGif) {
        if (imageRequest == null) {
            return;
        }

        Uri oldUri = imageRequest.getSourceUri();

        Uri uri = getTransformedUri(oldUri, this);

//        Log.e(TAG, "setUriCompare " + uri.toString() + " " + (getUri() != null ? getUri().toString() : "null"));
        if (uri.equals(getUri())) {
            if (state == 0) {
                notifyImageLoaded(null);
            } else if (state == 1) {
                notifyImageError();
            }

            return;
        }

        state = -1;

        String rawUri = uri.toString();


        Context context = getContext();

        if (rawUri.startsWith("android.resource://")) {
            if (uri.getPath().startsWith("/drawable")) {
                String flag = "drawable/";

                String drawableKey = rawUri.substring(rawUri.lastIndexOf(flag) + flag.length());
                int id = context.getResources().getIdentifier(drawableKey, "drawable", context.getPackageName());
                rawUri = "res://" + uri.getHost() + "/" + id;

                uri = Uri.parse(rawUri);
            } else {
                String flag = "://";

                rawUri = rawUri.substring(rawUri.indexOf(flag) + flag.length(), rawUri.length());

                rawUri = "res://" + rawUri;

                uri = Uri.parse(rawUri);
            }
        }

        this.uri = uri;

        ImageRequest newImageRequest = ImageRequestBuilder.newBuilderWithSource(this.uri)
                .setImageDecodeOptions(imageRequest.getImageDecodeOptions())
                .setImageType(imageRequest.getImageType())
                .setLowestPermittedRequestLevel(imageRequest.getLowestPermittedRequestLevel())
                .setRequestPriority(imageRequest.getPriority())
                .setAutoRotateEnabled(imageRequest.getAutoRotateEnabled())
                .setLocalThumbnailPreviewsEnabled(imageRequest.getLocalThumbnailPreviewsEnabled())
                .setProgressiveRenderingEnabled(imageRequest.getProgressiveRenderingEnabled())
                .setPostprocessor(imageRequest.getPostprocessor())
                .setResizeOptions(imageRequest.getResizeOptions())
                .build();

        if (onImageChangeListenerWeakReference != null && onImageChangeListenerWeakReference.get() != null) {
            onImageChangeListenerWeakReference.get().onImageLoading(0);
        }

        try {
            PipelineDraweeControllerBuilder pipelineDraweeControllerBuilder = Fresco.newDraweeControllerBuilder().setOldController(getController())
                    .setControllerListener(controllerListener)
                    .setAutoPlayAnimations(supportGif)
                    .setImageRequest(newImageRequest);

            final DraweeController controller = pipelineDraweeControllerBuilder.build();

            setController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnImageChangeListener(OnImageChangeListener onImageChangeListener) {
        setOnImageChangeListener(false, onImageChangeListener);
    }

    public void setOnImageChangeListener(boolean needStrongReference, OnImageChangeListener onImageChangeListener) {
        if (needStrongReference) {
            this.onImageChangeListenerStrongReference = onImageChangeListener;
        } else {
            this.onImageChangeListenerWeakReference = new WeakReference<>(onImageChangeListener);
        }
    }

    public boolean getWebPEnabled() {
        return webpEnabled;
    }

    public void setWebPEnabled(boolean value) {
        webpEnabled = value;
    }

    protected void notifyImageLoaded(ImageInfo imageInfo) {
        try {
            if (onImageChangeListenerWeakReference != null && onImageChangeListenerWeakReference.get() != null) {
                onImageChangeListenerWeakReference.get().onImageLoaded(imageInfo);
            } else if (onImageChangeListenerStrongReference != null) {
                onImageChangeListenerStrongReference.onImageLoaded(imageInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void notifyImageError() {
        try {
            if (onImageChangeListenerWeakReference != null && onImageChangeListenerWeakReference.get() != null) {
                onImageChangeListenerWeakReference.get().onImageLoadError();
            } else if (onImageChangeListenerStrongReference != null) {
                onImageChangeListenerStrongReference.onImageLoadError();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
