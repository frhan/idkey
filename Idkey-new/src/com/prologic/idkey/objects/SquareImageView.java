package com.prologic.idkey.objects;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageView extends ImageView {
	private static final float RATIO = 4f / 4f;
	
	public SquareImageView(Context context) 
	{
		super(context, null);

	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
		int heigthWithoutPadding = height - getPaddingTop() - getPaddingBottom();

		int maxWidth = (int) (heigthWithoutPadding * RATIO);
		int maxHeight = (int) (widthWithoutPadding / RATIO);

		if (widthWithoutPadding > maxWidth) {
			width = maxWidth + getPaddingLeft() + getPaddingRight();
		} else {
			height = maxHeight + getPaddingTop() + getPaddingBottom();
		}

		if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
			width = getMeasuredWidth();
		}
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
			height = getMeasuredHeight();
		}

		setMeasuredDimension(width, height);
	}

}
