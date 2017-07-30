package com.worldchip.bbp.ect.installapk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.worldchip.bbp.ect.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint({ "ViewConstructor", "DefaultLocale" })
public class CopyItemView extends FrameLayout{
	
	private static final boolean DEBUG = false;
	
	protected  static final int COPY_FAILED = -1;	
	protected  static final int COPY_SUCCESS = -2;	
	private final static int UPPDATE_PRORGRESS = 1;
	protected static final String TAG = "--CopyItemView--";
	
	private CopyFile mCopyFile;    
    private int mCopyTotal; 
    private String mCopyInfo; 
    
    private ProgressBar mProgressBar;
    private TextView mTvFileName;    
    private TextView mTvTooltip; 
    
    private ICopyEnd mCopyEnd;
    
    private CopyThread mThread;
    
	@SuppressWarnings("unused")
	private Context mContext;
	@SuppressWarnings("unused")
	private Resources mRes;
    
	public CopyItemView(Context context, CopyFile copyFile) {
		super(context);
		mCopyEnd = (ICopyEnd)context;
		
		mContext = context;
		mRes = getResources();
		
		mCopyFile = copyFile;
		
		LayoutInflater.from(context).inflate(R.layout.copy_item_layout, this); 
		
		initView();
	}
	
	private void initView(){
		mProgressBar = (ProgressBar)findViewById(R.id.progessbar);
		mTvFileName =(TextView)findViewById(R.id.file_name);
		mTvTooltip = (TextView)findViewById(R.id.tool_tip);
	}
	
	public void start(){
		String fileName= mCopyFile.mFileName; 
		if(fileName.length() > 20){
			//ֻȡǰ20λ������ʾ��
			fileName = fileName.substring(0, 20)+"...";
		}
		mTvFileName.setText(fileName+mCopyFile.mFileExtend);
		
		//��ʾ�ٷֱ�
		mTvTooltip.setText(mCopyTotal+"%");
		mThread = new CopyThread();
		mThread.start();
	}
	
    private class CopyThread extends Thread{			
		@SuppressLint("SimpleDateFormat")
		@SuppressWarnings("resource")
		@Override
		public void run() {
			try {
		       int length = 2097152;
		       String fromFile;
		       String toFile;
		       
		       fromFile = mCopyFile.mFilePath;
	    	   toFile = Utils.VIDEO_TO_PATH +mCopyFile.mFileName;
	    	   File targetDir = new File( Utils.VIDEO_TO_PATH);
	           if(!targetDir.exists()){
	        	   targetDir.mkdirs();
	           }
	           
		       if(DEBUG)Log.e(TAG, "start copy...fromFile="+fromFile+"; toFile="+toFile);
			   FileInputStream in = new FileInputStream(fromFile);
			   FileOutputStream out = new FileOutputStream(toFile);
			   FileChannel inC = in.getChannel();
			   FileChannel outC = out.getChannel();
			   ByteBuffer b = null;
			   while (true) 
			   {
					if (inC.position() == inC.size()) 
					{
						inC.close();
						outC.close();
					}
					if ((inC.size() - inC.position()) < length) 
					{
						length = (int) (inC.size() - inC.position());
					} else
						length = 2097152;
					b = ByteBuffer.allocateDirect(length);
					
					//���ƽ��
					mCopyTotal = (int) (((inC.position() +length) * 100) / inC.size());
					mCopyInfo = "("+Utils.convertFileSize(inC.position() +length)
							+"/" +Utils.convertFileSize(inC.size())+")";
					mCopyHandler.sendEmptyMessage(UPPDATE_PRORGRESS);
					
					inC.read(b);
					b.flip();
					outC.write(b);
					outC.force(false);
		       }
			} catch (Exception e) {
				e.printStackTrace();
				mCopyHandler.sendEmptyMessage(COPY_FAILED);
			}
			
		}
    };
    
	@SuppressLint("HandlerLeak")
	private Handler mCopyHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPPDATE_PRORGRESS) {
				mProgressBar.setProgress(mCopyTotal);
				mTvTooltip.setText(mCopyTotal+"% "+mCopyInfo);
				if(mCopyTotal >= 100){
					Log.d(TAG, "COPY_END ... mCopyTotal="+mCopyTotal+"; mFileName="+mCopyFile.mFileName);
					mCopyEnd.copyEnd(CopyItemView.this);
				}
			}else if(msg.what == COPY_SUCCESS){
				mCopyEnd.copyEnd(CopyItemView.this);
			}
		}
	};
	
	public String getFileName() {
		return mCopyFile.mFileName;
	}
}