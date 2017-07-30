package com.egreat.adlauncher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;

public class SlideGridView extends GridView {

	public SlideGridView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public SlideGridView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int count = getAdapter().getCount();
		int numColumns = getNumColumns();
		int currPosition = getSelectedItemPosition();
		// Log.d(TAG, "onKeyDown count:" + count + ";numColumns:" + numColumns
		// + ";currPosition:" + currPosition);

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			scroll(true);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			scroll(false);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			if ((currPosition > 0) && (currPosition % numColumns == 0)) {
				setSelection(currPosition);
				scroll(false);
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if ((currPosition < (count - 1))
					&& ((currPosition + 1) % numColumns == 0)) {
				setSelection(currPosition);
				scroll(true);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void scroll(boolean direction) {
		View localView = getSelectedView();
		if (localView == null)
			return;
		int height = localView.getHeight();
		int[] arrayOfInt = new int[2];
		localView.getLocationInWindow(arrayOfInt);
		// Log.d(TAG, "scroll height:" + height + ";arrayOfInt[0]:"
		// + arrayOfInt[0] + ",arrayOfInt[1]:" + arrayOfInt[1]);
		if (arrayOfInt != null) {
			if (direction) {
				if (arrayOfInt[1] <= height)
					return;
				smoothScrollBy(height, 500);
			} else {
				if (arrayOfInt[1] >= height)
					return;
				smoothScrollBy(-height, 500);
			}
		}
	}
}
