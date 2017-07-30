
package com.worldchip.bbp.bbpawmanager.cn.view;
import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.utils.Configure;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GlobalAlertDialog extends Dialog {
    private boolean mBlockKeycodeBack = false;

    public GlobalAlertDialog(Context context, int theme) {
        super(context, theme);
        mBlockKeycodeBack = false;
    }

    public GlobalAlertDialog(Context context) {
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

        private Context mContext;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private boolean mBlockKeyBack = false;
        private boolean mCancelable = true;
        private boolean mShowTitle = true;

        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener neutralButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;
        private Boolean positiveButtonAutoCloseDialog = true;
        private Boolean neutralButtonAutoCloseDialog = true;

        public Builder(Context context) {
            this.mContext = context;
            mBlockKeyBack = false;
        }

        /**
         * Set the Dialog message from String<br/>
         * 注：当设置Message之后，setBodyView()失效
         * 
         * @param title
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource<br/>
         * 注：当设置Message之后，setBodyView()失效
         * 
         * @param title
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) mContext.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         * 
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) mContext.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         * 
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 是否显示Line
         * T5下载提示没有line
         * @param mShowTitle
         * @return
         */
        public Builder setmShowTitle(boolean mShowTitle) {
            this.mShowTitle = mShowTitle;
            return this;
        }

        public Builder setCancelable(boolean mCancelable) {
            this.mCancelable = mCancelable;
            return this;
        }

        /**
         * Set a custom content view for the Dialog. If a message is set, the
         * contentView is not added to the Dialog...
         * 
         * @param v
         * @return
         */
        public Builder setBodyView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set a custom content view for the Dialog. If a message is set, the
         * contentView is not added to the Dialog...
         * 
         * @param v
         * @return
         */
        public Builder setBodyView(int v) {
            this.contentView = ((LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(v, null);
            return this;
        }

        public Builder setBlockKeyBack(boolean block) {
            this.mBlockKeyBack = block;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         * 
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) mContext.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * 
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * 
         * @param negativeButtonText
         * @param isAutoCloseDialog
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText, Boolean isAutoCloseDialog,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            this.positiveButtonAutoCloseDialog = isAutoCloseDialog;
            return this;
        }


        /**
         * Set the negative button resource and it's listener
         * 
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) mContext.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         * 
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         * dialogLayout为-1时，默认使用alert_dialog布局
         * @return
         */
        public GlobalAlertDialog create(int dialogLayout) {
        	if (dialogLayout != -1) {
        		return create(R.style.Dialog_global, dialogLayout);
        	} else {
        		return create(R.style.Dialog_global, R.layout.alert_dialog);
        	}
        }

        /**
         * Create the custom dialog
         * 
         * @param style 皮肤风格
         * @param conentView 布局文件
         * @return
         */
        public GlobalAlertDialog create(int style, int dialogLayout) {
            // instantiate the dialog with the custom Theme
            final GlobalAlertDialog dialog = new GlobalAlertDialog(mContext, style);
            dialog.setContentView(dialogLayout);
            dialog.setCancelable(mCancelable);
            Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(),"font/changcheng.TTF");
            
            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
           // layoutParams.width = (int) (Configure.getScreenWidth(mContext) * 0.5);
            //layoutParams.height = (int) (Configure.getScreenHeight(mContext) * 0.5);
            layoutParams.width = layoutParams.WRAP_CONTENT;
            layoutParams.height = layoutParams.WRAP_CONTENT;
            // set the dialog title
            TextView titleTv = (TextView) dialog.findViewById(R.id.title);
            if (titleTv != null) {
            	titleTv.setTypeface(typeFace);
            	titleTv.setText(this.title);
            }

            // set block key code back
            if (mBlockKeyBack) {
                dialog.setBlockKeyCodeBack(mBlockKeyBack);
            }

            int btnNum = 0;
            Button btnPositive = null, btnNegative = null;
            // set the confirm button
            btnPositive = ((Button) dialog.findViewById(R.id.positiveButton));
            if (positiveButtonText != null) {
                btnNum++;
                btnPositive.setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    btnPositive.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                            if (positiveButtonAutoCloseDialog) {
                                dialog.dismiss();
                            }
                        }
                    });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                btnPositive.setVisibility(View.GONE);
            }

            // set the cancel button
            btnNegative = ((Button) dialog.findViewById(R.id.negativeButton));
            if (negativeButtonText != null) {
                btnNum++;
                btnNegative.setText(negativeButtonText);
                btnNegative.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (negativeButtonClickListener != null)
                            negativeButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_NEGATIVE);
                        dialog.cancel();
                    }
                });
            } else {
                // if no confirm button just set the visibility to GONE
                btnNegative.setVisibility(View.GONE);
            }
            if (btnNum == 1) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                if (btnPositive != null) {
                    btnPositive.setLayoutParams(params);
                }
                if (btnNegative != null) {
                    btnNegative.setLayoutParams(params);
                }
            }
            if (btnNum > 2) {
                if (btnPositive != null) {
                    btnPositive.setPadding(10, 0, 10, 0);
                }
                if (btnNegative != null) {
                    btnNegative.setPadding(10, 0, 10, 0);
                }
            }

            // set the content message
            LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
            if (message != null) {
                TextView messageTv = (TextView) dialog.findViewById(R.id.message);
            	if (messageTv != null) {
            		messageTv.setTypeface(typeFace);
            		messageTv.setText(message);
            		messageTv.setMovementMethod(ScrollingMovementMethod.getInstance());
            	}
            } else if (contentView != null) {
                // if no message set add the contentView to the dialog body
                content.removeAllViews();
                content.addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT));
            }

            return dialog;
        }

    }

}
