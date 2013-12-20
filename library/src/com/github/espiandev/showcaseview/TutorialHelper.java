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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Tutorial helper class. To manage tutorial it is necessary to
 * provide the parent activity where the tutorial is shown 
 * and the {@link TutorialProvider} instance to provide
 * the information about the tutorial items and views, as 
 * well as to handle the tutorial termination.
 * @author raman
 *
 */
public class TutorialHelper implements Parcelable{

	protected final static int TUTORIAL_REQUEST_CODE = 10000;

	protected Activity mActivity = null;
	
	protected TutorialProvider mProvider;
	
	/**
	 * @param mActivity
	 * @param mItemList
	 */
	public TutorialHelper(Activity mActivity, TutorialProvider provider) {
		super();
		this.mActivity = mActivity;
		this.mProvider = provider;
		assert provider != null;
	}

	public void showTutorials() {
		TutorialItem[] items = new TutorialItem[mProvider.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = mProvider.getItemAt(i);
		}
		if (items != null && items.length > 0) {
			displayShowcaseView(items);
		}
	}
	
	private void displayShowcaseView(TutorialItem[] items) {
		if (items != null && items.length > 0) {
			TourActivity.newIstance(
					mActivity, 
					items, 
					Color.WHITE, 
					null,
					TUTORIAL_REQUEST_CODE);
		}
	}

	public void onTutorialActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TUTORIAL_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				String resData = data.getExtras().getString(BaseTutorialActivity.RESULT_DATA);
				if (resData.equals(BaseTutorialActivity.OK)) {
					mProvider.onTutorialFinished();
				} else {
					mProvider.onTutorialCancelled();
				}
			} else {
				mProvider.onTutorialCancelled();
			}
		}
	}
	
	/**
	 * Show a dialog to initiate the tutorial given the message and the title for
	 * the 'start' button
	 * @param message
	 * @param start
	 */
	public void showTourDialog(String message, String start) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
				.setMessage(message)
				.setPositiveButton(start,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								showTutorials();
							}
						})
				.setNeutralButton(mActivity.getString(android.R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								mProvider.onTutorialCancelled();
							}
						});
		builder.create().show();
	}

	public interface TutorialProvider {
		void onTutorialCancelled();
		void onTutorialFinished();
		
		TutorialItem getItemAt(int pos);
		int size();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

}
