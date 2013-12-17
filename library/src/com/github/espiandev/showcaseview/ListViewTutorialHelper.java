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
import android.view.View;
import android.widget.ListView;

/**
 * Tutorial helper class that uses a list view as the list of reference
 * points for the tutorial steps.  
 * To manage tutorial it is necessary to
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

	private int lastShown = -1;
	
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
	 * Fill in the parameters for the tutorial items using 
	 * the element at 'index' in the view to calculate the position and width.
	 * If needed, the required element will be scrolled to.
	 * 
	 * @param item {@link TutorialItem} instance to populate
	 * @param index position of the element in the list 
	 * @param listView {@link ListView} holding the steps of the tutorial
	 * @param itemId layout ID of the view to search for
	 */
	public static void fillTutorialItemParams(TutorialItem item, int index, ListView listView, int itemId) {
		if (listView != null) {
			int firstVisible = listView.getFirstVisiblePosition();
			int lastVisible = listView.getLastVisiblePosition();
			int shift = 0;
			View v = null;
			if (index <= firstVisible) {
				v = listView.getChildAt(index);
				shift = -v.getTop();
				listView.setSelection(index);
			} else if (index >= lastVisible && listView.getChildAt(index) != null) {
				// TODO
			} else {
				v = listView.getChildAt(index);
			}
			if (v != null) {
				View logo = v.findViewById(itemId);
				if (logo != null) {
					item.width = logo.getWidth();
					item.position = new int[2];
					logo.getLocationOnScreen(item.position);
					item.position[1] += shift;
				}
			}
		}
	}
	
	private void startShow() {
		int idx = getLastTutorialShown() + 1;
		if (idx >= 0 && idx < mProvider.size()) {
			lastShown = idx;
			displayShowcaseView(mProvider.getItemAt(lastShown), mProvider.size()-1==lastShown);
		} else
			setWantTour(mActivity, false);
		}
	private boolean wantTour() {
		return getTutorialPreferences(mActivity).getBoolean(TOUR_PREFS, false);
	}
	
	private void reset() {
		setLastShown(-1);
	}

	private void displayShowcaseView(TutorialItem item, boolean isLast) {
		BaseTutorialActivity.newIstance(
				mActivity, 
				item.position,
				item.width,
				Color.WHITE, 
				null,
				item.title == null ? mActivity.getString(item.titleId) : item.title,
				item.msg == null ? mActivity.getString(item.msgId) : item.msg,
				isLast,
				TUTORIAL_REQUEST_CODE,
				TutorialActivity.class);
	}

	private static SharedPreferences getTutorialPreferences(Context ctx) {
		SharedPreferences out = ctx.getSharedPreferences(TUT_PREFS, Context.MODE_PRIVATE);
		return out;
	}

	static void setWantTour(Context ctx, boolean want) {
		Editor edit = getTutorialPreferences(ctx).edit();
		edit.putBoolean(TOUR_PREFS, want);
		edit.commit();
	}

	private int getLastTutorialShown() {
		return getTutorialPreferences(mActivity).getInt("idx", -1);
	}

	private void setLastShown(int idx) {
		Editor edit = getTutorialPreferences(mActivity).edit();
		edit.putInt("idx", idx);
		edit.commit();
	}


	public void onTutorialActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TUTORIAL_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				String resData = data.getExtras().getString(BaseTutorialActivity.RESULT_DATA);
				if (resData.equals(BaseTutorialActivity.OK)) {
					setLastShown(lastShown);
					if (lastShown == mProvider.size()-1) {
						mProvider.onTutorialFinished();
					} else if (wantTour()) {
						startShow();
					} else {
						mProvider.onTutorialCancelled();
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
