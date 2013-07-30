package com.prologic.idkey;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.prologic.idkey.objects.Category;


public  class CategorySpinnerAdapter extends ArrayAdapter<Category>
{
	private List<Category> listCategories;
	private Context context;
	private LayoutInflater inflater ;
	private int textSize;
	
	public CategorySpinnerAdapter(Context context, int textViewResourceId,
			List<Category> objects) {
		super(context, textViewResourceId, objects);
		this.listCategories = objects;
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		textSize  = -1;
	}

	@Override
	public Category getItem(int position) {
		return listCategories.get(position);
	}
	@Override
	public int getCount() {

		return listCategories.size();
	}
	@Override
	public View getDropDownView(int position, View convertView,ViewGroup parent)
	{
		View v =  getCustomView(position, convertView, parent);

		return v;		

	}
	
	public void setTextSize(int dimen)
	{
		textSize = dimen;
	}
	
	public View getCustomView(int position, View convertView, ViewGroup parent) {
		Category category = listCategories.get(position);
		if(convertView == null)
		{				
			convertView = (View) inflater.inflate(R.layout.spinner_view,null);
		}
		TextView tvTitle = (TextView) convertView.findViewById(R.id.textView_spinner);
		tvTitle.setText(category.getName());
		
		if(textSize >-1)
		{
			//tvTitle.setTextSize(context.getResources().getDimension(textSize));
			tvTitle.setTypeface(null, Typeface.BOLD);
		}
		return convertView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return getCustomView(position, convertView, parent);
	}
}

