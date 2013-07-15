package com.prologic.idkey.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.prologic.idkey.CategorySpinnerAdapter;
import com.prologic.idkey.CustomProgressDailog;
import com.prologic.idkey.R;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.GetAllCategoriesCommand;
import com.prologic.idkey.objects.Category;
import com.prologic.idkey.objects.Key;

public class KeyShowActivity extends MainActivity 
{
	private Button btnSave;
	private Button btnDelete;
	private Key currentKey = null;
	public static final String CURRENT_KEY = "current_key";
	private EditText etKeyName;
	private CategoryListTask categoryTask = null;
	private List<Category> listCategories;
	private Spinner spinnerCategory;
	private CategorySpinnerAdapter spinnerAdapter;
	private TextView txtKeyTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_key_layout);

		btnSave = (Button) findViewById(R.id.btn_kay_use);
		btnDelete = (Button) findViewById(R.id.btn_key_delete_cancel);
		etKeyName = (EditText) findViewById(R.id.et_key_id);
		spinnerCategory = (Spinner) findViewById(R.id.spinner_key_category);
		txtKeyTitle = (TextView) findViewById(R.id.txt_key_title);
				
		listCategories = new ArrayList<Category>();
		spinnerAdapter = new CategorySpinnerAdapter(this,R.layout.spinner_view,listCategories);
		spinnerCategory.setAdapter(spinnerAdapter);

		btnSave.setText("Save");
		btnDelete.setText("Delete");
		Bundle b = getIntent().getExtras();
		if(b != null)
		{
			String [] keyArray = b.getStringArray(CURRENT_KEY);

			if(keyArray != null && keyArray.length  > 0)
			{
				currentKey = new Key(Integer.valueOf(keyArray[0]));
				currentKey.setName(keyArray[1]);
				currentKey.setCategoryId(Integer.valueOf(keyArray[4]));
				currentKey.setIqeToken(keyArray[2]);
				currentKey.setScanUrl(keyArray[3]);
				currentKey.setCategoryName(keyArray[5]);
				currentKey.setCreateDate(keyArray[6]);				
			}			
		}
		
		if(currentKey != null)
		{
			etKeyName.setText(currentKey.getName());
			txtKeyTitle.setText("Key"+currentKey.getId());
			loadCategories();
		}
	}

	private void loadCategories()
	{
		categoryTask = new CategoryListTask(this);
		categoryTask.execute();
	}


	private void updateCategoryList(List<Category> listCategories)
	{
		this.listCategories.clear();
		if(listCategories != null && listCategories.size() >0)
		{
			this.listCategories.addAll(listCategories);
			spinnerAdapter.notifyDataSetChanged();
			for(int i = 0; i<this.listCategories.size(); i++)
			{
				if(this.listCategories.get(i).getId() == currentKey.getCategoryId())
				{
					spinnerCategory.setSelection(i);
					break;					
				}
			}
		}
	}
	private void updateKeyImage()
	{
		
		
	}

	public void onClickUse(View v)
	{

	}

	public void onClickKeyCancelDelete(View v)
	{

	}

	private class CategoryListTask extends AsyncTask<Void, Void, Void>
	{
		private Context context;
		private GetAllCategoriesCommand allCategoriesCommand;
		private CustomProgressDailog progressDialog;

		public CategoryListTask(Context context) 
		{
			this.context = context;
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
		protected Void doInBackground(Void... params) 
		{
			allCategoriesCommand = new GetAllCategoriesCommand();
			allCategoriesCommand.execute(ApiConnection.getInstance(context));

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if(progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}

			if(allCategoriesCommand != null)
			{
				List<Category> categories = allCategoriesCommand.getCategories();
				updateCategoryList(categories);
			}
		}
	}




}
