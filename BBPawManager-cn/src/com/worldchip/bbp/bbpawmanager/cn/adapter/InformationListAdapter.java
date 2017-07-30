package com.worldchip.bbp.bbpawmanager.cn.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.image.utils.ImageLoader;
import com.worldchip.bbp.bbpawmanager.cn.image.utils.ImageLoader.Type;
import com.worldchip.bbp.bbpawmanager.cn.model.Information;

public class InformationListAdapter extends BaseAdapter implements Filterable {

	private LayoutInflater mInflater;
	private List<Information> mOriginalValues;// 全部消息
	private List<Information> mFilterDataList;// 消息类型过滤
	private Context mContext;
	private InformFilter mInformFilter = null;
	private SearchFilter mSearchFilter = null;
	private ImageLoader mImageLoader;
	private int mMenuState = Information.MENU_ALL;
	private String mFilterMsgType = String.valueOf(Information.ALL);
	private boolean isDeleteMode = false;
	public HashMap<Integer, Boolean> ischeck;
	private List<Information> mSelectDatas = new ArrayList<Information>();
	Resources mResources;
	private RelativeLayout mInformationLayout;

	private boolean mCompareID = true;

	public InformationListAdapter(Context context, List<Information> list) {
		mResources = context.getResources();
		mInflater = LayoutInflater.from(context);
		mFilterDataList = list;
		mOriginalValues = list;
		mContext = context;
		mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
		ischeck = new HashMap<Integer, Boolean>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				ischeck.put(i, false);
			}
		}
	}

	public boolean isDeleteMode() {
		return isDeleteMode;
	}

	public void setDeleteMode(boolean isDeleteMode) {
		this.isDeleteMode = isDeleteMode;
		// clearSelectDatas();
	}

	public HashMap<Integer, Boolean> getSelect() {
		return ischeck;
	}

	public void clearSelectDatas() {
		if (mSelectDatas != null && !mSelectDatas.isEmpty()) {
			mSelectDatas.clear();
		}
	}

	public List<Information> getSelectDatas() {
		return mSelectDatas;
	}

	public void removeItem(Information info) {
		if (mOriginalValues != null) {
			mOriginalValues.remove(info);
		}
		if (mFilterDataList != null) {
			mFilterDataList.remove(info);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		Information info = getItem(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.information_list_item,
					null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title_tv);
			holder.content = (TextView) convertView
					.findViewById(R.id.content_tv);
			holder.read = (TextView) convertView.findViewById(R.id.read_tv);
			holder.readLL = (View) convertView.findViewById(R.id.read_ll);
			holder.date = (TextView) convertView.findViewById(R.id.date_tv);
			holder.delete = (CheckBox) convertView
					.findViewById(R.id.delete_select);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		/*
		 * holder.icon.setTag(info); Information infoicon =
		 * (Information)holder.icon.getTag(); if (infoicon.getIcon()!=null) { if
		 * (infoicon.getIcon().contains("ic_")){
		 * holder.icon.setImageResource(mResources
		 * .getIdentifier(info.getIcon().toString
		 * (),"drawable",mContext.getPackageName())); }else{
		 * mImageLoader.loadImage(info.getIcon(), holder.icon, true, true); }
		 * }else { mImageLoader.loadImage(info.getIcon(), holder.icon, true,
		 * true); }
		 */
		if (info != null) {
			mImageLoader.loadImage(info.getIcon(), holder.icon, true, true);
			switch (info.getMsgId()) {
			case 1:
				holder.title.setText(R.string.default_information01_title);
				holder.content
						.setText(R.string.default_information01_description);
				break;
			case 2:
				holder.title.setText(R.string.default_information02_title);
				holder.content
						.setText(R.string.default_information02_description);
				break;
			case 3:
				holder.title.setText(R.string.default_information03_title);
				holder.content
						.setText(R.string.default_information03_description);
				break;
			case 4:
				holder.title.setText(R.string.default_information04_title);
				holder.content
						.setText(R.string.default_information04_description);
				break;
			case 5:
				holder.title.setText(R.string.default_information05_title);
				holder.content
						.setText(R.string.default_information05_description);
				break;
			default:
				holder.title.setText(info.getTitle());
				holder.content.setText(info.getDescription());
				break;
			}
			if (!info.isRead()) {
				holder.read.setText(mContext.getResources().getString(
						R.string.information_unread_text));
				holder.readLL.setBackgroundResource(R.drawable.ic_read_bg);
			} else {
				holder.read.setText("");
				holder.readLL.setBackgroundDrawable(null);
			}
			if (mMenuState == Information.MENU_FAVORITES) {
				holder.read.setText("");
				holder.readLL
						.setBackgroundResource(R.drawable.inform_favorites);
			}
			holder.delete.setTag(info);
			holder.date.setText(getFormatDate(info.getDate()));
			if (isDeleteMode()) {
				if (mSelectDatas != null && mSelectDatas.contains(info)) {
					holder.delete.setChecked(true);
				} else {
					holder.delete.setChecked(false);
				}
				holder.delete.setVisibility(View.VISIBLE);
				holder.delete.setFocusable(true);
				holder.delete
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(CompoundButton arg0,
									boolean checked) {
								// ischeck.put(Integer.parseInt(arg0.getTag().toString()),
								// arg1);
								Information info = (Information) arg0.getTag();
								if (info != null && mSelectDatas != null) {
									if (checked) {
										if (!mSelectDatas.contains(info)) {
											mSelectDatas.add(info);
										}
									} else {
										if (mSelectDatas.contains(info)) {
											mSelectDatas.remove(info);
										}
									}
								}
							}
						});
			} else {
				// holder.delete.setChecked(false);
				holder.delete.setVisibility(View.GONE);
				holder.delete.setFocusable(false);
			}
		}
		return convertView;
	}

	@SuppressLint("SimpleDateFormat")
	public String getFormatDate(String timestamp) {
		long currentTime = System.currentTimeMillis();
		String finalDatePattern = "yyyy-MM-dd";
		Date currentDate = new Date(currentTime);
		Date date = new Date(Long.parseLong(timestamp));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		DateInfo currDateInfo = new DateInfo();
		currDateInfo.year = calendar.get(Calendar.YEAR);
		currDateInfo.month = calendar.get(Calendar.MONTH);
		currDateInfo.day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.setTime(date);
		DateInfo dateInfo = new DateInfo();
		dateInfo.year = calendar.get(Calendar.YEAR);
		dateInfo.month = calendar.get(Calendar.MONTH);
		dateInfo.day = calendar.get(Calendar.DAY_OF_MONTH);
		if (currDateInfo.year == dateInfo.year
				&& currDateInfo.month == dateInfo.month
				&& currDateInfo.day == dateInfo.day) {
			finalDatePattern = "HH:mm";
		} else if (currDateInfo.year == dateInfo.year
				&& currDateInfo.month == dateInfo.month
				&& ((currDateInfo.day - dateInfo.day) == 1)) {
			return mContext.getResources().getString(R.string.yesterday_text);
		} else if (currDateInfo.year == dateInfo.year) {
			finalDatePattern = "MM-dd";
		}
		SimpleDateFormat dateSF = new SimpleDateFormat(finalDatePattern);
		return dateSF.format(date);
	}

	public class DateInfo {
		private int year;
		private int month;
		private int day;
	}

	public static class ViewHolder {
		ImageView icon;
		TextView title;
		TextView content;
		TextView read;
		View readLL;
		TextView date;
		CheckBox delete;
	}

	@Override
	public int getCount() {
		if (mFilterDataList != null) {
			return mFilterDataList.size();
		}
		return 0;
	}

	@Override
	public Information getItem(int position) {
		if (mFilterDataList != null) {
			return mFilterDataList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<Information> getDataList() {
		return mOriginalValues;
	}

	public void setDataList(List<Information> mDataList) {
		this.mOriginalValues = mDataList;
	}

	public List<Information> getFilterDataList() {
		return mFilterDataList;
	}

	public void setmFilterDataList(List<Information> mFilterDataList) {
		this.mFilterDataList = mFilterDataList;
	}

	public void notifyReadOriginalData(Information info) {
		if (mOriginalValues != null) {
			for (Information inform : mOriginalValues) {
				if (inform.equals(info)) {
					inform.setRead(true);
					break;
				}
			}
			getFilter().filter(mFilterMsgType);
		}
	}

	public void notifyFavoritesOriginalData(Information info) {
		if (mOriginalValues != null) {
			for (Information inform : mOriginalValues) {
				if (inform.equals(info)) {
					inform.setRead(true);
					break;
				}
			}
			getFilter().filter(mFilterMsgType);
		}
	}

	@Override
	public Filter getFilter() {
		if (mInformFilter == null) {
			mInformFilter = new InformFilter();
		}
		return mInformFilter;
	}

	private class InformFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			if (mOriginalValues == null || mOriginalValues.size() == 0) {
				return null;
			}
			/*
			 * Information infor = mOriginalValues.get(6);
			 * mOriginalValues.remove(6); mOriginalValues.add(0, infor);
			 */
			FilterResults results = new FilterResults();
			List<Information> resultList = new ArrayList<Information>();
			if (constraint.equals(String.valueOf(Information.ALL))
					&& mMenuState == Information.MENU_ALL) {
				resultList = mOriginalValues;
			} else {
				if (mOriginalValues != null && !mOriginalValues.isEmpty()) {
					for (int i = 0; i < mOriginalValues.size(); i++) {
						Information information = mOriginalValues.get(i);
						if (information != null) {
							if (!TextUtils.isEmpty(constraint)) {
								int msgType = information.getType();
								if (constraint.equals(String
										.valueOf(Information.ALL))) {
									boolean isRead = information.isRead();
									boolean isFavorites = information
											.isFavorites();
									if (mMenuState == Information.MENU_UNREAD) {
										if (!isRead) {
											resultList.add(information);
										}
									} else if (mMenuState == Information.MENU_READ) {
										if (isRead) {
											resultList.add(information);
										}
									} else if (mMenuState == Information.MENU_FAVORITES) {
										if (isFavorites) {
											resultList.add(information);
										}
									} else {
										resultList.add(information);
									}
								} else if (constraint.equals(String
										.valueOf(msgType))) {
									boolean isRead = information.isRead();
									boolean isFavorites = information
											.isFavorites();
									if (mMenuState == Information.MENU_UNREAD) {
										if (!isRead) {
											resultList.add(information);
										}
									} else if (mMenuState == Information.MENU_READ) {
										if (isRead) {
											resultList.add(information);
										}
									} else if (mMenuState == Information.MENU_FAVORITES) {
										if (isFavorites) {
											resultList.add(information);
										}
									} else {
										resultList.add(information);
									}
								}
							}
						}
					}
				}
			}

			Collections.sort(resultList, new Comparator<Information>() {
				@Override
				public int compare(Information infor0, Information infor1) {
					if (mCompareID) {
						return new Integer(infor0.getMsgId())
								.compareTo(new Integer(infor1.getMsgId()));
					} else {
						return infor0.getTitle().compareTo(infor1.getTitle());
					}
				}
			});
			mFilterMsgType = String.valueOf(constraint);
			results.values = resultList;
			results.count = resultList.size();
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			if (results == null) {
				return;
			}
			mFilterDataList = (List<Information>) results.values;
			if (mInformationLayout != null) {
				if (mFilterMsgType.equals("2") && results.count == 0) {
					mInformationLayout.setVisibility(View.VISIBLE);
				} else {
					mInformationLayout.setVisibility(View.GONE);
				}
			}
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}

	public void setInformStateFilter(int state) {
		mMenuState = state;
	}

	public void setDefaultInfoLayout(RelativeLayout layout) {
		mInformationLayout = layout;
	}

	public SearchFilter getSearchFilter() {
		if (mSearchFilter == null) {
			mSearchFilter = new SearchFilter();
		}
		return mSearchFilter;
	}

	@SuppressLint("DefaultLocale")
	public class SearchFilter extends Filter {

		@SuppressLint("DefaultLocale")
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			List<Information> resultList = new ArrayList<Information>();
			if (TextUtils.isEmpty(constraint)) {
				resultList = mOriginalValues;
			} else {
				if (mOriginalValues != null && !mOriginalValues.isEmpty()) {
					String lowerConstraint = constraint.toString()
							.toLowerCase();
					for (int i = 0; i < mOriginalValues.size(); i++) {
						Information information = mOriginalValues.get(i);
						if (information != null) {
							String title = information.getTitle();
							String content = information.getContent();
							if (title.toLowerCase().contains(lowerConstraint)
									|| content.toLowerCase().contains(
											lowerConstraint)) {
								resultList.add(information);
							}
						}
					}
				}
			}
			results.values = resultList;
			results.count = resultList.size();
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			mFilterDataList = (List<Information>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
