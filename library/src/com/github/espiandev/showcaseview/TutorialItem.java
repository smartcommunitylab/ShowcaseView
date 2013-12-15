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

import android.os.Parcel;
import android.os.Parcelable;


public class TutorialItem implements Parcelable {
	
	public String id;
	public int[] position;
	public int width;
	public int titleId;
	public int msgId;

	public static final Parcelable.Creator<TutorialItem> CREATOR  = new Parcelable.Creator<TutorialItem>() {
		public TutorialItem createFromParcel(Parcel in) {
			return new TutorialItem(in);
		}

		public TutorialItem[] newArray(int size) {
			return new TutorialItem[size];
		}
	};
	
	public TutorialItem(String id, int[] position, int width, int titleId, int msgId) {
		super();
		this.id = id;
		this.titleId = titleId;
		this.msgId = msgId;
		this.position = position;
		this.width = width;
	}
	
	public TutorialItem(Parcel in) {
		this.id = in.readString();
		this.position = new int[]{in.readInt(),in.readInt()};
		this.width = in.readInt();
		this.titleId = in.readInt();
		this.msgId = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeInt(position[0]);
		dest.writeInt(position[1]);
		dest.writeInt(width);
		dest.writeInt(titleId);
		dest.writeInt(msgId);
	}
}