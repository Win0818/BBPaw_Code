package com.worldchip.bbpaw.bootsetting.view;

import com.worldchip.bbpaw.bootsetting.R;
import com.worldchip.bbpaw.bootsetting.util.Configure;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class GlobalDialog extends Dialog {
    private boolean mBlockKeycodeBack = false;

    public GlobalDialog(Context context, int theme) {
        super(context, theme);
        mBlockKeycodeBack = false;
    }

    public GlobalDialog(Context context) {
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
        public GlobalDialog create() {
            return create(R.style.dialog_global, mDialogContentView);
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
        public GlobalDialog create(int style, View dialogContentView) {
            // instantiate the dialog with the custom Theme
        	if (dialogContentView == null) {
        		return null;
        	}
            final GlobalDialog dialog = new GlobalDialog(context, style);
            dialog.setContentView(dialogContentView);
            dialog.setCancelable(mCancelable);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            
            // 修改窗体宽高
            layoutParams.width = (int) (Configure.getScreenWidth(context) * 0.85);
            layoutParams.height = (int) (Configure.getScreenHeight(context) * 0.85);
            layoutParams.format = PixelFormat.TRANSLUCENT;
            dialogWindow.setAttributes(layoutParams); 
            if (mBlockKeyBack) {
                dialog.setBlockKeyCodeBack(mBlockKeyBack);
            }
            
            return dialog;
        }
    }
    
    
}
