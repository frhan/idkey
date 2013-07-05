package com.prologic.idkey.objects;

import java.util.List;

import com.prologic.idkey.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CategoryAdapter extends ArrayAdapter<Category> implements OnClickListener
{
	private List<Category> listCategories;
	private LayoutInflater layoutInflater;

	public CategoryAdapter(Context context, int textViewResourceId,
			List<Category> objects) {
		super(context, textViewResourceId, objects);
		this.listCategories = objects;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listCategories.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Category category = listCategories.get(position);
		if(convertView == null)
		{
			convertView = (View) layoutInflater.inflate(R.layout.category_row_view, null);			
		}

		Button btnDeleteCategory = (Button) convertView.findViewById(R.id.btn_cat_row_cross_button);
		TextView txtCatName = (TextView) convertView.findViewById(R.id.txt_cat_name);
		TextView txtCatCount = (TextView) convertView.findViewById(R.id.txt_cat_count);

		txtCatName.setText(category.getName());
		txtCatCount.setText(String.valueOf(category.getCount()));
		return convertView;
	}

	@Override
	public void onClick(View v) 
	{
	
		
	}



}
