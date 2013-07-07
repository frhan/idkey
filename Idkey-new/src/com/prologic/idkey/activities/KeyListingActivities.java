package com.prologic.idkey.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.prologic.idkey.R;
import com.prologic.idkey.objects.Key;
import com.prologic.idkey.objects.KeyListAdapter;
import com.prologic.idkey.objects.KeysComparator;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class KeyListingActivities extends MainActivity implements OnClickListener
{
	private List<Key> listKeys;
	private ListView lvKeys;
	private KeyListAdapter adapter;
	private Button btnNoKeySort;
	private Button btnKeySortId;
	private Button btnKeySortCat;
	private KeysComparator keysComparator;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.key_list_layout);
		lvKeys = (ListView) findViewById(R.id.lv_keys);
		btnNoKeySort = (Button) findViewById(R.id.btn_key_sort_no);
		btnKeySortId = (Button) findViewById(R.id.btn_key_sort_id);
		btnKeySortCat = (Button) findViewById(R.id.btn_key_sort_cat);

		listKeys = new ArrayList<Key>();
		adapter = new KeyListAdapter(this, R.layout.key_list_row_view, listKeys);
		lvKeys.setAdapter(adapter);

		btnNoKeySort.setOnClickListener(this);
		btnKeySortId.setOnClickListener(this);
		btnKeySortCat.setOnClickListener(this);

		keysComparator = new KeysComparator(KeysComparator.SORTING_TYPE_NO, KeysComparator.SORTING_ORDER_ASCENDING);
		loadKeys();
	}

	private void loadKeys()
	{
		Key key = new Key(1, "aoom 15", "11/12/13", "Raddison");
		Key key1 = new Key(2, "room 19", "11/12/13", "Raddison blu");
		Key key2 = new Key(3, "zoom 16","11/12/13", "sonargaon");
		Key key3 = new Key(4, "poom 17","11/12/13", "Hilton");
		Key key4 = new Key(5, "boom 18", "11/12/13", "hilton");

		listKeys.add(key);
		listKeys.add(key2);
		listKeys.add(key4);
		listKeys.add(key1);
		listKeys.add(key3);

		adapter.notifyDataSetChanged();


	}
	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.btn_key_sort_no:
			keysComparator.setSortingType(KeysComparator.SORTING_TYPE_NO);
			Collections.sort(listKeys,keysComparator);
			adapter.notifyDataSetChanged();
			break;

		case R.id.btn_key_sort_id:
			keysComparator.setSortingType(KeysComparator.SORTING_TYPE_ID);
			Collections.sort(listKeys,keysComparator);
			adapter.notifyDataSetChanged();
			break;
		case R.id.btn_key_sort_cat:
			keysComparator.setSortingType(KeysComparator.SORTING_TYPE_ALL_CAT);
			Collections.sort(listKeys,keysComparator);
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}
	}

}
