/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.github.espiandev.showcaseview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import com.github.espiandev.showcaseview.ShowcaseView.ConfigOptions;
import com.github.espiandev.showcaseview.ShowcaseView.OnShowcaseEventListener;

/**
 * Starts a chain of tutorial items, showing them one by one until cancelled or
 * finished.
 * 
 * @author raman
 * 
 */
public class TourActivity extends Activity implements OnShowcaseEventListener {

	private final static String PASSED_ITEMS = "items";

	private final static String PASSED_TITLE_COLOR = "passedTitleColor";
	private final static String PASSED_DESC_COLOR = "passedDescColor";
	private final static String PASSED_BACK_COLOR = "passedBackColor";
	private final static String PASSED_COLOR = "passedColor";
	private final static String PASSED_EXIT_BUTTON_COLOR = "passedExitButtonColor";

	public static final String RESULT_DATA = "tutResult";
	public static final String REDO = "redoTut";
	public static final String OK = "okTut";

	protected ShowcaseView mShowcaseView;
	private Button skip;
	private ConfigOptions mConfigOptions = null;
	private Parcelable[] mItemArray = null;
	private int mPosition = 0;

	public static void newIstance(Activity ctx, TutorialItem[] items,
			Integer titleColor, Integer descColor,Integer backColor, Integer color, Integer exitButtonColor,
			int requestCode) {
		Intent caller = getNewIstance(ctx, items, titleColor, descColor,
				requestCode);
		if (backColor != null)
			caller.putExtra(PASSED_BACK_COLOR, backColor);
		if (color != null)
			caller.putExtra(PASSED_COLOR, color);
		if (exitButtonColor != null)
			caller.putExtra(PASSED_EXIT_BUTTON_COLOR, exitButtonColor);
		
		animateOut(ctx);
		
		ctx.startActivityForResult(caller, requestCode);
	}

	@SuppressLint("NewApi")
	private static void animateOut(Activity ctx) {
		if(android.os.Build.VERSION.SDK_INT>4)
			ctx.overridePendingTransition(R.anim.tut_fade_in,R.anim.tut_fade_out);
	}

	public static void newIstance(Activity ctx, TutorialItem[] items,
			Integer titleColor, Integer descColor,
			int requestCode) {
		newIstance(ctx, items, titleColor, descColor,null, null,null, requestCode);
	}

	private static Intent getNewIstance(Activity ctx, TutorialItem[] items,
			Integer titleColor, Integer descColor, int requestCode) {
		Intent caller = new Intent(ctx, TourActivity.class);
		caller.putExtra(PASSED_ITEMS, items);
		if (titleColor != null)
			caller.putExtra(PASSED_TITLE_COLOR, titleColor);
		if (descColor != null)
			caller.putExtra(PASSED_DESC_COLOR, descColor);
		return caller;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setResult(RESULT_CANCELED);
		animateOut(this);
		this.finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		setResult(RESULT_CANCELED);
		animateOut(this);
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_activity);

		mShowcaseView = (ShowcaseView) findViewById(R.id.tut_sv);
		skip = (Button) findViewById(R.id.skip_tutorial_btn);

		mConfigOptions = new ConfigOptions();
		if (getIntent().getExtras()
				.getInt(PASSED_DESC_COLOR, Integer.MIN_VALUE) != Integer.MIN_VALUE)
			mConfigOptions.detailTextColor = getIntent().getExtras().getInt(
					PASSED_DESC_COLOR);
		if (getIntent().getExtras().getInt(PASSED_TITLE_COLOR,
				Integer.MIN_VALUE) != Integer.MIN_VALUE)
			mConfigOptions.titleTextColor = getIntent().getExtras().getInt(
					PASSED_TITLE_COLOR);

		if (getIntent().getExtras().getInt(PASSED_BACK_COLOR, Integer.MIN_VALUE) != Integer.MIN_VALUE)
			mConfigOptions.backColor = getIntent().getExtras().getInt(PASSED_BACK_COLOR);
		if (getIntent().getExtras().getInt(PASSED_COLOR, Integer.MIN_VALUE) != Integer.MIN_VALUE) {
			int color = getIntent().getExtras().getInt(PASSED_COLOR);
//			setSkipButtonColor(color);
			mConfigOptions.color = color;
		}
		if (getIntent().getExtras().getInt(PASSED_EXIT_BUTTON_COLOR, Integer.MIN_VALUE) != Integer.MIN_VALUE) {
			int color = getIntent().getExtras().getInt(PASSED_EXIT_BUTTON_COLOR);
			setSkipButtonColor(color);
		}
		mShowcaseView.setConfigOptions(mConfigOptions);

		mShowcaseView.setOnShowcaseEventListener(this);
		mItemArray = (Parcelable[]) getIntent().getParcelableArrayExtra(
				PASSED_ITEMS);
		if (mItemArray != null && mItemArray.length > 0) {
			setParams((TutorialItem) mItemArray[0], mItemArray.length == 1);
			mShowcaseView.show();
		}
	}

	private void setParams(TutorialItem item, boolean isLast) {

		if (isLast) {
			mConfigOptions.buttonText = getString(R.string.ok);
			skip.setVisibility(View.GONE);
		} else
			mConfigOptions.buttonText = getString(R.string.next_tut);

		if (mShowcaseView != null) {
			mConfigOptions.circleRadius = item.width / 2;

			int[] position = item.position;
			if (position != null)
				mShowcaseView.setShowcasePosition(position[0]
						+ mConfigOptions.circleRadius, position[1]);

			String title = item.title == null ? getString(item.titleId)
					: item.title;
			String desc = item.msg == null ? getString(item.msgId) : item.msg;

			mShowcaseView.setText(title, desc);
		}

		mShowcaseView.setConfigOptions(mConfigOptions);

	}

	protected void setSkipButtonColor(int backColor) {
		skip.setBackgroundColor(backColor);
	}

	public void skipTutorial(View v) {
		setResult(RESULT_CANCELED);
		animateOut(this);
		this.finish();
	}

	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		skipTutorial(null);
	}

	@Override
	public void onShowcaseViewHide(ShowcaseView showcaseView) {
		if (mPosition == mItemArray.length - 1) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra(RESULT_DATA, OK);
			this.setResult(RESULT_OK, returnIntent);
			this.finish();
		} else {
			mPosition++;
			setParams((TutorialItem) mItemArray[mPosition],
					mPosition == mItemArray.length - 1);
			mShowcaseView.show();
		}
	}

	@Override
	public void onShowcaseViewShow(ShowcaseView showcaseView) {

	}

}
