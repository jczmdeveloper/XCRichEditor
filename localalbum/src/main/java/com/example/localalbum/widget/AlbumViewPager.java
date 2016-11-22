package com.example.localalbum.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.localalbum.R;
import com.example.localalbum.common.LocalImageHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.List;


/**
 * @ClassName: AlbumViewPager
 * @Description:  自定义viewpager  优化了事件拦截
 * @author LinJ
 * @date 2015-1-9 下午5:33:33 
 *
 */
public class AlbumViewPager extends ViewPager implements MatrixImageView.OnMovingListener {
	public final static String TAG="AlbumViewPager";


	/**  当前子控件是否处理拖动状态  */
	private boolean mChildIsBeingDragged=false;

	/**  界面单击事件 用以显示和隐藏菜单栏 */
	private MatrixImageView.OnSingleTapListener onSingleTapListener;
	DisplayImageOptions options;
    //本地图片的option
	DisplayImageOptions localOptions;

	private  class ProcressListener implements ImageLoadingProgressListener {
		private View mView = null;

		public ProcressListener(View view){
			this.mView = view;
		}
		@Override
		public void onProgressUpdate(String arg0, View view, int current, int total) {
			TextView loadText = (TextView) mView.findViewById(R.id.current_procress);
			loadText.setText(String.valueOf(100 * current / total) + "%");
			loadText.bringToFront();
		}

	}


	private ImageLoadingListener loadingListenerr = new ImageLoadingListener(){

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
		}

		@Override
		public void onLoadingComplete(String arg0, View view, Bitmap arg2) {
			view.setVisibility(View.VISIBLE);
			view.getParent().bringChildToFront(view);
		}

		@Override
		public void onLoadingFailed(String arg0, View view, FailReason arg2) {
//			view.setVisibility(View.VISIBLE);
//			view.getParent().bringChildToFront(view);
		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
		}

	};





	/**  播放按钮点击事件 */
	public AlbumViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		localOptions=new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(false)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY)
				.displayer(new SimpleBitmapDisplayer()).build();
		//DisplayImageOptions.getDefaltOpitions(0, R.drawable.dangkr_no_picture,
		//((AppContext)context.getApplicationContext()).getWindowWidth());	
	}






	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(mChildIsBeingDragged)
			return false;
		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public void startDrag() {
		// TODO Auto-generated method stub
		mChildIsBeingDragged=true;
	}


	@Override
	public void stopDrag() {
		// TODO Auto-generated method stub
		mChildIsBeingDragged=false;
	}

	public void setOnSingleTapListener(MatrixImageView.OnSingleTapListener onSingleTapListener) {
		this.onSingleTapListener = onSingleTapListener;
	}

	public class ViewPagerAdapter extends PagerAdapter {
		private List<String> paths;//大图地址 如果为网络图片 则为大图url
		public ViewPagerAdapter(List<String> paths){
			this.paths=paths;
		}

		@Override
		public int getCount() {
			return paths.size();
		}

		@Override
		public Object instantiateItem(ViewGroup viewGroup, int position) {
			//注意，这里不可以加inflate的时候直接添加到viewGroup下，而需要用addView重新添加
			//因为直接加到viewGroup下会导致返回的view为viewGroup
			View imageLayout = inflate(getContext(),R.layout.item_album_pager, null);
			viewGroup.addView(imageLayout);
			assert imageLayout != null;
			MatrixImageView imageView = (MatrixImageView) imageLayout.findViewById(R.id.image);
			imageView.setOnMovingListener(AlbumViewPager.this);
			imageView.setOnSingleTapListener(onSingleTapListener);
			String path=paths.get(position);
			ImageLoader.getInstance().displayImage(path, imageView, localOptions, loadingListenerr, new ProcressListener(imageLayout));
			return imageLayout;
		}



		@Override
		public int getItemPosition(Object object) {
			//在notifyDataSetChanged时返回None，重新绘制
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int arg1, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}


	}

	public class LocalViewPagerAdapter extends PagerAdapter {
		private List<LocalImageHelper.LocalFile> paths;//大图地址 如果为网络图片 则为大图url
		public LocalViewPagerAdapter(List<LocalImageHelper.LocalFile> paths){
			this.paths=paths;
		}

		@Override
		public int getCount() {
			return paths.size();
		}

		@Override
		public Object instantiateItem(ViewGroup viewGroup, int position) {
			//注意，这里不可以加inflate的时候直接添加到viewGroup下，而需要用addView重新添加
			//因为直接加到viewGroup下会导致返回的view为viewGroup
			View imageLayout = inflate(getContext(), R.layout.item_album_pager, null);
			viewGroup.addView(imageLayout);
			assert imageLayout != null;
			MatrixImageView imageView = (MatrixImageView) imageLayout.findViewById(R.id.image);
			imageView.setOnMovingListener(AlbumViewPager.this);
			imageView.setOnSingleTapListener(onSingleTapListener);
			LocalImageHelper.LocalFile path=paths.get(position);
				ImageLoader.getInstance().displayImage(path.getOriginalUri(), new ImageViewAware(imageView), localOptions, loadingListenerr,
						new ProcressListener(imageLayout),path.getOrientation());

			return imageLayout;
		}



		@Override
		public int getItemPosition(Object object) {
			//在notifyDataSetChanged时返回None，重新绘制
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int arg1, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

}