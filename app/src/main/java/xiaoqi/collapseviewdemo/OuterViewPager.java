package xiaoqi.collapseviewdemo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 外层的ViewPager，这样可以嵌套滑动
 *
 * Created by xiaoqi on 2016/11/28.
 */

public class OuterViewPager extends ViewPager {
	public OuterViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OuterViewPager(Context context) {
		super(context);
	}
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		if(v != this && v instanceof ViewPager) {
			int currentItem = ((ViewPager) v).getCurrentItem();
			int countItem = ((ViewPager) v).getAdapter().getCount();
			if((currentItem==(countItem-1) && dx<0) || (currentItem==0 && dx>0)){
				return false;
			}
			return true;
		}
		return super.canScroll(v, checkV, dx, x, y);
	}
}
