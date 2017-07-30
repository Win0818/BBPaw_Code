package com.worldchip.bbp.ect.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import com.worldchip.bbp.ect.view.DynamicListView;
import com.worldchip.bbp.ect.view.DynamicListView.Swappable;

public abstract class BaseAdapterDecorator extends BaseAdapter implements SectionIndexer, DynamicListView.Swappable {

	protected final BaseAdapter mDecoratedBaseAdapter;

	private AbsListView mListView;

	private boolean mIsParentHorizontalScrollContainer;
	private int mResIdTouchChild;

	public BaseAdapterDecorator(BaseAdapter baseAdapter) 
	{
		mDecoratedBaseAdapter = baseAdapter;
	}

	public void setAbsListView(AbsListView listView) 
	{
		mListView = listView;
		if (mDecoratedBaseAdapter instanceof BaseAdapterDecorator)
		{
			((BaseAdapterDecorator) mDecoratedBaseAdapter).setAbsListView(listView);
		}

		if (mListView instanceof DynamicListView) 
		{
			DynamicListView dynListView = (DynamicListView) mListView;
			dynListView.setIsParentHorizontalScrollContainer(mIsParentHorizontalScrollContainer);
			dynListView.setDynamicTouchChild(mResIdTouchChild);
		}
	}

	public AbsListView getAbsListView()
	{
		return mListView;
	}

	@Override
	public int getCount() 
	{
		return mDecoratedBaseAdapter.getCount();
	}

	@Override
	public Object getItem(int position)
	{
		return mDecoratedBaseAdapter.getItem(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return mDecoratedBaseAdapter.getItemId(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return mDecoratedBaseAdapter.getView(position, convertView, parent);
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return mDecoratedBaseAdapter.areAllItemsEnabled();
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		return mDecoratedBaseAdapter.getDropDownView(position, convertView, parent);
	}

	@Override
	public int getItemViewType(int position) 
	{
		return mDecoratedBaseAdapter.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() 
	{
		return mDecoratedBaseAdapter.getViewTypeCount();
	}

	@Override
	public boolean hasStableIds() 
	{
		return mDecoratedBaseAdapter.hasStableIds();
	}

	@Override
	public boolean isEmpty()
	{
		return mDecoratedBaseAdapter.isEmpty();
	}

	@Override
	public boolean isEnabled(int position) 
	{
		return mDecoratedBaseAdapter.isEnabled(position);
	}

	@Override
	public void notifyDataSetChanged()
	{
		if (!(mDecoratedBaseAdapter instanceof ArrayAdapter<?>)) 
		{
			mDecoratedBaseAdapter.notifyDataSetChanged();
		}
	}
	
	public void notifyDataSetChanged(Boolean force)
	{
		if ((force) || (!(mDecoratedBaseAdapter instanceof ArrayAdapter<?>)))
		{
			mDecoratedBaseAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void notifyDataSetInvalidated()
	{
		mDecoratedBaseAdapter.notifyDataSetInvalidated();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer)
	{
		mDecoratedBaseAdapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer)
	{
		mDecoratedBaseAdapter.unregisterDataSetObserver(observer);
	}

	@Override
	public int getPositionForSection(int section)
	{
		if (mDecoratedBaseAdapter instanceof SectionIndexer)
		{
			return ((SectionIndexer) mDecoratedBaseAdapter).getPositionForSection(section);
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) 
	{
		if (mDecoratedBaseAdapter instanceof SectionIndexer)
		{
			return ((SectionIndexer) mDecoratedBaseAdapter).getSectionForPosition(position);
		}
		return 0;
	}

	@Override
	public Object[] getSections() 
	{
		if (mDecoratedBaseAdapter instanceof SectionIndexer)
		{
			return ((SectionIndexer) mDecoratedBaseAdapter).getSections();
		}
		return null;
	}

	public BaseAdapter getDecoratedBaseAdapter() 
	{
		return mDecoratedBaseAdapter;
	}

	@Override
	public void swapItems(int positionOne, int positionTwo)
	{
		if (mDecoratedBaseAdapter instanceof Swappable)
		{
			((Swappable) mDecoratedBaseAdapter).swapItems(positionOne, positionTwo);
		}
	}

	public void setIsParentHorizontalScrollContainer(boolean isParentHorizontalScrollContainer)
	{
		mIsParentHorizontalScrollContainer = isParentHorizontalScrollContainer;
		if (mListView instanceof DynamicListView) 
		{
			DynamicListView dynListView = (DynamicListView) mListView;
			dynListView.setIsParentHorizontalScrollContainer(mIsParentHorizontalScrollContainer);
		}
	}

	public boolean isParentHorizontalScrollContainer() 
	{
		return mIsParentHorizontalScrollContainer;
	}

	public void setTouchChild(int childResId) 
	{
		mResIdTouchChild = childResId;
		if (mListView instanceof DynamicListView)
		{
			DynamicListView dynListView = (DynamicListView) mListView;
			dynListView.setDynamicTouchChild(mResIdTouchChild);
		}
	}

	public int getTouchChild()
	{
		return mResIdTouchChild;
	}
}