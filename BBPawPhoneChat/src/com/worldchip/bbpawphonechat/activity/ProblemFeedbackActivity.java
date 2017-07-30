package com.worldchip.bbpawphonechat.activity;

import java.io.IOException;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import java.util.ArrayList;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.net.HttpUtils;
import com.worldchip.bbpawphonechat.net.PictureUploadUtils;
import com.worldchip.bbpawphonechat.net.PictureUploadUtils.HttpResponseCallBack;
import com.worldchip.bbpawphonechat.utils.CommonUtils;

public class ProblemFeedbackActivity extends Activity implements

OnClickListener, OnLongClickListener {

	private static final String TAG = "CHRIS";

	private TextView tv_number_word;
	private EditText et_content;
	private final int MAX_LENGTH = 500;
	private int Rest_Length = MAX_LENGTH;

	private RelativeLayout rl_picture_one, rl_picture_two, rl_picture_there;
	private ImageView iv_picture_one, iv_picture_two, iv_picture_there,
			iv_delete_one, iv_delete_two, iv_delete_there;
	private Animation shakeAnim;

	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;

	public static String USER_PIC_UPLOAD_REQ_URL;

	private List<String> imageList = new ArrayList<String>();
	private int typeId = -1;
	private String content;
	private String parent_unique, children_unique;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_problem_feedback);
		Intent intent = getIntent();
		typeId = intent.getIntExtra("typeId", -1);
		initEditText();
		initImageView();
		initCommit();
	}

	private ProgressDialog dialog;
	private Button commit_feedback;

	private ImageView iv_sentence;

	private void initCommit() {
		// TODO Auto-generated method stub

		commit_feedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!CommonUtils
						.isNetWorkConnected(ProblemFeedbackActivity.this)) {
					Toast.makeText(ProblemFeedbackActivity.this,
							getString(R.string.network_anomalies),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (et_content.getText().toString().isEmpty()) {
					Toast.makeText(ProblemFeedbackActivity.this,
							getString(R.string.please_write_question_first),
							Toast.LENGTH_LONG).show();
				} else {
					// final String upUrl = initContentUrl();
					dialog = ProgressDialog.show(ProblemFeedbackActivity.this,
							"", getString(R.string.question_commit));
					dialog.setCancelable(false);

					if (imageList.size() != 0) {
						for (int i = 0; i < imageList.size(); i++) {
							upLoadImage(imageList.get(i),
									"http://push.bbpaw.com.cn/interface/chatoperation.php?param=upload_image");

						}

					} else {
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									HttpUtils.getRequest(initContentUrl(),
											ProblemFeedbackActivity.this);
									mHandler.sendEmptyMessage(0x111);

								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						}).start();
					}

				}
			}
		});
	}

	private void initImageView() {
		// TODO Auto-generated method stub
		iv_picture_one = (ImageView) findViewById(R.id.iv_picture_one);
		iv_picture_two = (ImageView) findViewById(R.id.iv_picture_two);
		iv_picture_there = (ImageView) findViewById(R.id.iv_picture_there);

		rl_picture_one = (RelativeLayout) findViewById(R.id.rl_picture_one);
		rl_picture_two = (RelativeLayout) findViewById(R.id.rl_picture_two);
		rl_picture_there = (RelativeLayout) findViewById(R.id.rl_picture_there);

		rl_picture_one.setOnLongClickListener(this);
		rl_picture_two.setOnLongClickListener(this);
		rl_picture_there.setOnLongClickListener(this);

		iv_delete_one = (ImageView) findViewById(R.id.iv_delete_one);
		iv_delete_two = (ImageView) findViewById(R.id.iv_delete_two);
		iv_delete_there = (ImageView) findViewById(R.id.iv_delete_there);

		iv_delete_one.setOnClickListener(this);
		iv_delete_two.setOnClickListener(this);
		iv_delete_there.setOnClickListener(this);

		iv_sentence = (ImageView) findViewById(R.id.iv_problem_feedback_sentence);
		commit_feedback = (Button) findViewById(R.id.btn_commit_feedback);
		imageAdapter();
	}

	private void imageAdapter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(
				iv_sentence,
				new int[] { R.drawable.problem_feedback_sentence,
						R.drawable.problem_feedback_sentence_es,
						R.drawable.problem_feedback_sentence_en });
		MyApplication.getInstance().ImageAdapter(
				commit_feedback,
				new int[] { R.drawable.selector_submit_feedback,
						R.drawable.selector_submit_feedback_es,
						R.drawable.selector_submit_feedback_en });

	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_picture_one:

			iv_delete_one.setVisibility(View.VISIBLE);
			shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
			rl_picture_one.startAnimation(shakeAnim);

			break;
		case R.id.rl_picture_two:
			iv_delete_two.setVisibility(View.VISIBLE);
			shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
			rl_picture_two.startAnimation(shakeAnim);

			break;
		case R.id.rl_picture_there:
			iv_delete_there.setVisibility(View.VISIBLE);
			shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
			rl_picture_there.startAnimation(shakeAnim);
			break;

		default:
			break;
		}
		return false;

	}

	@Override
	public void onClick(View v) {
		int positon;
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_delete_one:
			rl_picture_one.getAnimation().cancel();
			iv_delete_one.setVisibility(View.INVISIBLE);
			iv_picture_one.setVisibility(View.INVISIBLE);
			oneIsLoad = false;

			positon = imageList.indexOf(uriOne);

			if (imageList.get(positon) != null) {
				imageList.remove(positon);
			}

			break;
		case R.id.iv_delete_two:
			rl_picture_two.getAnimation().cancel();
			iv_delete_two.setVisibility(View.INVISIBLE);
			iv_picture_two.setVisibility(View.INVISIBLE);
			twoIsLoad = false;

			positon = imageList.indexOf(uriTwo);

			if (imageList.get(positon) != null) {
				imageList.remove(positon);
			}

			break;
		case R.id.iv_delete_there:
			rl_picture_there.getAnimation().cancel();
			iv_delete_there.setVisibility(View.INVISIBLE);
			iv_picture_there.setVisibility(View.INVISIBLE);
			thereIsLoad = false;

			positon = imageList.indexOf(uriThere);

			if (imageList.get(positon) != null) {
				imageList.remove(positon);
			}

			break;

		default:
			break;
		}
	}

	private void initEditText() {
		// TODO Auto-generated method stub

		et_content = (EditText) findViewById(R.id.et_problem_feedback);
		tv_number_word = (TextView) findViewById(R.id.tv_problem_word_number);

		et_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (Rest_Length > 0) {
					Rest_Length = MAX_LENGTH - et_content.getText().length();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				tv_number_word
						.setText(getResources().getString(R.string.you_input)
								+ " "+Rest_Length+" "
								+ getResources().getString(R.string.word));

			}

			@Override
			public void afterTextChanged(Editable s) {
				tv_number_word
						.setText(getResources().getString(R.string.you_input)
								+ " "+Rest_Length+" "
								+ getResources().getString(R.string.word));

			}

		});

	}

	public void onBack(View v) {
		super.onBackPressed();
	}

	private String initContentUrl() {
		try {
			content = URLDecoder.decode(et_content.getText().toString(),
					"UTF-8");
			parent_unique = MyApplication.getInstance().getUserName();
			children_unique = MyComment.CONTROL_BABY_NAME;
			USER_PIC_UPLOAD_REQ_URL = "http://push.bbpaw.com.cn/interface/chatoperation.php?param=addquestion&"
					+ "typeId="
					+ typeId
					+ "&content="
					+ content
					+ "&parent_unique="
					+ parent_unique
					+ "&children_unique="
					+ children_unique + "&images=";
			Log.e(TAG, USER_PIC_UPLOAD_REQ_URL + "......content=....."
					+ content + "----children_unique=" + children_unique);
			return URLDecoder.decode(USER_PIC_UPLOAD_REQ_URL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 浠庡浘搴撹幏鍙栧浘鐗�
	 */
	public void selectPicFromLocal(View v) {

		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}

		startActivityForResult(intent, REQUEST_CODE_LOCAL);

	}

	/**
	 * onActivityResult
	 */
	private boolean oneIsLoad = false, twoIsLoad = false, thereIsLoad = false;
	private String uriOne, uriTwo, uriThere;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) { // 娓呯┖娑堟伅
			if (requestCode == REQUEST_CODE_LOCAL) { // 鍙戦�佹湰鍦板浘鐗�
				if (data != null) {
					Uri uri = data.getData();// 寰楀埌绯荤粺鐩稿唽鍥剧墖鐨剈ri
					String selectedImage = CommonUtils.getFilePathFromUri(uri);
					Bitmap bitmap = CommonUtils.getSimpleBitmipFromUri(selectedImage,iv_picture_one);
					Log.e(TAG, selectedImage);
					if (uri != null) {
						if (!oneIsLoad) {
							uriOne = selectedImage;
							imageList.add(uriOne);
							if (iv_picture_one.getVisibility() == View.INVISIBLE) {
								iv_picture_one.setVisibility(View.VISIBLE);
								iv_picture_one.setImageBitmap(bitmap);
							} else {
								iv_picture_one.setImageBitmap(bitmap);
							}
							oneIsLoad = true;
						} else if (!twoIsLoad) {
							uriTwo = selectedImage;
							imageList.add(uriTwo);

							if (iv_picture_two.getVisibility() == View.INVISIBLE) {
								iv_picture_two.setVisibility(View.VISIBLE);
								iv_picture_two.setImageBitmap(bitmap);

							} else {
								iv_picture_two.setImageBitmap(bitmap);

							}
							twoIsLoad = true;
						} else if (!thereIsLoad) {

							uriThere = selectedImage;
							imageList.add(uriThere);

							if (iv_picture_there.getVisibility() == View.INVISIBLE) {
								iv_picture_there.setVisibility(View.VISIBLE);
								iv_picture_there.setImageBitmap(bitmap);

							} else {
								iv_picture_there.setImageBitmap(bitmap);

							}
							thereIsLoad = true;
						} else {
							Toast.makeText(ProblemFeedbackActivity.this,
									"一次只能上传3张图片", Toast.LENGTH_LONG).show();
						}

					}
				}

			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x111: // 鍙彁浜ゆ枃瀛楁垚鍔�
				if (dialog.isShowing()) {
					dialog.dismiss();
					Toast.makeText(ProblemFeedbackActivity.this,
							R.string.thank_you_sugesst, Toast.LENGTH_LONG)
							.show();
					ProblemFeedbackActivity.this.finish();
				}
				break;
			case 0x222: // 鎻愪氦澶辫触
				if (dialog.isShowing()) {
					dialog.dismiss();
					Toast.makeText(ProblemFeedbackActivity.this,
							R.string.you_commit_sugesst_failed, Toast.LENGTH_LONG).show();
				}
				break;

			default:
				break;
			}

		};

	};
	private int upTimes = 0;
	private List<String> resultList = new ArrayList<String>();
	private String endStr = null;

	public void upLoadImage(String selectedImage, String url) {
		if (url != null) {
			PictureUploadUtils.doPost(selectedImage,
					new HttpResponseCallBack() {
						@Override
						public void onSuccess(String result, String httpTag) {
							// TODO Auto-generated method stub
							if (result != null) {
								System.out.println("upload-上传图片成功-onSuccess=="+ result);
								resultList.add(result);
								if (resultList.size() == imageList.size()) {
									for (int i = 0; i < resultList.size(); i++) {
										if (i == 0) {
											endStr = resultList.get(i);
										} else {
											endStr = endStr + ","
													+ resultList.get(i);
										}

									}
									commitQuestion(initContentUrl() + endStr);
								}
							}

						}

						@Override
						public void onStart(String httpTag) {

						}

						@Override
						public void onFinish(int result, String httpTag) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onFailure(IOException e, String httpTag) {
							System.out.println("upload-上传图片失败-onFailure=="+ httpTag+"------"+e.toString());
							mHandler.sendEmptyMessage(0x222);

						}
					}, "update", url);

		}

	}

	protected void commitQuestion(final String questionUrl) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String result = HttpUtils.getRequest(questionUrl,
							ProblemFeedbackActivity.this);
					if ("{\"resultCode\":200}".equals(result)) {
						mHandler.sendEmptyMessage(0x111);
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
