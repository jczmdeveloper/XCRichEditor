package com.czm.xcricheditor.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.imagepipeline.image.ImageInfo;

/**
 * Created by caizhiming on 2016/6/20.
 * <p>
 * 尽量不用
 */
public class RichTextDraweeView extends ImageDraweeView {
    private static final int ZOOM_WIDTH_360 = 360;
    private static final int ZOOM_WIDTH_300 = 300;
    public RichTextDraweeView(Context context) {
        super(context);
    }

    public RichTextDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichTextDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RichTextDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }
    private int mWidth = -1;
    public void setWidth(int w){
        mWidth = w;
    }

    @Override
    protected void notifyImageLoaded(ImageInfo imageInfo) {
        super.notifyImageLoaded(imageInfo);
        try {
            int h = imageInfo.getHeight();
            int w = imageInfo.getWidth();
            int vH;
//            int vW = getWidth();
            int vW = mWidth;
            if (vW > w) {
                vH = (w / vW) * h;
            } else {
                vW = w;
                vH = h;
            }
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = vW;
            params.height = vH;
            requestLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void notifyImageError() {
        super.notifyImageError();

    }
}
