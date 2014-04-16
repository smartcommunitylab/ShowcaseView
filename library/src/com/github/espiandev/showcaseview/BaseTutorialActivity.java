package com.github.espiandev.showcaseview;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.espiandev.showcaseview.ShowcaseView.ConfigOptions;
import com.github.espiandev.showcaseview.ShowcaseView.OnShowcaseEventListener;

public abstract class BaseTutorialActivity extends Activity implements
		OnShowcaseEventListener {

	private final static String PASSED_POSITION = "passedPos";
	private final static String PASSED_RADIUS = "passedRadius";
	private final static String PASSED_TITLE = "passedTitle";
	private final static String PASSED_DESC = "passedDesc";
	private final static String PASSED_COLOR = "passedColor";
	private final static String PASSED_BACK_COLOR = "passedBackColor";
	private final static String EXIT_BUTTON_COLOR = "exitButtonColor";
	private final static String PASSED_TITLE_COLOR = "passedTitleColor";
	private final static String PASSED_DESC_COLOR = "passedDescColor";
	private final static String PASSED_IS_LAST = "passedLastTut";
	
	public static final String RESULT_DATA = "tutResult";
	public static final String REDO = "redoTut";
	public static final String OK = "okTut";

	protected ShowcaseView mShowcaseView;
	private Button skip;
 
	public static void newIstance(Activity ctx, int[] position,int radius,Integer titleColor,Integer descColor,Integer backColor,Integer color,Integer exitButtonColor,
			String title, String description,boolean isLast, int requestCode, Class<? extends BaseTutorialActivity> act) {
		Intent caller = new Intent(ctx,act);
		caller.putExtra(PASSED_POSITION, position);
		caller.putExtra(PASSED_TITLE, title);
		caller.putExtra(PASSED_DESC, description);
		caller.putExtra(PASSED_RADIUS, radius);
		caller.putExtra(PASSED_IS_LAST, isLast);
		if(titleColor!=null)
			caller.putExtra(PASSED_TITLE_COLOR, titleColor);
		if(descColor!=null)
			caller.putExtra(PASSED_DESC_COLOR, descColor);
		if(backColor!=null)
			caller.putExtra(PASSED_BACK_COLOR, backColor);
		if(color!=null)
			caller.putExtra(PASSED_COLOR, color);
		if(exitButtonColor!=null)
			caller.putExtra(EXIT_BUTTON_COLOR, exitButtonColor);
		ctx.startActivityForResult(caller, requestCode)	;
	}
	public static void newIstance(Activity ctx, int[] position,int radius,Integer titleColor,Integer descColor,
			String title, String description,boolean isLast, int requestCode, Class<? extends BaseTutorialActivity> act) {
		newIstance(ctx, position, radius, titleColor, descColor, null,null, null, title, description, isLast, requestCode, act);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setResult(RESULT_CANCELED);
		this.finish();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		setResult(RESULT_CANCELED);
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_activity);

		mShowcaseView = (ShowcaseView) findViewById(R.id.tut_sv);
		skip = (Button) findViewById(R.id.skip_tutorial_btn);
		
		setParams(getIntent());

		mShowcaseView.show();
		
		mShowcaseView.setOnShowcaseEventListener(this);
	}

	private void setParams(Intent intent) {
		ConfigOptions co = new ConfigOptions();
		
		
		if(intent.getExtras().getBoolean(PASSED_IS_LAST)){
			co.buttonText = getString(R.string.ok);
			skip.setVisibility(View.GONE);
		}
		else
			co.buttonText = getString(R.string.next_tut);
		
		if(intent.getExtras().getInt(PASSED_DESC_COLOR,Integer.MIN_VALUE)!=Integer.MIN_VALUE)
			co.detailTextColor = intent.getExtras().getInt(PASSED_DESC_COLOR);
		
		if(intent.getExtras().getInt(PASSED_TITLE_COLOR,Integer.MIN_VALUE)!=Integer.MIN_VALUE)
			co.titleTextColor = intent.getExtras().getInt(PASSED_TITLE_COLOR);
		
		if (getIntent().getExtras().getInt(PASSED_BACK_COLOR, Integer.MIN_VALUE) != Integer.MIN_VALUE)
			co.backColor = getIntent().getExtras().getInt(PASSED_BACK_COLOR);
		if (getIntent().getExtras().getInt(PASSED_COLOR, Integer.MIN_VALUE) != Integer.MIN_VALUE)
			co.color = getIntent().getExtras().getInt(PASSED_COLOR);
		if (getIntent().getExtras().getInt(EXIT_BUTTON_COLOR, Integer.MIN_VALUE) != Integer.MIN_VALUE)
			co.exitButtonColor = getIntent().getExtras().getInt(EXIT_BUTTON_COLOR);
		
		co.circleRadius = intent.getExtras().getInt(PASSED_RADIUS)/2;
		mShowcaseView.setConfigOptions(co);
		
		if (mShowcaseView != null) {
			
			
			int[] position = intent.getExtras().getIntArray(PASSED_POSITION);
			if(position!=null)
				mShowcaseView.setShowcasePosition(position[0]+co.circleRadius, position[1]);
			
			String title = intent.getExtras().getString(PASSED_TITLE);
			String desc = intent.getExtras().getString(PASSED_DESC);
			
			mShowcaseView.setText(title, desc);
		}
		mShowcaseView.setConfigOptions(co);
		
	}
	
	protected void setSkipButtonColor(int backColor){
		skip.setBackgroundColor(backColor);
	}
	
	public abstract void skipTutorial(View v);
	

	@Override
	public void onShowcaseViewHide(ShowcaseView showcaseView) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(RESULT_DATA, OK);
		this.setResult(RESULT_OK,returnIntent);
		this.finish();
	}

	@Override
	public void onShowcaseViewShow(ShowcaseView showcaseView) {
		
	}
	
	

}
