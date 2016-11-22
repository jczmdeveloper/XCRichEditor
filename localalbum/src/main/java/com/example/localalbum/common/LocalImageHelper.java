package com.example.localalbum.common;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.localalbum.AppContext;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caizhiming on 15/6/11.
 */
public class LocalImageHelper {
    private static LocalImageHelper instance;
    private final Context context;
    final List<LocalFile> checkedItems = new ArrayList<>();

    public int getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    //当前选中得图片个数
    private int currentSize;
    public String getCameraImgPath() {
        return CameraImgPath;
    }

    public String setCameraImgPath() {
        String foloder= AppContext.getInstance().getCachePath()
                + "/PostPicture/";
        File savedir = new File(foloder);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        // 照片命名
        String picName =  timeStamp + ".jpg";
        //  裁剪头像的绝对路径
        CameraImgPath = foloder + picName;
        return  CameraImgPath;
    }

    //拍照时指定保存图片的路径
    private String CameraImgPath;
    //大图遍历字段
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION
    };
    //小图遍历字段
    private static final String[] THUMBNAIL_STORE_IMAGE = {
            MediaStore.Images.Thumbnails._ID,
            MediaStore.Images.Thumbnails.DATA
    };

    final List<LocalFile> paths = new ArrayList<>();

    final Map<String, List<LocalFile>> folders = new HashMap<>();

    private LocalImageHelper(Context context) {
        this.context = context;
    }

    public Map<String, List<LocalFile>> getFolderMap() {
        return folders;
    }

    public static LocalImageHelper getInstance() {
        return instance;
    }

    public static void init(Context context) {
        instance = new LocalImageHelper(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                instance.initImage();
            }
        }).start();
    }

    public boolean isInited() {
        return paths.size() > 0;
    }

    public List<LocalFile> getCheckedItems() {
        return checkedItems;
    }

    private boolean resultOk;

    public boolean isResultOk() {
        return resultOk;
    }

    public void setResultOk(boolean ok) {
        resultOk = ok;
    }

    private boolean isRunning = false;

    public synchronized void initImage() {
        if (isRunning)
            return;
        isRunning=true;
        if (isInited())
            return;
        //获取大图的游标
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  // 大图URI
                STORE_IMAGES,   // 字段
                null,         // No where clause
                null,         // No where clause
                MediaStore.Images.Media.DATE_TAKEN + " DESC"); //根据时间升序
        if (cursor == null)
            return;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);//大图ID
            String path = cursor.getString(1);//大图路径
            File file = new File(path);
            //判断大图是否存在
            if (file.exists()) {
                //小图URI
                String thumbUri = getThumbnail(id, path);
                //获取大图URI
                String uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                        appendPath(Integer.toString(id)).build().toString();
                if(StringUtils.isEmpty(uri))
                    continue;
                if (StringUtils.isEmpty(thumbUri))
                    thumbUri = uri;
                //获取目录名
                String folder = file.getParentFile().getName();

                LocalFile localFile = new LocalFile();
                localFile.setOriginalUri(uri);
                localFile.setThumbnailUri(thumbUri);
                int degree = cursor.getInt(2);
                if (degree != 0) {
                    degree = degree + 180;
                }
                localFile.setOrientation(360-degree);

                paths.add(localFile);
                //判断文件夹是否已经存在
                if (folders.containsKey(folder)) {
                    folders.get(folder).add(localFile);
                } else {
                    List<LocalFile> files = new ArrayList<>();
                    files.add(localFile);
                    folders.put(folder, files);
                }
            }
        }
        folders.put("所有图片", paths);
        cursor.close();
        isRunning=false;
    }

    private String getThumbnail(int id, String path) {
        //获取大图的缩略图
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                THUMBNAIL_STORE_IMAGE,
                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
                new String[]{id + ""},
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int thumId = cursor.getInt(0);
            String uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon().
                    appendPath(Integer.toString(thumId)).build().toString();
            cursor.close();
            return uri;
        }
        cursor.close();
        return null;
    }

    public List<LocalFile> getFolder(String folder) {
        return folders.get(folder);
    }

    public void clear(){
        checkedItems .clear();
        currentSize=(0);
        String foloder= AppContext.getInstance().getCachePath()
                + "/PostPicture/";
        File savedir = new File(foloder);
        if (savedir.exists()) {
            deleteFile(savedir);
        }
    }
    public void deleteFile(File file) {

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
        } else {
        }
    }
    public static class LocalFile {
        private String originalUri;//原图URI
        private String thumbnailUri;//缩略图URI
        private int orientation;//图片旋转角度

        public String getThumbnailUri() {
            return thumbnailUri;
        }

        public void setThumbnailUri(String thumbnailUri) {
            this.thumbnailUri = thumbnailUri;
        }

        public String getOriginalUri() {
            return originalUri;
        }

        public void setOriginalUri(String originalUri) {
            this.originalUri = originalUri;
        }


        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int exifOrientation) {
            orientation =  exifOrientation;
        }

    }
}
