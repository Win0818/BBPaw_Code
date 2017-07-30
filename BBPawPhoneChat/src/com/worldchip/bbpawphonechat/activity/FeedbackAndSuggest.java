package com.worldchip.bbpawphonechat.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.fragment.CommonProblemFragment;
import com.worldchip.bbpawphonechat.fragment.MyFeedbackFragment;
import com.worldchip.bbpawphonechat.fragment.ProblemFeedbackFragment;

public class FeedbackAndSuggest extends FragmentActivity {
	private RadioGroup rg;
	private RadioButton rb_problem_feedback, rb_common_problem, rb_my_feedback;
	private FrameLayout fram;
	private ProblemFeedbackFragment fragment_problem_feedback;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private CommonProblemFragment fragment_common_problem;
	private MyFeedbackFragment fragment_my_feedback;
	private ImageView iv_fas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback_and_suggest);

		initCurrentFragment();

		initView();

	}

	private void initCurrentFragment() {
		// TODO Auto-generated method stub
		manager = getSupportFragmentManager();
		fragment_problem_feedback = new ProblemFeedbackFragment();
		rb_problem_feedback = (RadioButton) findViewById(R.id.rb_problem_feedback);
		rb_problem_feedback.setChecked(true);
		manager.beginTransaction()
				.replace(R.id.fram_feedback_suggest, fragment_problem_feedback)
				.commit();

	}

	private void initView() {
		// TODO Auto-generated method stub
		// fram=(FrameLayout) findViewById(R.id.fram_feedback_suggest);
		iv_fas = (ImageView) findViewById(R.id.iv_fas_bg);
		rg = (RadioGroup) findViewById(R.id.rg_feedback_and_suggest);

		rb_common_problem = (RadioButton) findViewById(R.id.rb_common_problem);
		rb_my_feedback = (RadioButton) findViewById(R.id.rb_my_feedback);

		// transaction = manager.beginTransaction();

		fragment_common_problem = new CommonProblemFragment(this);
		fragment_my_feedback = new MyFeedbackFragment(this);

		imageAdapter();

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.rb_problem_feedback:
					manager.beginTransaction()
							.replace(R.id.fram_feedback_suggest,
									fragment_problem_feedback).commit();

					break;
				case R.id.rb_common_problem:
					/*
					 * transaction.add(R.id.fram_feedback_suggest,
					 * fragment_common_problem);
					 */
					manager.beginTransaction()
							.replace(R.id.fram_feedback_suggest,
									fragment_common_problem).commit();

					break;
				case R.id.rb_my_feedback:
					manager.beginTransaction()
							.replace(R.id.fram_feedback_suggest,
									fragment_my_feedback).commit();

					break;

				default:
					break;
				}
				// transaction.commit();
			}
		});
	}

	private void imageAdapter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(
				iv_fas,
				new int[] { R.drawable.feedback_suggest_sentence_bg,
						R.drawable.feedback_suggest_sentence_bg_es,
						R.drawable.feedback_suggest_sentence_bg_en });
		MyApplication.getInstance().ImageAdapter(
				rb_problem_feedback,
				new int[] { R.drawable.selector_problem_feedback,
						R.drawable.selector_problem_feedback_es,
						R.drawable.selector_problem_feedback_en });
		MyApplication.getInstance().ImageAdapter(
				rb_common_problem,
				new int[] { R.drawable.selector_common_problem,
						R.drawable.selector_common_problem_es,
						R.drawable.selector_common_problem_en });
		MyApplication.getInstance().ImageAdapter(
				rb_my_feedback,
				new int[] { R.drawable.selector_my_feedback,
						R.drawable.selector_my_feedback_es,
						R.drawable.selector_my_feedback_en });

	}

	public void onBack(View view) {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
