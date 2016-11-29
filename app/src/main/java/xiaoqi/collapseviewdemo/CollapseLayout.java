package xiaoqi.collapseviewdemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by xiaoqi on 2016/11/25.
 */

public class CollapseLayout extends FrameLayout {
	View rlShow;
	View llContent;
	View tabLayout;
	View viewPager;

	private final static String TAG = "VDHLayout";
	private final static String HEIGHT_TAG = "DEMANDS";
	ViewDragHelper viewDragHelper;
	int width;
	int height;
	//系统状态栏高度
	int systemBarHeight;

	boolean hasExpand = false;
	//初始的图片高度
	int initPicHeight ;
	Context context;

	public CollapseLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		width = getResources().getDisplayMetrics().widthPixels;
		height = getResources().getDisplayMetrics().heightPixels;
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
		systemBarHeight = resources.getDimensionPixelSize(resourceId);
		initPicHeight = height / 3 - dip2px(context, 50);
		viewDragHelper = ViewDragHelper.create(this,1.0f, new ViewDragHelper.Callback() {
			@Override
			public boolean tryCaptureView(View child, int pointerId) {
				Log.i(TAG,"tryCaptureView");
				return child == llContent || child == rlShow;
			}

			@Override
			public int clampViewPositionVertical(View child, int top, int dy) {
				Log.i(HEIGHT_TAG,"clampViewPositionVertical:"+top);
				int newTop = top;
				if(child == llContent){
					int topBounds =  getMeasuredHeight() - llContent.getMeasuredHeight();
					int bottomBounds = getMeasuredHeight();
					if(top < topBounds){
						newTop = topBounds;
					}else if(top > bottomBounds){
						newTop = bottomBounds;
					}else {
						newTop = top;
					}
				}else if(child == rlShow){
					if(llContent.getTop() > getMeasuredHeight()+systemBarHeight){
						newTop = rlShow.getTop();
					}
				}
				tabLayout.setVisibility(GONE);
				return newTop;
			}

			@Override
			public void onViewPositionChanged(View changedView, int left, int top, int dx, final int dy) {
				if(changedView == llContent){
					rlShow.offsetTopAndBottom(dy/2);
				}else if(changedView == rlShow){
					llContent.offsetTopAndBottom(2 * dy);
				}
			}

			@Override
			public void onViewCaptured(View capturedChild, int activePointerId) {
				super.onViewCaptured(capturedChild, activePointerId);
			}

			@Override
			public void onViewReleased(View releasedChild, float xvel, float yvel) {
				super.onViewReleased(releasedChild, xvel, yvel);
				//releasedChild到屏幕顶部的距离
				int offsetToTop = llContent.getTop();
				//当显示内容最上位置的坐标>初始图片高度时才会去展开和缩小
				if(offsetToTop > initPicHeight){
					if(offsetToTop >= tabLayout.getTop()){
						expendPicView();
					}else if(!hasExpand){
						expendPicView();
					}else {
						foldPicView();
					}
				}else {
					hasExpand = false;
					tabLayout.setVisibility(GONE);
				}
			}

			@Override
			public int getViewVerticalDragRange(View child) {
				return  getMeasuredHeight();
			}


		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return viewDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public void computeScroll()
	{
		if(viewDragHelper.continueSettling(true))
		{
			ViewCompat.postInvalidateOnAnimation(this);
			postInvalidate();
//			invalidate();
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		Log.i(TAG,"onFinishInflate");
		rlShow = getChildAt(0);
		llContent = getChildAt(1);
		tabLayout = ((RelativeLayout)rlShow).getChildAt(2);
		viewPager = ((RelativeLayout)rlShow).getChildAt(1);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();
		params.height = height/2;
		viewPager.setLayoutParams(params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.i(TAG,"onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
//		setMeasuredDimension(sizeWidth, sizeHeight);

		int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		llContent.measure(w, h);
		int height = llContent.getMeasuredHeight();
		int contentWidthSpec = MeasureSpec.makeMeasureSpec(sizeWidth,MeasureSpec.EXACTLY);
		int contentHeightSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
		llContent.measure(contentWidthSpec,contentHeightSpec);

		int picShowWidthSpec = MeasureSpec.makeMeasureSpec(sizeWidth,MeasureSpec.EXACTLY);
		int picShowHeightSpec = MeasureSpec.makeMeasureSpec(sizeHeight,MeasureSpec.EXACTLY);
		rlShow.measure(picShowWidthSpec,picShowHeightSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Log.i(TAG,"hasExpand："+hasExpand);
		if(hasExpand){
			rlShow.layout(0 , 0 , getMeasuredWidth() ,height);
			llContent.layout(0, height,getMeasuredWidth(), llContent.getMeasuredHeight() + height);
		}else {
			rlShow.layout(0, - initPicHeight , getMeasuredWidth(), height - initPicHeight);
			llContent.layout(0, initPicHeight,getMeasuredWidth(), llContent.getMeasuredHeight() + initPicHeight);
		}
	}

	/**
	 * 折叠图片浏览界面
	 */
	private void foldPicView() {
		hasExpand = false;
		viewDragHelper.smoothSlideViewTo(llContent,0,initPicHeight);
		tabLayout.setVisibility(GONE);
		postInvalidate();
	}

	/**
	 * 展开图片浏览界面
	 */
	private void expendPicView() {
		hasExpand = true;
		viewDragHelper.smoothSlideViewTo(llContent,0,height);
//		if(releasedChild == llContent){
//			viewDragHelper.settleCapturedViewAt(0,height);
//		}else if(releasedChild == rlShow){
//			viewDragHelper.settleCapturedViewAt(0, 0);
//		}
		tabLayout.setVisibility(VISIBLE);
		postInvalidate();
	}

	public static int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}

	public void viewPagerClick(){
		if(hasExpand){
			foldPicView();
		}else{
			expendPicView();
		}
	}
}
