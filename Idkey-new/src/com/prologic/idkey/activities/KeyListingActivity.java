package com.prologic.idkey.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.prologic.idkey.CustomProgressDailog;
import com.prologic.idkey.R;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.GetKeysCommand;
import com.prologic.idkey.objects.Key;
import com.prologic.idkey.objects.KeyListAdapter;
import com.prologic.idkey.objects.KeysComparator;

public class KeyListingActivity extends MainActivity implements OnClickListener,OnItemClickListener
{
	private List<Key> listKeys;
	private ListView lvKeys;
	private KeyListAdapter adapter;
	private Button btnNoKeySort;
	private Button btnKeySortId;
	private Button btnKeySortCat;
	private Button btnKeySortDate;
	private ImageView ivOrderIndicator;
	private EditText etSearch;
	private TextView tvKeyListTitle;
	public static final String USER_CATEGORY_ID = "user_category_id";
	public static final String USER_CATEGORY_NAME = "user_category_name";

	int userCategoryId;
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
		tvKeyListTitle = (TextView) findViewById(R.id.txt_key_list_title);
		ivOrderIndicator = (ImageView) findViewById(R.id.btn_order_indicator);
		btnKeySortDate = (Button)findViewById(R.id.btn_key_sort_date);

		listKeys = new ArrayList<Key>();
		adapter = new KeyListAdapter(this, R.layout.key_list_row_view, listKeys);
		lvKeys.setAdapter(adapter);
		lvKeys.setOnItemClickListener(this);

		btnNoKeySort.setOnClickListener(this);
		btnKeySortId.setOnClickListener(this);
		btnKeySortCat.setOnClickListener(this);
		btnKeySortDate.setOnClickListener(this);
		btnKeySortId.setSelected(false);

		userCategoryId = -1;
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
		{
			userCategoryId = bundle.getInt(USER_CATEGORY_ID, -1);
			String userCategoryName = bundle.getString(USER_CATEGORY_NAME);
			if(userCategoryName != null)
				tvKeyListTitle.setText(userCategoryName);

		}


		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) 
			{

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) 
			{

			}

			@Override
			public void afterTextChanged(Editable editable) 
			{
				String text = etSearch.getText().toString().toLowerCase(Locale.getDefault());
				adapter.filter(text);
			}
		});

	}

	@Override
	protected void onResume() {

		super.onResume();

		loadKeys();

		etSearch.setText("");
		adapter.setSortOrder(KeysComparator.SORTING_ORDER_ASCENDING);
		adapter.setSortingType(KeysComparator.SORTING_TYPE_ID);
	}
	private void loadKeys()
	{
		if(isOnline())
		{
			new KeyLoadTask(context,userCategoryId).execute();
		}else {
			showOkAlertDailog("No internet Connection connection", "Internet", false);
		}
	}

	private void updateKeyList(List<Key> keys)
	{
		if(keys != null)
		{
			listKeys.clear();
			listKeys.addAll(keys);
			adapter.updateList(keys);
			//need to optimize
			if(currentSelectedButton != null)
			{
				currentSelectedButton.setSelected(false);			
			}
			
			currentSelectedButton = btnNoKeySort;			
			btnNoKeySort.setSelected(true);
			ivOrderIndicator.setImageResource(adapter.getSortingOrder() == KeysComparator.SORTING_ORDER_ASCENDING? R.drawable.keys_filter_arrow_up:R.drawable.keys_filter_arrow_down);				
		}
	}


	@Override
	public void onClickMainKeys(View v) 
	{

	}

	private Button currentSelectedButton  = null;
	//need to optimize
	@Override
	public void onClick(View v) {
		super.onClick(v);

		if(currentSelectedButton != null)
		{
			currentSelectedButton.setSelected(false);			
		}
		switch (v.getId()) 
		{
		case R.id.btn_key_sort_id:

			if(adapter.getSortingType() == KeysComparator.SORTING_TYPE_ID)
			{
				adapter.setSortOrder(adapter.getSortingOrder() == KeysComparator.SORTING_ORDER_ASCENDING ? 
						KeysComparator.SORTING_ORDER_DECENDING: KeysComparator.SORTING_ORDER_ASCENDING );
				adapter.sortData(KeysComparator.SORTING_TYPE_ID);
			}else {
				adapter.sortData(KeysComparator.SORTING_TYPE_ID);
			}
			btnNoKeySort.setSelected(true);
			currentSelectedButton = btnNoKeySort;
			break;

		case R.id.btn_key_sort_name:

			if(adapter.getSortingType() == KeysComparator.SORTING_TYPE_NAME)
			{
				adapter.setSortOrder(adapter.getSortingOrder() == KeysComparator.SORTING_ORDER_ASCENDING ? 
						KeysComparator.SORTING_ORDER_DECENDING: KeysComparator.SORTING_ORDER_ASCENDING );
				adapter.sortData(KeysComparator.SORTING_TYPE_NAME);
			}else {
				adapter.sortData(KeysComparator.SORTING_TYPE_NAME);
			}
			btnKeySortId.setSelected(true);
			currentSelectedButton = btnKeySortId;
			break;
		case R.id.btn_key_sort_cat:

			if(adapter.getSortingType() == KeysComparator.SORTING_TYPE_ALL_CAT)
			{
				adapter.setSortOrder(adapter.getSortingOrder() == KeysComparator.SORTING_ORDER_ASCENDING ? 
						KeysComparator.SORTING_ORDER_DECENDING: KeysComparator.SORTING_ORDER_ASCENDING );
				adapter.sortData(KeysComparator.SORTING_TYPE_ALL_CAT);
			}else {
				adapter.sortData(KeysComparator.SORTING_TYPE_ALL_CAT);
			}
			btnKeySortCat.setSelected(true);
			currentSelectedButton = btnKeySortCat;
			break;
		case R.id.btn_key_sort_date:
			if(adapter.getSortingType() == KeysComparator.SORTING_TYPE_DATE)
			{
				adapter.setSortOrder(adapter.getSortingOrder() == KeysComparator.SORTING_ORDER_ASCENDING ? 
						KeysComparator.SORTING_ORDER_DECENDING: KeysComparator.SORTING_ORDER_ASCENDING );
				adapter.sortData(KeysComparator.SORTING_TYPE_DATE);
			}else {
				adapter.sortData(KeysComparator.SORTING_TYPE_DATE);
			}
			btnKeySortDate.setSelected(true);
			currentSelectedButton = btnKeySortDate;
			break;

		default:
			break;
		}

		ivOrderIndicator.setImageResource(adapter.getSortingOrder() == KeysComparator.SORTING_ORDER_ASCENDING? R.drawable.keys_filter_arrow_up:R.drawable.keys_filter_arrow_down);	

	}
	public void onClickCategories(View v)
	{
		setCurrent(com.prologic.idkey.activities.CategoryActivity.class, null);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) 
	{
		Key key = adapter.getItem(pos);
		if(key != null)
		{
			String [] keyArray = new String[8];
			keyArray[0] = String.valueOf(key.getId());
			keyArray[1] = key.getName();
			keyArray[2] = key.getIqeToken();
			keyArray[3] = key.getScanUrl();
			keyArray[4] = String.valueOf(key.getCategoryId());
			keyArray[5] = key.getCategoryName();
			keyArray[6] = key.getCreateDate();	
			keyArray[7] =String.valueOf(key.getNo());

			Bundle b = new Bundle();
			b.putStringArray(KeyShowActivity.CURRENT_KEY,keyArray);

			setCurrent(com.prologic.idkey.activities.KeyShowActivity.class, b);
		}


	}

}
