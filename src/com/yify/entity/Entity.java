package com.yify.entity;

import android.app.Activity;
import android.os.Parcelable;
import android.view.View;

public abstract class Entity implements Parcelable {
	
	public static final int ENTITY = 0X7895878;
	
	@Override
	public int describeContents() {
		return ENTITY;
	}
	/**
	 * creates a single activity view for an entity.
	 * @param parent the parent activity to gain access to resources.
	 * @return a single view for this entity
	 */
	public abstract View onCreateView(Activity parent);
	/**
	 * creates a view used inside a main view,
	 * for example a view for a horizontal scroll view.
	 * @param parent the parent activity to gain access to resources.
	 * @return a single subview for this entity
	 */
	public abstract View onCreateSubView(Activity parent);

}
