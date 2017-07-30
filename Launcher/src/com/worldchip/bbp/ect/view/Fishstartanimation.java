package com.worldchip.bbp.ect.view;

import com.worldchip.bbp.ect.R;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class Fishstartanimation {
	public static final int BASE_DURATION = 50;//ˮ����ʾʱ��
	public static final int START_NEXT_ANIMATION_OFFSET = 3000;//��һ��С���ƶ��ӳ�ʱ��
	public static final int BACK_ANIM_DURATION = 1000;//С�㷵�ض���ʱ��
	public static final int START_ANIM_DURATION = 1000;//С���ƶ�ʱ��
	private static AnimationDrawable mFishJumpDownAnim = null;
    private boolean isrunning=false;
	public void startAnimationFish(final ImageView imageview,final ViewGroup viewgroup,Long StartOffset){
		imageview.setImageResource(R.drawable.alarm_up);
		TranslateAnimation ta=new TranslateAnimation(0, 0, 0,30);
		ta.setStartOffset(StartOffset);
		ta.setFillAfter(true);
		ta.setDuration(START_ANIM_DURATION);
		ta.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation start) {
				isrunning=true;
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				startTuwater(imageview,viewgroup);
			}
		});
		imageview.startAnimation(ta);
	}
	private void startTuwater(final ImageView imageview,final ViewGroup viewgroup) {
		  if(viewgroup==null){
			  return;
		  }
		  int childcount=viewgroup.getChildCount();
		  AnimationSet animationset=new AnimationSet(false);
		  for (int i = 0; i <childcount; i++) {
			final View view=viewgroup.getChildAt(i);
			    view.setVisibility(View.VISIBLE);
			    if(view!=null){
			    	int startoffset=0;
			    	int durition=0;
			    	int repeatcount=0;
			    	switch (view.getId()) {
					case R.id.water_01:
						durition=BASE_DURATION;
						break;
					case R.id.water_02:
//						startoffset=
						durition=BASE_DURATION;
						break;
                   
					default:
						break;
					}
			    }
		}
		  
	}
}
