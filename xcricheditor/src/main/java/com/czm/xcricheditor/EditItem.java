package com.czm.xcricheditor;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by caizhiming on 2016/11/21.
 */

public class EditItem implements Parcelable{
    private int type; //0：文本，1：图片
    private String content;//type=0 表示text，type=1 表示 path or url
    private Uri uri;//uri




    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public EditItem() {
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.content);
        dest.writeParcelable(this.uri, flags);
    }

    protected EditItem(Parcel in) {
        this.type = in.readInt();
        this.content = in.readString();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<EditItem> CREATOR = new Creator<EditItem>() {
        @Override
        public EditItem createFromParcel(Parcel source) {
            return new EditItem(source);
        }

        @Override
        public EditItem[] newArray(int size) {
            return new EditItem[size];
        }
    };
}
