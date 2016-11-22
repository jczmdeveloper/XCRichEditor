package com.example.localalbum.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


/** 
 * @ClassName: ThumbnailView 
 * @Description:  点击时显示明暗变化(滤镜效果)的ImageView
 * @author LinJ
 * @date 2015-1-6 下午2:13:46 
 *  
 */
public class FilterImageView extends ImageView {
	public FilterImageView(Context context){
		super(context);
	}
	public FilterImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
    public FilterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			//在按下事件中设置滤镜
			setFilter();
			break;
		case MotionEvent.ACTION_UP:
			//由于捕获了Touch事件，需要手动触发Click事件
			performClick();
		case MotionEvent.ACTION_CANCEL:
			//在CANCEL和UP事件中清除滤镜
			removeFilter();
			break;
		default:
			break;
		}
		return true;
	}

	/**  
	 *   设置滤镜
	 */
	private void setFilter() {
		//先获取设置的src图片
		Drawable drawable=getDrawable();
		//当src图片为Null，获取背景图片
		if (drawable==null) {
			drawable=getBackground();
		}
		if(drawable!=null){
			//设置滤镜
			drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
		}
	}
	/**  
	 *   清除滤镜
	 */
	private void removeFilter() {
		//先获取设置的src图片
		Drawable drawable=getDrawable();
		//当src图片为Null，获取背景图片
		if (drawable==null) {
			drawable=getBackground();
		}
		if(drawable!=null){
			//清除滤镜
			drawable.clearColorFilter();
		}
	}



}
