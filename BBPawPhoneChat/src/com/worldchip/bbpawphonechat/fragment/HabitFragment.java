package com.worldchip.bbpawphonechat.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.adapter.HabitCategoryAdapter;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MyJsonParserUtil;
import com.worldchip.bbpawphonechat.comments.MySharePreData;
import com.worldchip.bbpawphonechat.db.HabitDao;
import com.worldchip.bbpawphonechat.db.UserDao;
import com.worldchip.bbpawphonechat.dialog.HabitAddLockDialog;
import com.worldchip.bbpawphonechat.dialog.MyAlertDialog;
import com.worldchip.bbpawphonechat.dialog.SelectControlBabyDialog;
import com.worldchip.bbpawphonechat.entity.HabitCategory;
import com.worldchip.bbpawphonechat.entity.User;
import com.worldchip.bbpawphonechat.net.HttpUtils;
import com.worldchip.bbpawphonechat.utils.CommonUtils;

public class HabitFragment extends Fragment implements OnItemClickListener {
	private static final String TAG = "CHRIS";
	private Context mContext;
	private GridView mGvCategory;
	private List<HabitCategory> mhabitcCategorList;
	private HabitCategoryAdapter adapter;
	private ImageView mIvSelectHead;
	private TextView mSelectBabyNameTv;
	private TextView mTopTv;
	// private TextView mTvSelect;
	private LinearLayout mControlBabyInfoLayout;

	private boolean mSelectContactFlag;

	private HabitAddLockDialog mAddLockDialog;
	private HabitDao habitDao;
	private UserDao userDao;

	private SelectControlBabyDialog mSelectControlBabyDialog;

	private User mControlContact = null;
	private String mControlBabyUserName = "";
	private String mContrlBabyNick = "";
	private String mControlHeadUrl = "";

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyComment.SEND_CODE_OPEN_BBPAW:
				mAddLockDialog.dismiss();
				mAddLockDialog = null;
				MySendCodeTask codeTask = new MySendCodeTask();
				codeTask.execute();
				break;
			case MyComment.SEND_CODE_LOCK_BBPAW:
				MySendCodeTask codeTask2 = new MySendCodeTask();
				codeTask2.execute();
				break;
			case MyComment.SELECTED_CONTROL_BABY:
				mControlContact = (User) msg.obj;
				showControlContact(mControlContact);
				MySharePreData.SetData(mContext, MyComment.CHAT_SP_NAME,
						"control_to", mControlBabyUserName);
				mSelectControlBabyDialog.dismiss();
				mSelectControlBabyDialog = null;
				mContext.sendBroadcast(new Intent(
						MyComment.SEND_CONTROL_CHANGE_BROADCAST));
				break;
			default:
				break;
			}
		}
	};

	private HabitCategory mCurrentSelectHabit;

	private int[] mCategoryImageArrs = { R.drawable.habit_wash_hand_btn,
			R.drawable.habit_eat_btn, R.drawable.habit_drink_btn,
			R.drawable.habit_sport_btn, R.drawable.habit_brush_btn,
			R.drawable.habit_take_shower_btn, R.drawable.habit_do_homework_btn,
			R.drawable.habit_close_screen_btn, 
		   
	  };

	private int[] mCategoryTextArrs = { R.drawable.habit_wash_hand_text,
			R.drawable.habit_eat_text, R.drawable.habit_drink_text,
			R.drawable.habit_sport_text, R.drawable.habit_brush_text,
			R.drawable.habit_take_shower_text,
			R.drawable.habit_do_homework_text,
			R.drawable.habit_close_screen_text, 
	     	
	};
	
	private int[] mCategoryTextArrs_en = { R.drawable.habit_wash_hand_text_en,
			R.drawable.habit_eat_text_en, R.drawable.habit_drink_text_en,
			R.drawable.habit_sport_text_en, R.drawable.habit_brush_text_en,
			R.drawable.habit_take_shower_text_en,
			R.drawable.habit_do_homework_text_en,
			R.drawable.habit_close_screen_text_en,
	       	
	};

	private int[] codeIds = { 10001, 10002, 10003, 10004, 10005, 10006, 10007,
			10008};

	private int[] timeLong = { 15, 60, 15, 15, 15, 30, 15, 15};

	public HabitFragment(Context context) {
		this.mContext = context;
		mhabitcCategorList = new ArrayList<HabitCategory>();
		habitDao = new HabitDao(mContext);
		IntentFilter filter = new IntentFilter(
				MyComment.SEND_CONTACT_INFO_BROADCAST);
		mContext.registerReceiver(mContactInfoReceiver, filter);
	}

	public HabitFragment(){}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String strTimes = getResources().getString(R.string.habit_item_minutes);
	    habitDao = new HabitDao(mContext);
		List<HabitCategory> categories = habitDao.getHabitList();
		int[] temp = new int[8];
		switch (MyApplication.getInstance().system_local_language) {
		case 0:
			temp=mCategoryTextArrs;
			break;
		case 1:
			temp=mCategoryTextArrs_en;
			break;
		case 2:
			temp=mCategoryTextArrs_en;
			break;

		default:
			break;
		}
		for (int i = 0; i < 8; i++) {
			if(mhabitcCategorList == null){
				mhabitcCategorList = new ArrayList<HabitCategory>();
			}
			mhabitcCategorList.add(new HabitCategory(timeLong[i] + "",
					categories.get(i).getIsLockInt(), mCategoryImageArrs[i],
					temp[i], categories.get(i).getHuanxinId()));
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "HabitFragment-----onCreateView");
		View view = inflater.inflate(R.layout.fragment_habit_tab_content,
				container, false);
		ImageView iv_head = (ImageView) view
				.findViewById(R.id.iv_top_bar_my_head);
		MyApplication
				.getInstance()
				.getImageLoader()
				.displayImage(MyApplication.getInstance().getHeadImageUrl(),
						iv_head,
						MyApplication.getInstance().getDisplayOptionsHead());

		mTopTv = (TextView) view.findViewById(R.id.tv_top_bar_title);
		// mTvSelect = (TextView)
		// view.findViewById(R.id.tv_note_select_contact);
		mControlBabyInfoLayout = (LinearLayout) view
				.findViewById(R.id.ll_control_baby);
		mGvCategory = (GridView) view.findViewById(R.id.gv_habit_category);

		mIvSelectHead = (ImageView) view
				.findViewById(R.id.iv_select_control_baby);
		mSelectBabyNameTv = (TextView) view
				.findViewById(R.id.tv_myControl_baby);
		mTopTv.setVisibility(View.INVISIBLE);

		userDao = new UserDao(mContext);

		adapter = new HabitCategoryAdapter(mhabitcCategorList, mContext);
		mGvCategory.setAdapter(adapter);
		mGvCategory.setOnItemClickListener(this);

		initSelectContolInfo();

		mIvSelectHead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mSelectContactFlag) {
					mSelectControlBabyDialog = new SelectControlBabyDialog(
							handler, getActivity());
					mSelectControlBabyDialog.show();
				}
			}
		});
		return view;
	}

	private void initSelectContolInfo() {
		mControlBabyUserName = MySharePreData.GetData(mContext,
				MyComment.CHAT_SP_NAME, "control_to");
		mSelectContactFlag = MySharePreData.GetBooleanData(mContext,
				MyComment.CHAT_SP_NAME, "show_select");
		if (mControlBabyUserName != null && !mControlBabyUserName.equals("")) {
			mControlBabyInfoLayout.setVisibility(View.VISIBLE);
			mControlContact = userDao.getOneContact(mControlBabyUserName);
			showControlContact(mControlContact);
		} else {
			mSelectBabyNameTv.setText(getString(R.string.please_add_contact));
		}
	}

	private void showControlContact(User controlUser) {
		if (controlUser != null) {
			if(controlUser.getRemark_name() != null  && 
					!controlUser.getRemark_name().equals("")){
					mContrlBabyNick = controlUser.getRemark_name();
			}else{
				mContrlBabyNick = controlUser.getNick();
			}
			mControlBabyUserName = controlUser.getUsername();
			MyComment.CONTROL_BABY_NAME = mControlBabyUserName;
			if (!mContrlBabyNick.equals("")) {
				mSelectBabyNameTv.setText(mContrlBabyNick);
			} else {
				mSelectBabyNameTv.setText(mControlBabyUserName);
			}
			mControlHeadUrl = controlUser.getHeadurl();
			if (mControlHeadUrl != null && !mControlHeadUrl.equals("")) {
				MyApplication
						.getInstance()
						.getImageLoader()
						.displayImage(
								mControlHeadUrl,
								mIvSelectHead,
								MyApplication.getInstance()
										.getDisplayOptionsHead());
			} else {
				mIvSelectHead.setImageResource(R.drawable.setting_head_default);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		//initSelectContolInfo();
	}

	@Override
	public void onDestroy() {
		mContext.unregisterReceiver(mContactInfoReceiver);
		Log.d(TAG, "HabitFragment-----onDestroy");
		super.onDestroy();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		if (CommonUtils.isNetWorkConnected(mContext)) {
			mCurrentSelectHabit = (HabitCategory) adapter.getItem(position);
			if (mCurrentSelectHabit != null) {
				if (!mControlBabyUserName.equals("")) {
					if (mCurrentSelectHabit.getIsLockInt() == 1) {
						mAddLockDialog = new HabitAddLockDialog(handler,
								mContext);
						mAddLockDialog.show();
					} else {
						String message = null;
						switch (mCurrentSelectHabit.getHuanxinId()) {
						case 10001:
							message = getString(R.string.is_time_wash_hands);
							break;
						case 10002:
							message = getString(R.string.is_time_eatting);
							break;
						case 10003:
							message = getString(R.string.is_time_drinking);
							break;
						case 10004:
							message = getString(R.string.is_time_sporting);
							break;
						case 10005:
							message = getString(R.string.is_time_tooth_brush);
							break;
						case 10006:
							message = getString(R.string.is_time_take_shower);
							break;
						case 10007:
							message = getString(R.string.is_time_do_housework);
							break;
						case 10008:
							message = getString(R.string.is_time_lock_screen);
							break;
						case 10009:
							message = getString(R.string.close_paid);
							break;
						default:
							break;
						}
						alertDialogLockPaid(
								getString(R.string.alert_dialog_lock_paid),
								message, MyComment.SEND_CODE_LOCK_BBPAW);
					}
				}
			}
		} else {
			Toast.makeText(mContext, getString(R.string.network_anomalies),
					Toast.LENGTH_LONG).show();
		}
	}

	private void alertDialogLockPaid(String title, String message,
			final int codeCategory) {
		final MyAlertDialog mAd = new MyAlertDialog(getActivity());
		mAd.setTitle(title);
		mAd.setMessage(message);
		mAd.setPositiveButton(getString(R.string.ok), new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAd.dismiss();
				Toast.makeText(mContext, getString(R.string.ok),
						Toast.LENGTH_LONG).show();
				handler.removeMessages(codeCategory);
				handler.sendEmptyMessage(codeCategory);
				Log.e(TAG, "-----发送指令锁住平板---onClick--");
			}
		});
		mAd.setNegativeButton(getString(R.string.cancel),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mAd.dismiss();
						Toast.makeText(mContext, getString(R.string.cancel),
								Toast.LENGTH_LONG).show();
					}
				});
	}

	class MySendCodeTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... arg0) {
			String type = mCurrentSelectHabit.getHuanxinId() + "";
			String time_long = mCurrentSelectHabit.getTime();
			String push_addr = mControlBabyUserName;
			String username = MyApplication.getInstance().getUserName();
			String status = null;
			if (mCurrentSelectHabit.getIsLockInt() == 0) {
				status = "true";
			} else {
				status = "false";
			}
			String url = MyComment.SEND_CODE_URL + "&type=" + type
					+ "&time_long=" + time_long + "&push_addr=" + push_addr
					+ "&status=" + status;
			try {
				String result = HttpUtils.getRequest(url, getActivity());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return url;
		}

		@Override
		protected void onPostExecute(String result) {
			if (mCurrentSelectHabit.getIsLockInt() == 0) {
				mCurrentSelectHabit.setIsLockInt(1);
				habitDao.updataHabitList(mCurrentSelectHabit.getHuanxinId(), 1);
				adapter.notifyDataSetChanged();
			} else {
				mCurrentSelectHabit.setIsLockInt(0);
				habitDao.updataHabitList(mCurrentSelectHabit.getHuanxinId(), 0);
				adapter.notifyDataSetChanged();
			}
		}
	}

	private BroadcastReceiver mContactInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			initSelectContolInfo();
		}
	};

}
