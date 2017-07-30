package com.worldchip.bbpawphonechat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.activity.ProblemFeedbackActivity;
import com.worldchip.bbpawphonechat.application.MyApplication;

public class ProblemFeedbackFragment extends Fragment implements
		OnClickListener {
	private ImageButton ib_hardware_facade, ib_health_eye, ib_study_content,
			ib_teach_baby, ib_parent_helper, ib_app_store, ib_other_suggest;
	private Intent intent;

	public ProblemFeedbackFragment() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_problem_feedback,
				container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		ib_hardware_facade = (ImageButton) view
				.findViewById(R.id.ib_hardware_facade);
		ib_health_eye = (ImageButton) view.findViewById(R.id.ib_health_eye);
		ib_study_content = (ImageButton) view
				.findViewById(R.id.ib_study_content);
		ib_teach_baby = (ImageButton) view.findViewById(R.id.ib_teach_baby);
		ib_parent_helper = (ImageButton) view
				.findViewById(R.id.ib_parent_helper);
		ib_other_suggest = (ImageButton) view
				.findViewById(R.id.ib_other_suggest);
		ib_app_store = (ImageButton) view.findViewById(R.id.ib_app_store);

		imageAdapter();

		ib_hardware_facade.setOnClickListener(this);
		ib_health_eye.setOnClickListener(this);
		ib_study_content.setOnClickListener(this);
		ib_teach_baby.setOnClickListener(this);
		ib_parent_helper.setOnClickListener(this);
		ib_other_suggest.setOnClickListener(this);
		ib_app_store.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), R.string.comson, Toast.LENGTH_LONG).show();

			}
		});

	}

	private void imageAdapter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(
				ib_hardware_facade,
				new int[] { R.drawable.hardware_facade,
						R.drawable.hardware_facade_es,
						R.drawable.hardware_facade_en });
		MyApplication.getInstance().ImageAdapter(
				ib_health_eye,
				new int[] { R.drawable.health_eye, R.drawable.health_eye_es,
						R.drawable.health_eye_en });
		MyApplication.getInstance().ImageAdapter(
				ib_study_content,
				new int[] { R.drawable.study_content,
						R.drawable.study_content_es,
						R.drawable.study_content_en });
		MyApplication.getInstance().ImageAdapter(
				ib_teach_baby,
				new int[] { R.drawable.teach_baby, R.drawable.teach_baby_es,
						R.drawable.teach_baby_en });
		MyApplication.getInstance().ImageAdapter(
				ib_parent_helper,
				new int[] { R.drawable.parent_helper,
						R.drawable.parent_helper_es,
						R.drawable.parent_helper_en });
		MyApplication.getInstance().ImageAdapter(
				ib_other_suggest,
				new int[] { R.drawable.other_suggest,
						R.drawable.other_suggest_es,
						R.drawable.other_suggest_en });
		MyApplication.getInstance().ImageAdapter(
				ib_app_store,
				new int[] { R.drawable.app_store, R.drawable.app_store_es,
						R.drawable.app_store_en });

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int typeId = -1;
		intent = new Intent(this.getActivity(), ProblemFeedbackActivity.class);

		switch (v.getId()) {
		case R.id.ib_hardware_facade:
			typeId = 1;
			break;
		case R.id.ib_health_eye:
			typeId = 2;

			break;
		case R.id.ib_study_content:
			typeId = 3;

			break;
		case R.id.ib_teach_baby:
			typeId = 4;

			break;
		case R.id.ib_parent_helper:
			typeId = 5;

			break;
		case R.id.ib_other_suggest:
			typeId = 7;

			break;
		

		default:
			break;
		}
		intent.putExtra("typeId", typeId);
		startActivity(intent);

	}

}
