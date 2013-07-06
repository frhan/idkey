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
	private boolean showDeleteButton;
	private OnCategoryItemDeleteListener categoryItemDeleteListener;
	public CategoryAdapter(Context context, int textViewResourceId,
			List<Category> objects) {
		super(context, textViewResourceId, objects);
		this.listCategories = objects;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		showDeleteButton = false;
	}

	@Override
	public int getCount() {
		return listCategories.size();
	}

	@Override
	public Category getItem(int position) {
		return listCategories.get(position);
	}

	public void setShowDelete(boolean showDeleteButton)
	{
		this.showDeleteButton = showDeleteButton;
	}

	public void setOnCategoryItemDeleteListener(OnCategoryItemDeleteListener categoryItemDeleteListener)
	{
		this.categoryItemDeleteListener = categoryItemDeleteListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Category category = listCategories.get(position);
		final ViewHolder viewHolder;
		if(convertView == null)
		{
			convertView = (View) layoutInflater.inflate(R.layout.category_row_view, null);				
			viewHolder = new ViewHolder();
			viewHolder.txtCategoryName = (TextView) convertView.findViewById(R.id.txt_cat_name);
			viewHolder.txtCount = (TextView) convertView.findViewById(R.id.txt_cat_count);	
			viewHolder.btnActualDelete = (Button) convertView.findViewById(R.id.btn_cat_delete);
			viewHolder.btnCrossDelete =  (Button) convertView.findViewById(R.id.btn_cat_row_cross_button);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.btnCrossDelete.setVisibility((showDeleteButton && category.getCount() == 0 && !category.getName().equalsIgnoreCase("Unassigned"))? View.VISIBLE: View.GONE);		
		viewHolder.btnActualDelete.setVisibility(View.GONE);
		viewHolder.btnActualDelete.setTag(position);
		viewHolder.btnActualDelete.setOnClickListener(this);
		viewHolder.txtCategoryName.setText(category.getName());		
		viewHolder.txtCount.setText(String.valueOf(category.getCount()));

		viewHolder.btnCrossDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				viewHolder.btnActualDelete.setVisibility(View.VISIBLE);

			}
		});
		return convertView;
	}

	@Override
	public void onClick(View v) 
	{

		switch (v.getId()) {
		case R.id.btn_cat_delete:
			if(categoryItemDeleteListener != null)
			{
				categoryItemDeleteListener.onItemDelete((Integer)v.getTag());
			}
			break;

		default:
			break;
		}
	}

	private class ViewHolder
	{
		Button btnCrossDelete;
		Button btnActualDelete;
		TextView txtCategoryName;
		TextView txtCount;

	}
	public interface OnCategoryItemDeleteListener
	{
		void onItemDelete(int position);
	}
}
