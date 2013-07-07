package com.prologic.idkey.objects;

import java.util.List;

import com.prologic.idkey.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class KeyListAdapter extends ArrayAdapter<Key>
{

	private List<Key> listKeys;
	private LayoutInflater layoutInflater;
	private int [] rowColors = {Color.parseColor("#ffffff"),Color.parseColor("#accdf3")};
	
	public KeyListAdapter(Context context, int textViewResourceId,
			List<Key> objects) 
	{
		super(context, textViewResourceId, objects);
		this.listKeys = objects;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}
	@Override
	public int getCount() {
		return listKeys.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		Key key = listKeys.get(position);
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

		txtKeyNo.setText(String.valueOf(key.getNo()));
		txtKeyId.setText(key.getId());
		txtKeyDate.setText(key.getDate().toString());
		txtKeyHotelName.setText(key.getCategory());	


		return convertView;
	}


}
