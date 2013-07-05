package com.prologic.idkey.activities;

import java.util.ArrayList;
import java.util.List;

import com.prologic.idkey.CustomProgressDailog;
import com.prologic.idkey.R;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.GetAllCategoriesCommand;
import com.prologic.idkey.objects.Category;
import com.prologic.idkey.objects.CategoryAdapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class CategoryActivity extends MainActivity implements OnItemClickListener {

	private ListView lvCategories;
	private CategoryAdapter adapter;
	private List<Category> listCategories;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);

		setContentView(R.layout.category_screen);
		listCategories = new ArrayList<Category>();	

		lvCategories = (ListView) findViewById(R.id.lv_categories);

		adapter = new CategoryAdapter(context, R.layout.category_row_view, listCategories);
		lvCategories.setAdapter(adapter);

		loadCategoryList();
	}

	private void loadCategoryList() 
	{	
		new CategoryListTask(context).execute();

	}

	private void updateCategoryList(List<Category> categories) 
	{

		listCategories.addAll(categories);

		adapter.notifyDataSetChanged();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id)
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
			progressDialog.setTitle("Log In");
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

	private class UpdateCategoryTask extends AsyncTask<Void, Void, Void>
	{
		public UpdateCategoryTask(Context context,int userCategoryId,String newName) 
		{

		}

		@Override
		protected Void doInBackground(Void... params) {

			return null;
		}

	}

	private class DeleteCategoryTask extends AsyncTask<Void, Void, Void>
	{
		public DeleteCategoryTask(Context context,int userCategoryId) 
		{
			

		}

		@Override
		protected Void doInBackground(Void... params) {
			
			return null;
		}

	}

	private class CreateCategoriesTask extends AsyncTask<Void, Void, Void>
	{
		private String categoryName;
		private Context context;

		public CreateCategoriesTask(Context context,String categoryName) 
		{
			this.categoryName = categoryName;
		}
		@Override
		protected Void doInBackground(Void... params) {

			return null;
		}

	}



}
