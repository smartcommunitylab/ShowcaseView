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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

/**
 * Tutorial helper class. To manage tutorial it is necessary to
 * provide the parent activity where the tutorial is shown 
 * and the {@link TutorialProvider} instance to provide
 * the information about the tutorial items and views, as 
 * well as to handle the tutorial termination.
 * @author raman
 *
 */
public class ListViewTutorialHelper extends TutorialHelper {


	private static final String TUT_PREFS = "tut_prefs";
	private static final String TOUR_PREFS = "wantTour";

	private TutorialItem lastShown = null;
	
	/**
	 * @param mActivity
	 * @param mItemList
	 */
	public ListViewTutorialHelper(Activity mActivity, TutorialProvider provider) {
		super(mActivity, provider);
	}

	public void showTutorials() {
		reset();
		setWantTour(mActivity, true);
		startShow();
	}

	/**
	 * 
	 */
	private void startShow() {
		TutorialItem t = getFirstValidTutorial();
		if (t != null) {
			lastShown = t;
			displayShowcaseView(t, mProvider.getItemAt(mProvider.size()-1)==t);
		} else
			setWantTour(mActivity, false);
		}
	private boolean wantTour() {
		return getTutorialPreferences(mActivity).getBoolean(TOUR_PREFS, false);
	}
	
	private void reset() {
		for (int i = 0; i < mProvider.size(); i++) {
			setTutorialVisibility(mProvider.getItemAt(i), false);
		}
	}

	private void displayShowcaseView(TutorialItem item, boolean isLast) {
		BaseTutorialActivity.newIstance(
				mActivity, 
				item.position,
				item.width,
				Color.WHITE, 
				null,
				mActivity.getString(item.titleId),
				mActivity.getString(item.msgId),
				isLast,
				TUTORIAL_REQUEST_CODE,
				TutorialActivity.class);
	}

	private TutorialItem getFirstValidTutorial() {
		TutorialItem t = getLastTutorialNotShown();
		return t;
	}

	private static SharedPreferences getTutorialPreferences(Context ctx) {
		SharedPreferences out = ctx.getSharedPreferences(TUT_PREFS, Context.MODE_PRIVATE);
		return out;
	}

	public static void setWantTour(Context ctx, boolean want) {
		Editor edit = getTutorialPreferences(ctx).edit();
		edit.putBoolean(TOUR_PREFS, want);
		edit.commit();
	}

	private boolean isTutorialShown(TutorialItem t) {
		return getTutorialPreferences(mActivity).getBoolean(t.id, false);
	}

	private void setTutorialVisibility(TutorialItem t, boolean visibility) {
		Editor edit = getTutorialPreferences(mActivity).edit();
		edit.putBoolean(t.id, visibility);
		edit.commit();
	}


	private TutorialItem getLastTutorialNotShown() {
		for (int i = 0; i < mProvider.size(); i++) {
			TutorialItem item = mProvider.getItemAt(i);
			if (!isTutorialShown(item)) return item;
		}
		return null;
	}

	public void onTutorialActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TUTORIAL_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				String resData = data.getExtras().getString(BaseTutorialActivity.RESULT_DATA);
				if (resData.equals(BaseTutorialActivity.OK)) {
					setTutorialVisibility(lastShown, true);
					if (lastShown == mProvider.getItemAt(mProvider.size()-1)) {
						mProvider.onTutorialFinished();
					} else  if (wantTour()) {
						startShow();
					}
				} else {
					mProvider.onTutorialCancelled();
				}
			} else {
				mProvider.onTutorialCancelled();
			}
		}
	}
}
