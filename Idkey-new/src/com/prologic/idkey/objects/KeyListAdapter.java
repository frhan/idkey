package com.prologic.idkey.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.prologic.idkey.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class KeyListAdapter extends BaseAdapter
{

	private List<Key> listKeys;
	private LayoutInflater layoutInflater;
	private int [] rowColors = {Color.parseColor("#ffffff"),Color.parseColor("#accdf3")};
	private List<Key> filterKeys  = null;
	private KeysComparator keysComparator;


	public KeyListAdapter(Context context, int textViewResourceId,
			List<Key> objects) 
	{
		//super(context, textViewResourceId, objects);
		this.listKeys = objects;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		filterKeys = new ArrayList<Key>();
		filterKeys.addAll(objects);
		keysComparator = new KeysComparator(KeysComparator.SORTING_TYPE_ID, KeysComparator.SORTING_ORDER_ASCENDING);
	}

	public void sortData(int sortingType)
	{
		keysComparator.setSortingType(sortingType);
		Collections.sort(filterKeys,keysComparator);
		notifyDataSetChanged();
	}
	public void setSortOrder(int orderType)
	{
		keysComparator.setSortingOrder(orderType);
	}

	public int getSortingOrder()
	{
		return keysComparator.getSortingOrder();
	}
	public void setSortingType(int sortingType)
	{
		 keysComparator.setSortingType(sortingType);
	}
	
	public int getSortingType()
	{
		return keysComparator.getSortingType();
	}
	
	@Override
	public int getCount() {
		return filterKeys.size();
	}

	@Override
	public Key getItem(int position) {
		return  filterKeys.get(position);
	}
	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	public void updateList(List<Key> keys)
	{
		filterKeys.clear();
		filterKeys.addAll(keys);	
		Collections.sort(filterKeys,keysComparator);
		notifyDataSetChanged();		
	}	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		Key key = getItem(position);
		if(convertView == null)
		{
			convertView = (View) layoutInflater.inflate(R.layout.key_list_row_view, null);

		}

		int colorPos = position % rowColors.length;
		convertView.setBackgroundColor(rowColors[colorPos]); 

		TextView txtKeyNo = (TextView) convertView.findViewById(R.id.txt_key_no);
		TextView txtKeyId = (TextView) convertView.findViewById(R.id.txt_key_id);
		TextView txtKeyDate = (TextView) convertView.findViewById(R.id.txt_key_date);
		TextView txtKeyHotelName = (TextView) convertView.findViewById(R.id.txt_hotel_name);

		txtKeyNo.setText(String.valueOf(key.getId()));
		txtKeyId.setText(key.getName());
		if(key.getCreateDate() != null && key.getCreateDate().split("T")[0] != null)
			txtKeyDate.setText(key.getCreateDate().split("T")[0]);
		txtKeyHotelName.setText(key.getCategoryName());	


		return convertView;
	}

	public void filter(String charText)
	{

		charText = charText.toLowerCase(Locale.getDefault());
		filterKeys.clear();
		if (charText.length() == 0) {
			filterKeys.addAll(this.listKeys);
		}
		  else
	        {
	            for (Key key : listKeys) 
	            {
	                if (key.getName().toLowerCase(Locale.getDefault()).contains(charText) ||
	                	key.getCategoryName().toLowerCase(Locale.getDefault()).contains(charText)) 
	                {
	                	filterKeys.add(key);
	                }
	            }
	        }
		Collections.sort(filterKeys,keysComparator);
		notifyDataSetChanged();	
	}


}
