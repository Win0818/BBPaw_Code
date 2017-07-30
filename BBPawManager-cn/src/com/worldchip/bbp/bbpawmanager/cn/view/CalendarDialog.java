package com.worldchip.bbp.bbpawmanager.cn.view;



import com.worldchip.bbp.bbpawmanager.cn.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;


public class CalendarDialog extends Dialog {
    private boolean mBlockKeycodeBack = false;

    public CalendarDialog(Context context, int theme) {
        super(context, theme);
        mBlockKeycodeBack = false;
    }

    public CalendarDialog(Context context) {
        super(context);
    }

    public void setBlockKeyCodeBack(boolean key) {
        mBlockKeycodeBack = key;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mBlockKeycodeBack && (keyCode == KeyEvent.KEYCODE_BACK)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private boolean mBlockKeyBack = false;
        private boolean mCancelable = true;
        private View mDialogContentView;
        private int mWidth =0;
        private int mHeight =0;
        private View mAnchorView = null;
        
        public Builder(Context context) {
            this.context = context;
            mBlockKeyBack = false;
        }

        public Builder setCancelable(boolean mCancelable) {
            this.mCancelable = mCancelable;
            return this;
        }

        public Builder setBlockKeyBack(boolean block) {
            this.mBlockKeyBack = block;
            return this;
        }


        /**
         * Create the custom dialog
         * 
         * @return
         */
        public CalendarDialog create() {
            return create(R.style.calendar_dialog, mDialogContentView);
        }

        /**
         * Create the custom dialog
         * 
         * @return
         */
        public Builder setContentView(View dialogContentView) {
            this.mDialogContentView = dialogContentView;
            return this;
        }
        
        public Builder setViewHeight(int height) {
            this.mHeight = height;
            return this;
        }
        
        public Builder setViewWidth(int width) {
            this.mWidth = width;
            return this;
        }
        
        public Builder setAnchorView(View view) {
            this.mAnchorView = view;
            return this;
        }
        
        /**
         * Create the custom dialog
         * 
         * @param style 皮肤风格
         * @param conentView 布局文件
         * @return
         */
        public CalendarDialog create(int style, View dialogContentView) {
            // instantiate the dialog with the custom Theme
        	if (dialogContentView == null) {
        		return null;
        	}
            final CalendarDialog dialog = new CalendarDialog(context, style);
            dialog.setContentView(dialogContentView);
            dialog.setCancelable(mCancelable);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
            if (mWidth > 0) {
            	layoutParams.width = mWidth;
            } else {
            	layoutParams.width = LayoutParams.WRAP_CONTENT;
            }
            if (mHeight > 0) {
            	layoutParams.height = mHeight;
            } else {
            	layoutParams.height = LayoutParams.WRAP_CONTENT;
            }
            if(mAnchorView != null) {
            	int[] location = new int[2];
            	mAnchorView.getLocationOnScreen(location);
            	int anchorViewHeight = mAnchorView.getHeight();
            	layoutParams.x = location[0];
            	layoutParams.y = location[1] + anchorViewHeight + 2;
            }
            layoutParams.format = PixelFormat.TRANSLUCENT;
            dialogWindow.setAttributes(layoutParams); 
            if (mBlockKeyBack) {
                dialog.setBlockKeyCodeBack(mBlockKeyBack);
            }
            
            return dialog;
        }
    }
    
    
}
