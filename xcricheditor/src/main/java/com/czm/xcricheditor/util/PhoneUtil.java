package com.czm.xcricheditor.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by caizhiming on 15/12/18.
 */
public class PhoneUtil {
    public static int TAB_BOTTOM_TYPE = 0;
    public static int TAB_TOP_TYPE = 0;
    private static boolean deviceDataInited = false;
    private static float displayMetricsDensity;
    private static int displayMetricsWidthPixels;
    private static int displayMetricsHeightPixels;
    private static int SCREEN_WIDTH_PX_CACHE = -1;
    private static int SCREEN_HEIGHT_PX_CACHE = -1;
    //android 5.0 以下
    private static final int DEFAULT_MAX_BITMAP_DIMENSION = 8196;

    public static void initDeviceData(Context context) {
        DisplayMetrics displayMetrics = null;
        if (context.getResources() != null && (displayMetrics = context.getResources().getDisplayMetrics()) != null) {
            displayMetricsDensity = displayMetrics.density;
            displayMetricsWidthPixels = displayMetrics.widthPixels;
            displayMetricsHeightPixels = displayMetrics.heightPixels;
        }
        deviceDataInited = true;
    }

    public static int dip2px(Context context, float dipValue) {
        if (!deviceDataInited) {
            initDeviceData(context);
        }

        return (int) (dipValue * displayMetricsDensity + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        if (!deviceDataInited) {
            initDeviceData(context);
        }

        return (int) (pxValue / displayMetricsDensity + 0.5f);
    }

    public static int getScreenWidthPx(Context context) {
        if (SCREEN_WIDTH_PX_CACHE < 0) {
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            SCREEN_WIDTH_PX_CACHE = display.getWidth();
        }

        return SCREEN_WIDTH_PX_CACHE;
    }

    public static int getScreenHeightPx(Context context) {
        if (SCREEN_HEIGHT_PX_CACHE < 0) {
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            SCREEN_HEIGHT_PX_CACHE = display.getHeight();
        }

        return SCREEN_HEIGHT_PX_CACHE;
    }


    public static View getCenterXChild(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; ++i) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterX(recyclerView, child)) {
                    return child;
                }
            }
        }

        return null;
    }

    public static int getCenterXChildPosition(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; ++i) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterX(recyclerView, child)) {
                    return recyclerView.getChildAdapterPosition(child);
                }
            }
        }

        return childCount;
    }

    public static View getCenterYChild(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; ++i) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterY(recyclerView, child)) {
                    return child;
                }
            }
        }

        return null;
    }

    public static int getCenterYChildPosition(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; ++i) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterY(recyclerView, child)) {
                    return recyclerView.getChildAdapterPosition(child);
                }
            }
        }

        return childCount;
    }

    public static boolean isChildInCenterX(RecyclerView recyclerView, View view) {
        int childCount = recyclerView.getChildCount();
        int[] lvLocationOnScreen = new int[2];
        int[] vLocationOnScreen = new int[2];
        recyclerView.getLocationOnScreen(lvLocationOnScreen);
        int middleX = lvLocationOnScreen[0] + recyclerView.getWidth() / 2;
        if (childCount > 0) {
            view.getLocationOnScreen(vLocationOnScreen);
            if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.getWidth() >= middleX) {
                return true;
            }
        }

        return false;
    }

    public static boolean isChildInCenterY(RecyclerView recyclerView, View view) {
        int childCount = recyclerView.getChildCount();
        int[] lvLocationOnScreen = new int[2];
        int[] vLocationOnScreen = new int[2];
        recyclerView.getLocationOnScreen(lvLocationOnScreen);
        int middleY = lvLocationOnScreen[1] + recyclerView.getHeight() / 2;
        if (childCount > 0) {
            view.getLocationOnScreen(vLocationOnScreen);
            if (vLocationOnScreen[1] <= middleY && vLocationOnScreen[1] + view.getHeight() >= middleY) {
                return true;
            }
        }

        return false;
    }

    //隐藏键盘
    public static void hideSoftInput(Context context, View view) {
//        Log.e(TAG, Log.getStackTraceString(new Exception("hideSoftInput")));

        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //显示键盘
    public static void showSoftInput(Context context, View view) {

        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            // 如果输入法打开则关闭，如果没打开则打开
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get the phone max textrure size (height)
     * caizhiming
     */
    public static int getMaxTextureSize(Context context,int maxHeight){
        if(maxHeight > 0 ){
            return maxHeight;
        }
        int[] maxSize = new int[1];
        try {
            ConfigurationInfo configurationInfo = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo();
            int glesVersion = configurationInfo.reqGlEsVersion;
            if(Build.VERSION.SDK_INT >= 21) {
                //configureEGLContext
                EGLDisplay mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
                if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
                    throw new IllegalStateException("No EGL14 display");
                }
                int[] version = new int[2];
                if (!EGL14.eglInitialize(mEGLDisplay, version, /*offset*/ 0, version, /*offset*/ 1)) {
                    throw new IllegalStateException("Cannot initialize EGL14");
                }
                int[] attribList = {
                        EGL14.EGL_RED_SIZE, 8,
                        EGL14.EGL_GREEN_SIZE, 8,
                        EGL14.EGL_BLUE_SIZE, 8,
                        EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                        //EGL_RECORDABLE_ANDROID, 1,
                        EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT | EGL14.EGL_WINDOW_BIT,
                        EGL14.EGL_NONE
                };
                EGLConfig[] configs = new EGLConfig[1];
                int[] numConfigs = new int[1];
                EGL14.eglChooseConfig(mEGLDisplay, attribList, /*offset*/ 0, configs, /*offset*/ 0,
                        configs.length, numConfigs, /*offset*/ 0);
                if (EGL14.eglGetError() != EGL14.EGL_SUCCESS) {
                    throw new IllegalStateException("eglCreateContext RGB888+recordable ES2" + ": EGL error: 0x" + Integer.toHexString(EGL14.eglGetError()));
                }
                int[] attrib_list = {
                        EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                        EGL14.EGL_NONE
                };
                EGLContext mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT,
                        attrib_list, /*offset*/ 0);
                if (EGL14.eglGetError() != EGL14.EGL_SUCCESS) {
                    throw new IllegalStateException("eglCreateContext" + ": EGL error: 0x" + Integer.toHexString(EGL14.eglGetError()));
                }
                if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
                    throw new IllegalStateException("No EGLContext could be made");
                }
                int[] surfaceAttribs = {
                        EGL14.EGL_WIDTH, 64,
                        EGL14.EGL_HEIGHT, 64,
                        EGL14.EGL_NONE
                };
                EGLSurface surface = EGL14.eglCreatePbufferSurface(mEGLDisplay, configs[0], surfaceAttribs, 0);
                EGL14.eglMakeCurrent(mEGLDisplay, surface, surface, mEGLContext);
                //getMaxTextureSize
                if(glesVersion >= 0x20000) {
                    GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0);
                }else if(glesVersion >= 0x10000) {
                    GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
                }
                //releaseEGLContext
                EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(mEGLDisplay);
            }else {
                if(glesVersion >= 0x20000) {
                    GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0);
                }else if(glesVersion >= 0x10000) {
                    GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        maxHeight = maxSize[0] > 0 ? maxSize[0] : DEFAULT_MAX_BITMAP_DIMENSION;
        return maxHeight;
    }
}
