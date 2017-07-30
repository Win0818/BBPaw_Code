package com.worldchip.bbp.ect.adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public abstract class AnimationAdapter extends BaseAdapterDecorator {

	protected static final long DEFAULTANIMATIONDELAYMILLIS = 100;
	protected static final long DEFAULTANIMATIONDURATIONMILLIS = 300;
	private static final long INITIALDELAYMILLIS = 150;

	private SparseArray<AnimationInfo> mAnimators;
	private long mAnimationStartMillis;
	private int mFirstAnimatedPosition;
	private int mLastAnimatedPosition;
	private boolean mHasParentAnimationAdapter;
	private boolean mShouldAnimate = true;

	public AnimationAdapter(BaseAdapter baseAdapter)
	{
		super(baseAdapter);
		mAnimators = new SparseArray<AnimationInfo>();

		mAnimationStartMillis = -1;
		mLastAnimatedPosition = -1;

		if (baseAdapter instanceof AnimationAdapter)
		{
			((AnimationAdapter) baseAdapter).setHasParentAnimationAdapter(true);
		}
	}

	public void reset() 
	{
		mAnimators.clear();
		mFirstAnimatedPosition = 0;
		mLastAnimatedPosition = -1;
		mAnimationStartMillis = -1;
		mShouldAnimate = true;

		if (getDecoratedBaseAdapter() instanceof AnimationAdapter) 
		{
			((AnimationAdapter) getDecoratedBaseAdapter()).reset();
		}
	}

	public void setShouldAnimate(boolean shouldAnimate)
	{
		mShouldAnimate = shouldAnimate;
	}

	public void setShouldAnimateFromPosition(int position)
	{
		mShouldAnimate = true;
		mFirstAnimatedPosition = position - 1;
		mLastAnimatedPosition = position - 1;
	}

	public void setShouldAnimateNotVisible()
	{
		if (getAbsListView() == null)
		{
			throw new IllegalStateException("Call setListView() on this AnimationAdapter before setShouldAnimateNotVisible()!");
		}

		mShouldAnimate = true;
		mFirstAnimatedPosition = getAbsListView().getLastVisiblePosition();
		mLastAnimatedPosition = getAbsListView().getLastVisiblePosition();
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent)
	{
		boolean alreadyStarted = false;
		if (!mHasParentAnimationAdapter)
		{
			if (getAbsListView() == null) 
			{
				throw new IllegalStateException("Call setListView() on this AnimationAdapter before setAdapter()!");
			}

			if (convertView != null) 
			{
				alreadyStarted = cancelExistingAnimation(position, convertView);
			}
		}

		View itemView = super.getView(position, convertView, parent);

		if (!mHasParentAnimationAdapter && !alreadyStarted) 
		{
			animateViewIfNecessary(position, itemView, parent);
		}
		return itemView;
	}

	private boolean cancelExistingAnimation(int position, View convertView)
	{
		boolean alreadyStarted = false;

		int hashCode = convertView.hashCode();
		AnimationInfo animationInfo = mAnimators.get(hashCode);
		if (animationInfo != null)
		{
			if (animationInfo.position != position)
			{
				animationInfo.animator.end();
				mAnimators.remove(hashCode);
			} else {
				alreadyStarted = true;
			}
		}
		return alreadyStarted;
	}

	private void animateViewIfNecessary(int position, View view, ViewGroup parent)
	{
		if (position > mLastAnimatedPosition && mShouldAnimate) 
		{
			animateView(position, parent, view, false);
			mLastAnimatedPosition = position;
		}
	}

	private void animateView(int position, ViewGroup parent, View view, boolean isHeader) 
	{
		if (mAnimationStartMillis == -1) 
		{
			mAnimationStartMillis = System.currentTimeMillis();
		}
		hideView(view);

		Animator[] childAnimators;
		if (mDecoratedBaseAdapter instanceof AnimationAdapter)
		{
			childAnimators = ((AnimationAdapter) mDecoratedBaseAdapter).getAnimators(parent, view);
		} else {
			childAnimators = new Animator[0];
		}
		Animator[] animators = getAnimators(parent, view);
		Animator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(concatAnimators(childAnimators, animators, alphaAnimator));
		set.setStartDelay(calculateAnimationDelay(isHeader));
		set.setDuration(getAnimationDurationMillis());
		set.start();

		mAnimators.put(view.hashCode(), new AnimationInfo(position, set));
	}

	private void hideView(View view) 
	{
		ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0);
		AnimatorSet set = new AnimatorSet();
		set.play(animator);
		set.setDuration(0);
		set.start();
	}

	private Animator[] concatAnimators(Animator[] childAnimators, Animator[] animators, Animator alphaAnimator)
	{
		Animator[] allAnimators = new Animator[childAnimators.length + animators.length + 1];
		int i;

		for (i = 0; i < animators.length; ++i)
		{
			allAnimators[i] = animators[i];
		}

		for (int j = 0; j < childAnimators.length; ++j)
		{
			allAnimators[i] = childAnimators[j];
			++i;
		}

		allAnimators[allAnimators.length - 1] = alphaAnimator;
		return allAnimators;
	}

	@SuppressLint("NewApi")
	private long calculateAnimationDelay(boolean isHeader) 
	{
		long delay;
		int numberOfItems = getAbsListView().getLastVisiblePosition() - getAbsListView().getFirstVisiblePosition();
		if (numberOfItems + 1 < mLastAnimatedPosition) 
		{
			delay = getAnimationDelayMillis();

			if (getAbsListView() instanceof GridView && Build.VERSION.SDK_INT >= 11)
			{
				delay += getAnimationDelayMillis() * ((mLastAnimatedPosition + 1) % ((GridView) getAbsListView()).getNumColumns());
			}
		} else {
			long delaySinceStart = (mLastAnimatedPosition - mFirstAnimatedPosition + 1) * getAnimationDelayMillis();
			delay = mAnimationStartMillis + getInitialDelayMillis() + delaySinceStart - System.currentTimeMillis();
			delay -= isHeader && mLastAnimatedPosition > 0 ? getAnimationDelayMillis() : 0;
		}
		return Math.max(0, delay);
	}

	public void setHasParentAnimationAdapter(boolean hasParentAnimationAdapter)
	{
		mHasParentAnimationAdapter = hasParentAnimationAdapter;
	}

	protected long getInitialDelayMillis() 
	{
		return INITIALDELAYMILLIS;
	}

	protected abstract long getAnimationDelayMillis();

	protected abstract long getAnimationDurationMillis();

	public abstract Animator[] getAnimators(ViewGroup parent, View view);

	private class AnimationInfo 
	{
		public int position;
		public Animator animator;

		public AnimationInfo(int position, Animator animator) 
		{
			this.position = position;
			this.animator = animator;
		}
	}
}