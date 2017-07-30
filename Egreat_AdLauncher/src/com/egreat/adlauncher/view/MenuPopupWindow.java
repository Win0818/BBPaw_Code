package com.egreat.adlauncher.view;

import java.util.List;

import com.egreat.adlauncher.adapter.ShortcutAppAdapter;
import com.egreat.adlauncher.db.ApkInfo;
import com.egreat.adlauncher.util.Utils;
import com.lidroid.xutils.view.annotation.event.OnKey;
import com.mgle.launcher.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout.LayoutParams;

public class MenuPopupWindow extends PopupWindow{

	protected static final String LOGTAG = "--MenuPopupWindow--";
	private Context mContext;
	private View mParent;
	private Handler mHandler;
	private Resources mRes;
	private ApkInfo mCurrentShortcutApkInfo;
	
	private List<ApkInfo> mShortcutApkList = null;
	private ShortcutAppAdapter mShortcutAdapter;
	private DisplayMetrics mDm;
	
	public MenuPopupWindow(Context context,View parent,Handler mHandler, 
			List<ApkInfo> shortcutApkList, ShortcutAppAdapter shortcutAdapter,
			DisplayMetrics dm)
	{
		this.mParent = parent;
		this.mContext = context;
		this.mHandler = mHandler;
		mShortcutApkList = shortcutApkList;
		mShortcutAdapter = shortcutAdapter;
		mDm = dm;
		
		mRes = context.getResources();
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_menu_layout, null);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setAnimationStyle(R.style.MenuPopupAnimation);
		setBackgroundDrawable(new ColorDrawable(0));
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		
		initView(view);
	}

	public void show(){
		showAtLocation(mParent,  Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	
	private void initView(View view) {
        GridView gridMenu = (GridView)view.findViewById(R.id.grid_menu);

        int size = mShortcutApkList.size(); 
        int length = (int) (mDm.widthPixels /6.857);
        float density = mDm.density;
        int gridviewWidth = (int) (size * (length ) * density);
        
        Log.e(LOGTAG, "gridviewWidth..gridviewWidth="+gridviewWidth);
       
        int width = size*120;
        LinearLayout.LayoutParams params;
        if(width > mDm.widthPixels){
        	params = new LinearLayout.LayoutParams(
        			LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }else{
        	params = new LinearLayout.LayoutParams(
        			width, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
		
		gridMenu.setGravity(Gravity.BOTTOM);
		gridMenu.setLayoutParams(params);
		gridMenu.setNumColumns(size); 
	    gridMenu.setAdapter(mShortcutAdapter);
	    mShortcutAdapter.setSelctItem(0);
	    mCurrentShortcutApkInfo = mShortcutAdapter.getItem(0);
		
		gridMenu.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent key) {
				Log.e(LOGTAG, "keycode="+key.getKeyCode());
				if(key.getRepeatCount()==0 && key.getAction()==KeyEvent.ACTION_DOWN){
					int index = mShortcutAdapter.getSelectedIndex(); 
					Log.e(LOGTAG, "keycode="+key.getKeyCode()+"; index="+index);
					switch(key.getKeyCode()){
					    case KeyEvent.KEYCODE_BACK:
					    	return true;
					    case KeyEvent.KEYCODE_DPAD_CENTER:
					    	if(mCurrentShortcutApkInfo!=null){
						    	mHandler.removeMessages(Utils.RUN_CURRENT_SHORTCUT_APP);
						    	Message msg = mHandler.obtainMessage();
						    	msg.what = Utils.RUN_CURRENT_SHORTCUT_APP;
						    	msg.obj = mCurrentShortcutApkInfo;
								mHandler.sendMessage(msg);
					    	}
					    	break;
						case KeyEvent.KEYCODE_DPAD_LEFT:  
			            	index --;
			            	if(index >= 0){
			            		mCurrentShortcutApkInfo = mShortcutAdapter.getItem(index);
			            	    mShortcutAdapter.setSelctItem(index);
			            	}
			            	break;
			            case KeyEvent.KEYCODE_DPAD_RIGHT:  
			            	index++;
			            	if(index < mShortcutAdapter.getCount()){
			            		mCurrentShortcutApkInfo = mShortcutAdapter.getItem(index);
			                    Log.e(LOGTAG, "index="+index+"; mCurrentShortcutApkInfo="+mCurrentShortcutApkInfo);
			                    mShortcutAdapter.setSelctItem(index);
			            	}
			            	break;
						case KeyEvent.KEYCODE_DPAD_UP:
							try{
								/*mHandler.removeMessages(CLOSE_SHORTCUT_POPUP);
								mHandler.sendEmptyMessage(CLOSE_SHORTCUT_POPUP);*/
								mHandler.sendEmptyMessage(Utils.HIDE_SHORTCUT_APP);
								dismiss();
							}catch(Exception err){
								err.printStackTrace();
								
							}
							break;
					}
					return true;
				}
				return true;
			}
		});
		
		gridMenu.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Log.e(LOGTAG, "onItemSelected..position="+position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		gridMenu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				//Log.e(LOGTAG, "onItem click...position="+position+"; packagename="+mShortcutAdapter.getItem(position).getPackagename());
				
			}
		});
	}
}
