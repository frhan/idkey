package com.prologic.idkey.activities;

import java.util.ArrayList;
import java.util.List;

import com.prologic.idkey.CustomProgressDailog;
import com.prologic.idkey.R;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.GetAllCategoriesCommand;
import com.prologic.idkey.objects.Category;
import com.prologic.idkey.objects.CategoryAdapter;
import com.prologic.idkey.objects.CategoryAdapter.OnCategoryItemDeleteListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class CategoryActivity extends MainActivity implements OnItemClickListener,OnCategoryItemDeleteListener {

	private ListView lvCategories;
	private CategoryAdapter adapter;
	private List<Category> listCategories;
	private boolean isEditing;
	private Button btnEditDone;
	private Button btnAddNewCategory;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);

		setContentView(R.layout.category_screen);
		btnEditDone = (Button) findViewById(R.id.btn_cate_done);
		btnAddNewCategory = (Button) findViewById(R.id.btn_category_add_new);
		listCategories = new ArrayList<Category>();	

		lvCategories = (ListView) findViewById(R.id.lv_categories);

		adapter = new CategoryAdapter(context, R.layout.category_row_view, listCategories);
		adapter.setOnCategoryItemDeleteListener(this);
		lvCategories.setOnItemClickListener(this);
		lvCategories.setAdapter(adapter);

		isEditing = false;
		loadCategoryList();
	}

	private void loadCategoryList() 
	{	
		new CategoryListTask(context).execute();

	}

	private void updateCategoryList(List<Category> categories) 
	{

		if(categories != null)
		{
			listCategories.addAll(categories);

			adapter.notifyDataSetChanged();
		}

	}

	public void onClickAddCategory(View v) 
	{
		final EditText input = new EditText(context);
		input.setHint("name");
		showCustomAlertDailog("Name?","Add","Cancel",input,new ICustomDailogClickListener() {

			@Override
			public void onOkClick(View customView) 
			{
				Toast.makeText(context,(((EditText) customView).getText().toString()),Toast.LENGTH_SHORT).show();

			}
		});

	}

	public void onClickClose(View v)
	{
		finish();
	}

	public void onClickEditDone(View v)
	{	
		btnEditDone.setText(!isEditing?"Done":"Edit");
		btnAddNewCategory.setVisibility(!isEditing?View.VISIBLE:View.GONE);
		adapter.setShowDelete(!isEditing);
		adapter.notifyDataSetChanged();	
		isEditing = !isEditing;
	}	

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id)
	{
		Category category = listCategories.get(pos);
		if(isEditing)
		{
			final EditText input = new EditText(context);
			input.setText(category.getName());
			showCustomAlertDailog("Edit Category","Update","Cancel",input,new ICustomDailogClickListener() {

				@Override
				public void onOkClick(View customView) 
				{
					Toast.makeText(context,(((EditText) customView).getText().toString()),Toast.LENGTH_SHORT).show();

				}
			});
		}


	}


	@Override
	public void onItemDelete(int position) 
	{
		listCategories.remove(position);
		adapter.notifyDataSetChanged();

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
