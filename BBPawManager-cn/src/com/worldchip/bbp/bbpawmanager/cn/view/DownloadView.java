package com.worldchip.bbp.bbpawmanager.cn.view;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.activity.InformationActivity;
import com.worldchip.bbp.bbpawmanager.cn.model.Information;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.NetworkUtils;
import com.worldchip.bbp.bbpawmanager.cn.utils.ScaleAnimEffect;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;

public class DownloadView implements OnClickListener{

	private Context mContext;
	//private RoundProgress roundProgressBar;
	private ImageView closeButton;
	private ImageView installButton;
	private ImageView cancelButton;
	private ImageView startButton;
	private ImageView mRoundProgress;
	private TextView downloadProgress;
	private TextView mDownloadTitle;
	private Handler mCallback;
	private String downloadUrl;
	public static final int DOWNLOAD_CLOSE = 200;
	public static final int DOWNLOAD_CANCEL = 201;
	public static final int DOWNLOAD_INSTALL = 202;
	public static final int DOWNLOAD_START = 203;
	public static final int DOWNLOAD_PAUSE = 204;
	public static final int DOWNLOAD_CONTINUE = 205;
	public static final int CLOSE_DOWNLOAD_VIEW = 206;
	public DownloadState mDownloadState = DownloadState.STOP;
	public boolean isAlready = false;
	private RotateAnimation mRoundProgressAnim = null;
	private String mCompleteFile="";
	private GlobalAlertDialog mNoticeDialog = null;
	private Information mDownloadInfo = null;
	
	private ScaleAnimEffect mAnimEffect = new ScaleAnimEffect();
	private static float big_x = 1.08F;
	private static float big_y = 1.08F;
	
	public enum DownloadState
    {
		STOP, PAUSE, DOWNLOADING
    }
	
	public DownloadView(Context context, Handler callback,Information info) {
		this.mContext = context;
		this.mCallback = callback;
		this.mDownloadInfo = info;
		if (info != null) {
			this.downloadUrl = info.getDownloadUrl();
		}
	}
	
	public View creatDownloadView() {
		View downloadView = LayoutInflater.from(mContext).inflate(R.layout.app_update_layout, null);
		if (downloadView == null) {
			return null;
		}
		//roundProgressBar = (RoundProgress) downloadView.findViewById(R.id.progress);
		closeButton = (ImageView) downloadView.findViewById(R.id.dowmload_close_btn);
		installButton = (ImageView) downloadView.findViewById(R.id.install_btn);
		installButton.setVisibility(View.GONE);
		cancelButton = (ImageView) downloadView.findViewById(R.id.dowmload_cancel_btn);
		startButton = (ImageView) downloadView.findViewById(R.id.start_download_btn);
		mRoundProgress = (ImageView) downloadView.findViewById(R.id.round_progress);
		downloadProgress = (TextView) downloadView.findViewById(R.id.download_progress);
		mDownloadTitle = (TextView) downloadView.findViewById(R.id.download_title);
		mDownloadTitle.setSelected(true);
		closeButton.setOnClickListener(this);
		installButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		startButton.setOnClickListener(this);
		return downloadView;
	}

	/*public RoundProgress getRoundProgressBar() {
		return roundProgressBar;
	}

	public void setRoundProgressBar(RoundProgress roundProgressBar) {
		this.roundProgressBar = roundProgressBar;
	}*/

	public ImageView getCloseButton() {
		return closeButton;
	}

	public void setCloseButton(ImageView closeButton) {
		this.closeButton = closeButton;
	}

	public ImageView getInstallButton() {
		return installButton;
	}

	public void setInstallButton(ImageView installButton) {
		this.installButton = installButton;
	}

	public ImageView getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(ImageView cancelButton) {
		this.cancelButton = cancelButton;
	}

	
	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadTitle(String title) {
		if (mDownloadTitle != null) {
			mDownloadTitle.setText(title);
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		startAnimEffect(view);
		/*switch (view.getId()) {
		case R.id.dowmload_close_btn:
			if (mDownloadState == DownloadState.DOWNLOADING){
				showBackgroundDownloadDialog();
			} else {
				if (mCallback != null) {
					mCallback.sendEmptyMessage(DOWNLOAD_CLOSE);
				}
			}
			break;
		case R.id.install_btn:
			installAPK(mCompleteFile);
			if (mDownloadInfo != null) {
				Common.replyStateToServer(mDownloadInfo, Utils.REPLY_STATE_INSTALLED);
			}
			break;
		case R.id.dowmload_cancel_btn:
			if (mDownloadState == DownloadState.DOWNLOADING) {
				showCancelDownloadDialog();
			} else {
				mCallback.sendEmptyMessage(CLOSE_DOWNLOAD_VIEW);
			}
			break;

		case R.id.start_download_btn:
			if (mCallback != null) {
				if (mDownloadState == DownloadState.STOP
						|| mDownloadState == DownloadState.PAUSE) {
					mDownloadState = DownloadState.DOWNLOADING;
					startButton.setBackgroundResource(R.drawable.ic_pause_btn);
					startRoundProgressAnim();
					if (!isAlready) {
						mCallback.sendEmptyMessage(DOWNLOAD_START);
						isAlready = true;
					} else {
						mCallback.sendEmptyMessage(DOWNLOAD_CONTINUE);
					}
				} else {
					mDownloadState = DownloadState.PAUSE;
					startButton.setBackgroundResource(R.drawable.ic_start_btn);
					mCallback.sendEmptyMessage(DOWNLOAD_PAUSE);
					stopRoundProgressAnim();
				}
			}
			break;
		}*/
	}

	public TextView getDownloadProgress() {
		return downloadProgress;
	}

	public void setDownloadProgress(TextView downloadProgress) {
		this.downloadProgress = downloadProgress;
	}

	@SuppressLint("DefaultLocale")
	public void onDownloadComplete(String downloadFile) {
		mCompleteFile = downloadFile;
		if (startButton != null) {
			startButton.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(downloadFile) && 
				downloadFile.toLowerCase().endsWith(".apk")) {
			if (installButton != null) {
				installButton.setVisibility(View.VISIBLE);
			}
		}
		if(downloadProgress != null) {
			downloadProgress.setText("100%");
		}
		mDownloadState = DownloadState.STOP;
		isAlready = false;
		if (mDownloadInfo != null) {
			Common.replyStateToServer(mDownloadInfo, Utils.REPLY_STATE_DOWNLOADED);
		}
	}
	
	
	public void startRoundProgressAnim() {
		if (mRoundProgress != null) {
			if (mRoundProgressAnim == null) {
				mRoundProgressAnim = new RotateAnimation(0f, 720f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				mRoundProgressAnim.setInterpolator(new LinearInterpolator());
				mRoundProgressAnim.setDuration(1500);
				mRoundProgressAnim.setRepeatCount(Integer.MAX_VALUE);
				mRoundProgressAnim.setFillAfter(true);
			}
			stopRoundProgressAnim();
			mRoundProgress.startAnimation(mRoundProgressAnim);
		}
	}
	
	public void stopRoundProgressAnim() {
		if (mRoundProgressAnim != null) {
			if (mRoundProgressAnim.hasStarted()) {
				if (mRoundProgress != null) {
					mRoundProgress.clearAnimation();
				}
			}
		}
	}
	
	public void onUpdateStartBtn() {
		if (mDownloadState == DownloadState.STOP) {
			Log.e("lee", "dowmload view onUpdateStartBtn ----- ");
			if (startButton != null) {
				startButton.setBackgroundResource(R.drawable.ic_pause_btn);
			}
			mDownloadState = DownloadState.DOWNLOADING;
			isAlready = true;
			startRoundProgressAnim();
		}
	}
	
	public void onStopDownload() {
		if (startButton != null) {
			startButton.setBackgroundResource(R.drawable.ic_start_btn);
		}
		mDownloadState = DownloadState.STOP;
		isAlready = false;
		stopRoundProgressAnim();
	}
	
	public void cancelReset() {
		mDownloadState = DownloadState.PAUSE;
		isAlready = false;
		if (downloadProgress != null) {
			downloadProgress.setText("");
		}
		if (startButton != null) {
			startButton.setBackgroundResource(R.drawable.ic_start_btn);
		}
		stopRoundProgressAnim();
		Log.e("lee", "dowmload view reset ----- ");
	}
	
	private void installAPK(String filePath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);  
        intent.setDataAndType(Uri.fromFile(new File(filePath)),  
                "application/vnd.android.package-archive");  
        mContext.startActivity(intent);  
	}
	
	private void showBackgroundDownloadDialog() {
		String message = mContext.getResources().getString(R.string.global_background_download_msg);
		showDialog(message,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int whichButton) {//确定
				if (mCallback != null) {
					mCallback.sendEmptyMessage(DOWNLOAD_CLOSE);
				}
				dialog.dismiss();
			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {//取消
				// TODO Auto-generated method stub
			}
		});
		
	}
	
	private void showCancelDownloadDialog() {
		String fileName = NetworkUtils.getFileNameFromUrl(downloadUrl);
		String message = mContext.getResources().getString(R.string.download_cancel_text_format, fileName);
		showDialog(message,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int whichButton) {//确定
				cancelReset();
				if (mCallback != null) {
					mCallback.sendEmptyMessage(DOWNLOAD_CANCEL);
					isAlready = false;
				}
				dialog.dismiss();
			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {//取消
				// TODO Auto-generated method stub
			}
		});
	}
	
	
	
	private void showDialog(String message, DialogInterface.OnClickListener positiveButtonOnClickListener,
			DialogInterface.OnClickListener negativeButtonOnClickListener) {
		if (mNoticeDialog != null) {
			if (mNoticeDialog.isShowing()) {
				mNoticeDialog.dismiss();
			}
			mNoticeDialog = null;
		}
		mNoticeDialog = new GlobalAlertDialog.Builder(mContext)
		.setTitle(mContext.getResources().getString(R.string.global_title_text))
		.setMessage(message)
		.setCancelable(true)
		.setNegativeButton(mContext.getResources().getString(R.string.no_text), negativeButtonOnClickListener)
		.setPositiveButton(mContext.getResources().getString(R.string.yes_text), positiveButtonOnClickListener)
		.create(-1);
		mNoticeDialog.show();
	}
	
	public DownloadState getDownloadState() {
		return mDownloadState;
	}
	
private void startAnimEffect(final View view) {
		
		mAnimEffect.setAttributs(1.0F, big_x, 1.0F, big_y, 200);
		Animation anim = mAnimEffect.createAnimation();
		anim.setFillBefore(true);
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				switch (view.getId()) {
				case R.id.dowmload_close_btn:
					if (mDownloadState == DownloadState.DOWNLOADING){
						showBackgroundDownloadDialog();
					} else {
						if (mCallback != null) {
							mCallback.sendEmptyMessage(DOWNLOAD_CLOSE);
						}
					}
					break;
				case R.id.install_btn:
					installAPK(mCompleteFile);
					if (mDownloadInfo != null) {
						Common.replyStateToServer(mDownloadInfo, Utils.REPLY_STATE_INSTALLED);
					}
					break;
				case R.id.dowmload_cancel_btn:
					if (mDownloadState == DownloadState.DOWNLOADING) {
						showCancelDownloadDialog();
					} else {
						mCallback.sendEmptyMessage(CLOSE_DOWNLOAD_VIEW);
					}
					break;

				case R.id.start_download_btn:
					if (mCallback != null) {
						if (mDownloadState == DownloadState.STOP
								|| mDownloadState == DownloadState.PAUSE) {
							mDownloadState = DownloadState.DOWNLOADING;
							startButton.setBackgroundResource(R.drawable.ic_pause_btn);
							startRoundProgressAnim();
							if (!isAlready) {
								mCallback.sendEmptyMessage(DOWNLOAD_START);
								isAlready = true;
							} else {
								mCallback.sendEmptyMessage(DOWNLOAD_CONTINUE);
							}
						} else {
							mDownloadState = DownloadState.PAUSE;
							startButton.setBackgroundResource(R.drawable.ic_start_btn);
							mCallback.sendEmptyMessage(DOWNLOAD_PAUSE);
							stopRoundProgressAnim();
						}
					}
					break;
				}
			}
		});
		view.startAnimation(anim);
	}
	
}
