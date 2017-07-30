package com.worldchip.bbpawphonechat.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.entity.CommonProblemEntity;
import com.worldchip.bbpawphonechat.entity.CommonProblemEntity.ListQuestionEntity;

public class CommonProblemActivity extends Activity {
	private TextView tv_my_question, tv_BBpaw_response;
	private ImageView iv_user_one, iv_user_two, iv_user_there;
	private ImageView iv_BBpaw_one, iv_BBpaw_two, iv_BBpaw_there;
	private ListQuestionEntity entity;
	private List<String> user_imageUrl;
	private List<String> BBpaw_imageUrl;

	private ImageLoader mImageLoader;
	private DisplayImageOptions imageOptions;
	private ImageView iv_sentence;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_problem);

		iv_sentence = (ImageView) findViewById(R.id.iv_common_problem_sentence);
		MyApplication.getInstance().ImageAdapter(
				iv_sentence,
				new int[] { R.drawable.common_problem_sentence,
						R.drawable.common_problem_sentence_es,
						R.drawable.common_problem_sentence_en });

		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("Question");
		entity = (CommonProblemEntity.ListQuestionEntity) bundle
				.getSerializable("QuestionEntity");

		tv_my_question = (TextView) findViewById(R.id.tv_my_question);
		tv_BBpaw_response = (TextView) findViewById(R.id.tv_BBpaw_response);

		tv_my_question.setText(entity.getContent());

		tv_BBpaw_response.setText(entity.getResponse().getContent());

		iv_user_one = (ImageView) findViewById(R.id.iv_user_one);
		iv_user_two = (ImageView) findViewById(R.id.iv_user_two);
		iv_user_there = (ImageView) findViewById(R.id.iv_user_there);

		iv_BBpaw_one = (ImageView) findViewById(R.id.iv_bbpaw_one);
		iv_BBpaw_two = (ImageView) findViewById(R.id.iv_bbpaw_two);
		iv_BBpaw_there = (ImageView) findViewById(R.id.iv_bbpaw_there);

		initImgaeView();

	}

	private void initImgaeView() {

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

	public void onBack(View v) {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

}
