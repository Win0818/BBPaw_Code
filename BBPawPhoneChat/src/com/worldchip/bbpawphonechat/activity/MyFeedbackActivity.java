package com.worldchip.bbpawphonechat.activity;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.entity.CommonProblemEntity;
import com.worldchip.bbpawphonechat.entity.CommonProblemEntity.ListQuestionEntity;
import com.worldchip.bbpawphonechat.net.HttpUtils;

@SuppressLint("HandlerLeak")
public class MyFeedbackActivity extends Activity implements OnClickListener {
	private TextView tv_my_question, tv_BBpaw_response, tv_my_commit_time,
			tv_bbpaw_reply_time;
	private ImageView iv_user_one, iv_user_two, iv_user_there;
	private ImageView iv_BBpaw_one, iv_BBpaw_two, iv_BBpaw_there;
	private LinearLayout ll_feedback_resolved, ll_feedback_unsolved;
	private ListQuestionEntity entity = null;
	private List<String> user_imageUrl;
	private ImageLoader mImageLoader;
	private DisplayImageOptions imageOptions;
	private List<String> BBpaw_imageUrl;
	private ImageView iv_my_feedback;
	private ImageView iv_resolved_sentence;
	private ImageView iv_unsolved_sentence;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_feedback);

		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("Question");
		entity = (CommonProblemEntity.ListQuestionEntity) bundle
				.getSerializable("QuestionEntity");

		iv_my_feedback = (ImageView) findViewById(R.id.iv_my_feedback);
		iv_resolved_sentence = (ImageView) findViewById(R.id.iv_resolved_sentence);
		iv_unsolved_sentence = (ImageView) findViewById(R.id.iv_unsolved_sentence);

		imageAdapter();

		initview();
	}

	private void imageAdapter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(
				iv_my_feedback,
				new int[] { R.drawable.my_feedback_sentence,
						R.drawable.my_feedback_sentence_es,
						R.drawable.my_feedback_sentence_en });
		MyApplication.getInstance().ImageAdapter(
				iv_resolved_sentence,
				new int[] { R.drawable.my_feedback_resolved_sentence,
						R.drawable.my_feedback_resolved_sentence_es,
						R.drawable.my_feedback_resolved_sentence_en });
		MyApplication.getInstance().ImageAdapter(
				iv_unsolved_sentence,
				new int[] { R.drawable.my_feedback_unsolved_sentence,
						R.drawable.my_feedback_unsolved_sentence_es,
						R.drawable.my_feedback_unsolved_sentence_en });

	}

	private void initview() {
		// TODO Auto-generated method stub
		tv_my_question = (TextView) findViewById(R.id.tv_my_question);
		tv_BBpaw_response = (TextView) findViewById(R.id.tv_BBpaw_response);

		tv_my_commit_time = (TextView) findViewById(R.id.tv_my_commit_time);
		tv_bbpaw_reply_time = (TextView) findViewById(R.id.tv_bbpaw_reply_time);

		iv_user_one = (ImageView) findViewById(R.id.iv_user_one);
		iv_user_two = (ImageView) findViewById(R.id.iv_user_two);
		iv_user_there = (ImageView) findViewById(R.id.iv_user_there);

		iv_BBpaw_one = (ImageView) findViewById(R.id.iv_bbpaw_one);
		iv_BBpaw_two = (ImageView) findViewById(R.id.iv_bbpaw_two);
		iv_BBpaw_there = (ImageView) findViewById(R.id.iv_bbpaw_there);

		ll_feedback_resolved = (LinearLayout) findViewById(R.id.ll_feedback_resolved);
		ll_feedback_unsolved = (LinearLayout) findViewById(R.id.ll_feedback_unsolved);

		if (entity.getIsSolved()) {
			ll_feedback_resolved.setPressed(true);
			ll_feedback_resolved.setClickable(false);
		} else {
			ll_feedback_resolved.setOnClickListener(this);
		}

		ll_feedback_unsolved.setOnClickListener(this);

		initImgaeView();
	}

	private void initImgaeView() {
		tv_my_commit_time.setText(entity.getCommit_time());
		tv_bbpaw_reply_time.setText(entity.getResponse().getResponse_time());
		tv_my_question.setText(entity.getContent());
		tv_BBpaw_response.setText(entity.getResponse().getContent());

		user_imageUrl = entity.getMy_url();
		mImageLoader = MyApplication.getInstance().getImageLoader();
		imageOptions = MyApplication.getInstance().getDisplayOptions();

		Log.e("user_imageUrl的长度",
				"------------------------" + user_imageUrl.size());
		switch (user_imageUrl.size()) {

		case 1:

			mImageLoader.displayImage(user_imageUrl.get(0), iv_user_one,
					imageOptions);

			break;
		case 2:

			mImageLoader.displayImage(user_imageUrl.get(0), iv_user_one,
					imageOptions);
			mImageLoader.displayImage(user_imageUrl.get(1), iv_user_two,
					imageOptions);

			break;
		case 3:

			mImageLoader.displayImage(user_imageUrl.get(0), iv_user_one,
					imageOptions);
			mImageLoader.displayImage(user_imageUrl.get(1), iv_user_two,
					imageOptions);
			mImageLoader.displayImage(user_imageUrl.get(2), iv_user_there,
					imageOptions);

			break;

		default:
			break;
		}

		BBpaw_imageUrl = entity.getResponse().getBBpaurl();
		switch (BBpaw_imageUrl.size()) {

		case 1:

			mImageLoader.displayImage(BBpaw_imageUrl.get(0), iv_BBpaw_one,
					imageOptions);

			break;
		case 2:

			mImageLoader.displayImage(BBpaw_imageUrl.get(0), iv_BBpaw_one,
					imageOptions);

			mImageLoader.displayImage(BBpaw_imageUrl.get(1), iv_BBpaw_two,
					imageOptions);

			break;
		case 3:

			mImageLoader.displayImage(BBpaw_imageUrl.get(0), iv_BBpaw_one,
					imageOptions);

			mImageLoader.displayImage(BBpaw_imageUrl.get(1), iv_BBpaw_two,
					imageOptions);

			mImageLoader.displayImage(BBpaw_imageUrl.get(2), iv_BBpaw_there,
					imageOptions);

			break;

		default:
			break;
		}

	}

	private ProgressDialog dialog;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				startActivity(new Intent(MyFeedbackActivity.this,
						FeedbackAndSuggest.class));
			}
		};
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_feedback_resolved:
			if (entity.getIsReplied()) {
				dialog = ProgressDialog.show(
						MyFeedbackActivity.this,
						"",
						getApplicationContext().getResources().getString(
								R.string.committing));

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							HttpUtils.getRequest(
									"http://push.bbpaw.com.cn/interface/chatoperation.php?param=state&question_id="
											+ entity.getQuestion_ID(),
									MyFeedbackActivity.this);
							mHandler.sendEmptyMessage(0x123);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			}

			break;
		case R.id.ll_feedback_unsolved:
			Intent intent = new Intent(MyFeedbackActivity.this,
					ProblemFeedbackActivity.class);
			startActivity(intent);

			break;

		default:
			break;
		}

	}

	public void onBack(View v) {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		entity = null;
	}

}
