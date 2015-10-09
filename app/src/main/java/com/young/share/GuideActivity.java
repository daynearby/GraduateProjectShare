package com.young.share;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.young.adapter.welcomePagerAdapter;

public class GuideActivity extends Activity {

	private List<View> list;
	private LayoutInflater inflater;
	private ViewPager viewPager;
	private ImageView[] im;
	private int[] _id = { R.id.pots0, R.id.pots1, R.id.pots2 };
	private TextView enterMain_tx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		initViews();

	}

	private void initViews() {

		inflater = LayoutInflater.from(this);

		list = new ArrayList<>();

		list.add(inflater.inflate(R.layout.guide_one, null));
		list.add(inflater.inflate(R.layout.guide_two, null));
		list.add(inflater.inflate(R.layout.guide_three, null));

		enterMain_tx = (TextView) list.get(2).findViewById(R.id.id_enterMain);

		im = new ImageView[list.size()];

		for (int i = 0; i < list.size(); i++) {
			im[i] = (ImageView) findViewById(_id[i]);
		}

		welcomePagerAdapter myPagerAdapter = new welcomePagerAdapter(this, list);


		viewPager = (ViewPager) findViewById(R.id.viewpager);

		viewPager.setAdapter(myPagerAdapter);

		viewPager.setOnPageChangeListener(new pageChangeListener());

		enterMain_tx.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intents = new Intent(GuideActivity.this, MainActivity.class);

				startActivity(intents);

				GuideActivity.this.finish();

			}
		});

	}

	private class pageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {

			for (int i = 0; i < list.size(); i++) {

				if (arg0 == i) {
					im[i].setImageResource(R.drawable.login_point_selected);

				} else {
					im[i].setImageResource(R.drawable.login_point);
				}

			}

		}

	}

}
