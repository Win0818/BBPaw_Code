package com.worldchip.bbp.ect.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.worldchip.bbp.ect.R;

public class FestivalActivity  extends Activity{
	private Button backButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 int flag = getIntent().getIntExtra("flag", -1);
		 switch (flag) {
         case 0:
        	 setContentView(R.layout.birthday_layout);
             break;
         case 1:
        	 setContentView(R.layout.yuandan);
             break;
         case 2:
        	 setContentView(R.layout.childrenday);
             break;
         case 3:
             setContentView(R.layout.lunar_new_year);
             break;
         case 4:
             setContentView(R.layout.duanwu_layout);
             break;
		 } 
		 backButton = (Button) findViewById(R.id.back_button);
		 backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
