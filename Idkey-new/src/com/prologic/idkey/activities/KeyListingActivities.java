package com.prologic.idkey.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.prologic.idkey.CustomProgressDailog;
import com.prologic.idkey.R;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.GetKeysCommand;
import com.prologic.idkey.objects.Key;
import com.prologic.idkey.objects.KeyListAdapter;
import com.prologic.idkey.objects.KeysComparator;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import android.text.TextWatcher;

public class KeyListingActivities extends MainActivity implements OnClickListener
{
	private List<Key> listKeys;
	private ListView lvKeys;
	private KeyListAdapter adapter;
	private Button btnNoKeySort;
	private Button btnKeySortId;
	private Button btnKeySortCat;
	private KeysComparator keysComparator;
	private EditText etSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.key_list_layout);
		lvKeys = (ListView) findViewById(R.id.lv_keys);
		btnNoKeySort = (Button) findViewById(R.id.btn_key_sort_id);
		btnKeySortId = (Button) findViewById(R.id.btn_key_sort_name);
		btnKeySortCat = (Button) findViewById(R.id.btn_key_sort_cat);
		etSearch = (EditText) findViewById(R.id.et_key_search);
		listKeys = new ArrayList<Key>();
		adapter = new KeyListAdapter(this, R.layout.key_list_row_view, listKeys);
		lvKeys.setAdapter(adapter);

		btnNoKeySort.setOnClickListener(this);
		btnKeySortId.setOnClickListener(this);
		btnKeySortCat.setOnClickListener(this);

		keysComparator = new KeysComparator(KeysComparator.SORTING_TYPE_ID, KeysComparator.SORTING_ORDER_ASCENDING);


		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text
				adapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
		loadKeys();



	}

	private void loadKeys()
	{
		/*Key key = new Key(1, "aoom 15", "11/12/13", "Raddison");
		Key key1 = new Key(2, "room 19", "11/12/13", "Raddison blu");
		Key key2 = new Key(3, "zoom 16","11/12/13", "sonargaon");
		Key key3 = new Key(4, "poom 17","11/12/13", "Hilton");
		Key key4 = new Key(5, "boom 18", "11/12/13", "hilton");

		listKeys.add(key);
		listKeys.add(key2);
		listKeys.add(key4);
		listKeys.add(key1);
		listKeys.add(key3);

		adapter.notifyDataSetChanged();*/
		new KeyLoadTask(context).execute();


	}

	private void updateKeyList(List<Key> keys)
	{
		if(keys != null)
		{
			listKeys.clear();
			listKeys.addAll(keys);
			adapter.notifyDataSetChanged();
		}	

	}
	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.btn_key_sort_id:
			keysComparator.setSortingType(KeysComparator.SORTING_TYPE_ID);
			Collections.sort(listKeys,keysComparator);
			adapter.notifyDataSetChanged();
			break;

		case R.id.btn_key_sort_name:
			keysComparator.setSortingType(KeysComparator.SORTING_TYPE_NAME);
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

	private class KeyLoadTask extends AsyncTask<Void, Void, Void>
	{
		private int userCategoryId;
		private Context context;
		private CustomProgressDailog progressDialog;
		private GetKeysCommand keysCommand;

		public KeyLoadTask(Context context) 
		{
			this(context,-1);			
		}
		public KeyLoadTask(Context context,int userCategoryId) 
		{
			this.context = context;
			this.userCategoryId = userCategoryId;
			progressDialog = new CustomProgressDailog(context);
			progressDialog.setTitle("Loading");
			progressDialog.setMessage("Please wait...");

		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if(progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			updateKeyList(keysCommand.getListKeys());
			keysCommand = null;		

		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			keysCommand = new GetKeysCommand(userCategoryId);
			keysCommand.execute(ApiConnection.getInstance(context));
			return null;
		}

	}

}
