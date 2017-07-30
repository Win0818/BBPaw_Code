package com.worldchip.bbp.ect.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.entity.MusicInfo;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicShareAdapter extends BaseAdapter{

	static class Holder 
	{
		 private TextView mMusicValue;
		 private ImageView selected;
	}

	private DataCallback dataCallback = null;
	private List<MusicInfo> dataList;
	private Activity act;
	private List<MusicInfo> shareList = new ArrayList<MusicInfo>();
	
	public static interface DataCallback
	{
		public void onListen(List<MusicInfo> Videos);
	}
	
	public void setDataCallback(DataCallback listener)
	{
		dataCallback = listener;
	}
	
	/**
	 * ��ȡ����Ҫ���������
	 */
	public List<MusicInfo> getShareMusicData()
	{
		return shareList;
	}
   
	public MusicShareAdapter(Activity act, List<MusicInfo> list) 
	{
		this.act = act;
		dataList = list;
	}
	
	@Override
	public int getCount() 
	{
		int count = 0;
		if (dataList != null) 
		{
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) 
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		final Holder holder;
		if (convertView == null) 
		{
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_music_list, null);
			holder.mMusicValue = (TextView) convertView.findViewById(R.id.music_value);
			holder.selected = (ImageView) convertView.findViewById(R.id.musicisselected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final MusicInfo music = dataList.get(position);
		String suffix = getExtensionName(music.getData());
		holder.mMusicValue.setText(music.getTitle()+"."+suffix);
		holder.mMusicValue.setSelected(true);
		if (music.isSelected)
		{
			holder.selected.setImageResource(R.drawable.setting_select_image);
		} else {
			holder.selected.setImageResource(-1);
		}
		
		holder.mMusicValue.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (music.isSelected)
				{
					music.isSelected = false;
					holder.selected.setImageResource(-1);
					shareList.remove(music);
				} else {
					music.isSelected = true;
					holder.selected.setImageResource(R.drawable.setting_select_image);
					shareList.add(music);
				}
				if(dataCallback != null)
				{
					dataCallback.onListen(shareList);
				}
			}
		});
		return convertView;
	} 
	
	/**
	 * ��ȡָ���ļ��ĺ�׺��
	 * @param filename
	 * @return
	 */
    private static String getExtensionName(String filename) 
    {    
        if ((filename != null) && (filename.length() > 0)) 
        {    
            int dot = filename.lastIndexOf('.');    
            if ((dot >-1) && (dot < (filename.length() - 1)))
            {    
                return filename.substring(dot + 1);    
            }    
        }    
        return filename;    
    }
    
    /** 
     * ����б��� 
     * @param item 
     */  
   public void addItem(MusicInfo music) 
   {  
	   dataList.add(music);  
   }
   
   /**
    * ɾ���б���
    */
   public void delItem(String data) 
   {  
	   Iterator<MusicInfo> ite = dataList.iterator();
	    while(ite.hasNext())
	    {
	    	MusicInfo musicInfo = ite.next();
		    if(musicInfo.getData().equals(data))
		    {
			    ite.remove();  
			    notifyDataSetChanged(); 
			    break;
		    }
	    }
   }
   
   /**
	 * ɾ������
	 */
	public void delItem(MusicInfo musicInfo)
	{
		dataList.remove(musicInfo);
		notifyDataSetChanged();
	}
	
	/**
	 * ���
	 */
	public void clearShareMusicList()
	{
		if (shareList != null)
		 {
			shareList.clear();
		 }
	}
}