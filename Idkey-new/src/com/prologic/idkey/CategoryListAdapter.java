package com.prologic.idkey;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.prologic.idkey.objects.Category;

public  class CategoryListAdapter extends ArrayAdapter<Category>
{
	private List<Category> listCategories;
	private Context context;
	private LayoutInflater inflater ;


	public CategoryListAdapter(Context context, int textViewResourceId,
			List<Category> objects) {
		super(context, textViewResourceId, objects);
		this.listCategories = objects;
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}

	@Override
	public Category getItem(int position) {
		return listCategories.get(position);
	}
	@Override
	public int getCount() {

		return listCategories.size();
	}


	public View getCustomView(int position, View convertView, ViewGroup parent) {
		Category category = listCategories.get(position);
		if(convertView == null)
		{				
			convertView = (View) inflater.inflate(R.layout.category_list,null);
		}
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_category_list_row);
		tvTitle.setText(category.getName());

		return convertView;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return getCustomView(position, convertView, parent);
	}
}

