package xiaoqi.collapseviewdemo;

import android.graphics.Matrix;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.attr.width;
import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {
	OuterViewPager outerViewPager;
	TabLayout tabLayout;
	CollapseLayout vdhLayout;
	ArrayList<String> tabList = new ArrayList<>();
	ArrayList<Integer> imageList = new ArrayList<>();
	ArrayList<ImageView> imageViewList = new ArrayList<>();
	ArrayList<ViewPager> outerViewPagerList = new ArrayList<>();
	int width;
	Matrix matrix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		width = getResources().getDisplayMetrics().widthPixels;
		matrix = new Matrix();
		vdhLayout = (CollapseLayout) findViewById(R.id.collapseLayout);
		outerViewPager = (OuterViewPager) findViewById(R.id.outerViewPager);
		tabLayout = (TabLayout) findViewById(R.id.tabLayout);
		tabList.add("户型图");
		tabList.add("户型图");
		tabList.add("户型图");
		tabList.add("户型图");
		tabList.add("户型图");
		tabList.add("户型图");
		tabList.add("户型图");
		imageList.add(R.drawable.pic1);
		imageList.add(R.drawable.pic2);
		imageList.add(R.drawable.pic5);
		tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
		for(int i=0;i<tabList.size();i++){
			tabLayout.addTab(tabLayout.newTab().setText(tabList.get(i)));
		}
		outerViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				outerViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
		for(int i=0;i<tabList.size();i++){
			ViewPager viewPager = new ViewPager(this);
			viewPager.setLayoutParams(new ViewPager.LayoutParams());
			viewPager.setAdapter(new InnerViewPagerAdapter());
			outerViewPagerList.add(viewPager);
		}
		MyOuterViewPagerAdapter adapter = new MyOuterViewPagerAdapter();
		outerViewPager.setAdapter(adapter);
	}


	/**
	 * 中间的ViewPager中存放的是图片
	 */
	class InnerViewPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return imageList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = new ImageView(MainActivity.this);
			imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
					.MATCH_PARENT,ViewPager.LayoutParams.WRAP_CONTENT));
			imageView.setScaleType(ImageView.ScaleType.MATRIX);
			imageView.setImageResource(imageList.get(position));
			//设置图片大小
			int intrinsicWidth = imageView.getDrawable().getIntrinsicWidth();
			float f = width * 1.0f / intrinsicWidth;
			matrix.reset();
			matrix.setScale(f, f);
			matrix.postTranslate(0.0F, 0F);
			imageView.setImageMatrix(matrix);
			container.addView(imageView);
			imageViewList.add(imageView);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					vdhLayout.viewPagerClick();
				}
			});
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(imageViewList.get(position));
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return super.getPageTitle(position);
		}
	}


	/**
	 * 外层ViewPager，中间添加的是子Viewpager
	 */
	class MyOuterViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return tabList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view==object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(outerViewPagerList.get(position));
			Toast.makeText(MainActivity.this,"" + position,Toast.LENGTH_SHORT).show();
			return outerViewPagerList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(outerViewPagerList.get(position));
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabList.get(position);
		}
	}
}
