package com.worldchip.bbp.bbpawmanager.cn.view;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;
import com.worldchip.bbp.bbpawmanager.cn.view.SwitchButton.OnSwitchChangeListener;

@SuppressLint("NewApi")
public class EyecarePageFragment extends Fragment implements OnSwitchChangeListener{

	private SwitchButton mDistanceSensorSwicthButtom = null;
	private SwitchButton mReverseSensorSwicthButtom = null;
	private SwitchButton mLightSensorSwicthButtom = null;
	private static final String WAKE_PATH = "/sys/devices/platform/twi.1/i2c-1/1-003a/psledpeakcurrsetup";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.eyecare_page_layout, container,
				false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		mDistanceSensorSwicthButtom = (SwitchButton)view.findViewById(R.id.distance_sensor_swicth_btn);
		mReverseSensorSwicthButtom = (SwitchButton)view.findViewById(R.id.reverse_sensor_swicth_btn);
		mLightSensorSwicthButtom = (SwitchButton)view.findViewById(R.id.light_sensor_swicth_btn);
		mDistanceSensorSwicthButtom.setOnSwitchChangeListener(this);
		mReverseSensorSwicthButtom.setOnSwitchChangeListener(this);
		mLightSensorSwicthButtom.setOnSwitchChangeListener(this);
		initDatas();
	}

	
	private void initDatas() {
		String distanceSensor = Common.getEyecareSettingsPreferences(Utils.DISTANCE_SENSOR_PRE_KEY, "false");
		boolean distanceSensorSwitch = distanceSensor.equals("false") ? false : true;
		mDistanceSensorSwicthButtom.setOpened(distanceSensorSwitch);
		
		String reverseSensor = Common.getEyecareSettingsPreferences(Utils.REVERSE_SENSOR_PRE_KEY, "false");
		boolean reverseSensorSwitch = reverseSensor.equals("false") ? false : true;
		mReverseSensorSwicthButtom.setOpened(reverseSensorSwitch);
		
		String lightSensor = Common.getEyecareSettingsPreferences(Utils.LIGHT_SENSOR_PRE_KEY, "false");
		boolean lightSensorSwitch = lightSensor.equals("false") ? false : true;
		mLightSensorSwicthButtom.setOpened(lightSensorSwitch);
		//光感和距感同时关闭时减小亮度
		if (Common.getEyecareSettingsPreferences(Utils.LIGHT_SENSOR_PRE_KEY, "false").equals("false") && 
				Common.getEyecareSettingsPreferences(Utils.DISTANCE_SENSOR_PRE_KEY, "false").equals("false")) {
			write_node();
		}else {
			open_node();
		}
		
	}
	
	@Override
	public void onChanged(SwitchButton button, boolean state) {
		// TODO Auto-generated method stub
		if (button == null)
			return;
		
		switch (button.getId()) {
		case R.id.distance_sensor_swicth_btn:
			Common.saveEyecareSettingsPreferences(Utils.DISTANCE_SENSOR_PRE_KEY, String.valueOf(state));
			//光感和距感同时关闭时减小亮度
			if (Common.getEyecareSettingsPreferences(Utils.LIGHT_SENSOR_PRE_KEY, "false").equals("false") && 
					Common.getEyecareSettingsPreferences(Utils.DISTANCE_SENSOR_PRE_KEY, "false").equals("false")) {
				write_node();
			}else {
				open_node();
			}
			break;
		case R.id.reverse_sensor_swicth_btn:
			Common.saveEyecareSettingsPreferences(Utils.REVERSE_SENSOR_PRE_KEY, String.valueOf(state));
			break;
		case R.id.light_sensor_swicth_btn:
			Common.saveEyecareSettingsPreferences(Utils.LIGHT_SENSOR_PRE_KEY, String.valueOf(state));
			//光感和距感同时关闭时减小亮度
			if (Common.getEyecareSettingsPreferences(Utils.LIGHT_SENSOR_PRE_KEY, "false").equals("false") && 
					Common.getEyecareSettingsPreferences(Utils.DISTANCE_SENSOR_PRE_KEY, "false").equals("false")) {
				write_node();
			} else {
				open_node();
			}
			break;
		}
		getActivity().sendBroadcast(new Intent("com.worldchip.bbpaw.sensor.swicth.UPDATE"));
	}
	
	//增加亮度
	   public void open_node(){
	        try {
	          BufferedWriter bufWriter = new BufferedWriter(new FileWriter(WAKE_PATH));
	           bufWriter.write("500");
	           bufWriter.close();
	           Log.d("Wing","open_node set LTR507_PS_LED Current 50MA");
	        }catch(IOException e){
	            e.printStackTrace();
	                Log.e("Wing","can't write the" + WAKE_PATH);
	        }
	    }
	 //减少亮度
	    public void write_node(){
	    	BufferedWriter bufWriter = null;
	        try {
	          bufWriter = new BufferedWriter(new FileWriter(WAKE_PATH));
	          bufWriter.write("50");
	          Log.d("Wing","close_node set LTR507_PS_LED Current 5MA");
	        }catch(IOException e){
	            e.printStackTrace();
	                Log.e("Wing","can't write the" + WAKE_PATH);
	        } finally {
	        	if (bufWriter != null) {
	        		try {
						bufWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        	
	        }
	    }

}
