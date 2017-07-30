/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.worldchip.bbpawphonechat.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.utils.Configure;

@SuppressLint("NewApi")
public class ExpressionAdapter extends ArrayAdapter<String>{

	private Context mContext;
	private int width;
	private int height;
	
	
	public ExpressionAdapter(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
		this.mContext = context;
		setImageHeightAndWidth();
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = View.inflate(getContext(), R.layout.row_expression, null);
		}
		ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_expression);
		imageView.setMaxHeight(20);
		LayoutParams  params  = new LinearLayout.LayoutParams(new LayoutParams(width, height));
		imageView.setLayoutParams(params);
		
		
		String filename = getItem(position);
		int resId = getContext().getResources().getIdentifier(filename, "drawable", getContext().getPackageName());
		//imageView.setBackgroundResource(resId);
		imageView.setImageResource(resId);
		System.out.println("---图片控件的高度---"+imageView.getHeight()+"---图片控件的宽度---"+imageView.getWidth());
		return convertView;
	}
	
	
	private void setImageHeightAndWidth(){
		 width = Configure.getScreenWidth(mContext)/6;
		 height = width;
		 System.out.println("--width--"+width+"--height--"+height);
	}
	
	
	
}
