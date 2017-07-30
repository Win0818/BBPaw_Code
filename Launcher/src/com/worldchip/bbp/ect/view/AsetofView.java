package com.worldchip.bbp.ect.view;

import com.worldchip.bbp.ect.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

public class AsetofView extends PopupWindow implements OnClickListener,
		OnTouchListener, OnCheckedChangeListener {

	private Context mContext;
	private TextView mBaby_name, mBaby_sex, mBaby_brithday,mBoy_tv,mGirl_tv;
	private RelativeLayout mSecand_edit_relayout, mFirst_relayout;
	private ImageView mBabyTete,mBabyTete_edit,mBaby_edit;
	private RadioGroup mRaioGroup;
	private SharedPreferences mBabyinfo;
	private RadioButton mBoy_radiobtn, mGirl_radiobtn;
	private EditText mBaby_editname;
	private Button mSave;
	private View mParentView = null;
 
	private boolean mShowEdit;
	
	public AsetofView(Context context) {
		mContext = context;
		mShowEdit = false;
	}
	
	public void showEdit(boolean showEdit){
		mShowEdit = showEdit;
	}

	public void show() {
		if (mParentView != null) {
			showAtLocation(mParentView, Gravity.CENTER, 0, 0);
		}
	}

	public void initView(Context context) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.bb_edit_box_layout, null);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new ColorDrawable(0));
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		mBabyinfo=mContext.getSharedPreferences("babyinfo", 0);
		mBaby_edit = (ImageView) view.findViewById(R.id.bb_edit_btn);
		mBaby_name=(TextView)view.findViewById(R.id.baby_name);
		mBaby_sex = (TextView) view.findViewById(R.id.baby_sex);
		mFirst_relayout = (RelativeLayout) view
				.findViewById(R.id.bb_secand_relayout);
		mBoy_tv=(TextView)view.findViewById(R.id.baby_boy_tv);
		mGirl_tv=(TextView)view.findViewById(R.id.baby_girl_tv);
		mSecand_edit_relayout = (RelativeLayout) view
				.findViewById(R.id.baby_edit_secand_relayout);
		mRaioGroup = (RadioGroup) view
				.findViewById(R.id.baby_selector_sex_radiogroup);
		mSave = (Button) view.findViewById(R.id.baby_edit_save_btn);
		mBoy_radiobtn = (RadioButton) view.findViewById(R.id.baby_boy_radiobtn);
		mGirl_radiobtn = (RadioButton) view
				.findViewById(R.id.baby_girl_radiobtn);
		mBaby_editname=(EditText)view.findViewById(R.id.baby_edit_name);
		mBabyTete=(ImageView)view.findViewById(R.id.bb_touxiang);

		StringBuilder buffer = new StringBuilder();
		String targetDir = buffer.append(Environment.getDataDirectory())
				.append("/data/").append(mContext.getPackageName())
				.append("/imagedata/").toString();
		// ³õÊ¼»¯Í¼Æ¬
		Bitmap imageBitmap = BitmapFactory.decodeFile(targetDir
				+ "imagename.png");
		if (imageBitmap != null) {
			mBabyTete_edit.setImageBitmap(imageBitmap);
		}
		
		mBaby_name.setText(mBabyinfo.getString("babyname",""));
		mBaby_sex.setText(mBabyinfo.getString("babysex",""));
		mBaby_brithday.setText(mBabyinfo.getString("babysex",""));
		mBaby_editname.setText(mBabyinfo.getString("babyname",""));
		mRaioGroup.setOnCheckedChangeListener(this);
		mSave.setOnClickListener(this);
		mBaby_edit.setOnClickListener(this);
		mBabyTete_edit.setOnClickListener(this);
		
		if(mShowEdit){
			mFirst_relayout.setVisibility(View.GONE);
			mSecand_edit_relayout.setVisibility(View.VISIBLE);
			mSave.setVisibility(View.VISIBLE);
		}else{
			mFirst_relayout.setVisibility(View.VISIBLE);
			mSecand_edit_relayout.setVisibility(View.GONE);
			mSave.setVisibility(View.GONE);
		}
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bb_edit_btn:
			mSecand_edit_relayout.setVisibility(View.VISIBLE);
			mSave.setVisibility(View.VISIBLE);
			mFirst_relayout.setVisibility(View.GONE);
			break;
		case R.id.baby_edit_save_btn:
			String name=mBaby_editname.getText().toString().trim();
		    mBabyinfo.edit().putString("babyname", name).commit();
			if (this.isShowing()) {
				this.dismiss();
			}
			break;
	
		}
	}

	public void setParentView(View parentView) {
		mParentView = parentView;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {

		return false;
	}

	@Override
	public void onCheckedChanged(RadioGroup view, int id) {
		
      switch (view.getCheckedRadioButtonId()) {
	   case R.id.baby_boy_radiobtn:
		  mBabyinfo.edit().putString("babysex",mBoy_tv.getText().toString().trim());
		  Log.e("tag...selector..boy",mBabyinfo.getString("babysex", ""));
		   break;
	   case R.id.baby_girl_radiobtn:
		  mBabyinfo.edit().putString("babysex",mGirl_tv.getText().toString().trim());
		  Log.e("tag...selector..girl",mBabyinfo.getString("babysex", ""));
		  ;
		   break;
	default:
		break;
	}
	}


}
