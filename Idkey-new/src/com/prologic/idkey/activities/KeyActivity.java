package com.prologic.idkey.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iqengines.sdk.IQRemote;
import com.prologic.idkey.CategorySpinnerAdapter;
import com.prologic.idkey.CustomProgressDailog;
import com.prologic.idkey.R;
import com.prologic.idkey.Utilities;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.AddKeyCommand;
import com.prologic.idkey.api.command.CreateCategoryCommand;
import com.prologic.idkey.api.command.GetAllCategoriesCommand;
import com.prologic.idkey.objects.Category;

public class KeyActivity extends MainActivity implements OnItemClickListener
{
	protected ListView lvCategories;
	protected List<Category> listCategories;
	
	protected Dialog dlgKey;
	private Button btnDlgCancel;
	protected Button btnAddKey;
	protected EditText etKeyName;	
	protected Button btnCancel;

	protected IQRemote iqRemote;
	protected ImageView ivAddImage;
	protected CategorySpinnerAdapter categorySpinnerAdapter;

	public static final String IMAGE_FILE = "image_file";	
	protected Button btnKeyCategory;
	protected Button btnSave;
	protected Button btnDelete;
	protected TextView tvTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.key_layout);
		ivAddImage = (ImageView) findViewById(R.id.iv_add_key_image_);
		btnKeyCategory = (Button) findViewById(R.id.btn_key_category);
		etKeyName = (EditText) findViewById(R.id.et_key_id_);	
		btnSave = (Button) findViewById(R.id.btn_key_use_);
		btnDelete = (Button)  findViewById(R.id.btn_key_delete_cancel_);
		tvTitle = (TextView) findViewById(R.id.txt_key_title_);
		
		setDailog();
		loadCategories();
		//setImageFile();
		initSdk();
	}	

	protected void setKeyTitle(String title)
	{
		tvTitle.setText(title);
	}	

	private void loadCategories()
	{
		if(isOnline())
		{
			new CategoryListTask(this).execute();
		}
	}


	private void initSdk()
	{
		iqRemote = new  IQRemote(getResources().getString(R.string.iqe_app_key), 
				getResources().getString(R.string.iqe_app_secret));

	}
	private void setDailog()
	{
		View keyView = inflater.inflate(R.layout.key_dailog, null);
		dlgKey = new Dialog(this);		
		dlgKey.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgKey.setContentView(keyView);
		dlgKey.setCancelable(true);
		btnAddKey = (Button) keyView.findViewById(R.id.btn_key_dlg_add);
		btnDlgCancel = (Button) keyView.findViewById(R.id.btn_key_dlg_cancel);
		btnAddKey.setOnClickListener(this);
		btnDlgCancel.setOnClickListener(this);
		lvCategories = (ListView) keyView.findViewById(R.id.lv_dlg_keys);
		lvCategories.setOnItemClickListener(this);
		listCategories = new ArrayList<Category>();
		categorySpinnerAdapter = new CategorySpinnerAdapter(context, R.layout.spinner_view, listCategories);
		lvCategories.setAdapter(categorySpinnerAdapter);		
	}

	@Override
	public void onClick(View v) 
	{
		super.onClick(v);

		if(v == btnAddKey)
		{		
			addCategory();
		}
		else if (v == btnDlgCancel) 
		{
			dlgKey.dismiss();

		}
	}

	public void onClickKeyCancelDelete(View v)
	{
		finish();
	}

	public void addNewCategoryToRemote(String name)
	{
		if(isOnline())
		{
			KeyActivity.AddCategoryThread addCategoryThread = new KeyActivity.AddCategoryThread(name);
			addCategoryThread.start();
		}else {
			showLongToast("No internet connection.Please check your connection");
		}
	}

	public void addCategory()
	{
		final EditText input = new EditText(context);
		input.setHint("name");
		showCustomAlertDailog("Name?","Add","Cancel",input,new ICustomDailogClickListener() {

			@Override
			public void onOkClick(View customView) 
			{
				String name = (((EditText) customView).getText().toString());
				addNewCategoryToRemote(name);
				Log.i(TAG, name);
			}

			@Override
			public void onCancelClick(View customView) 
			{


			}
		});

	}

	protected void updateListCategory(List<Category> categories)
	{
		this.listCategories.clear();
		if(categories != null && categories.size() >0)
		{
			this.listCategories.addAll(categories);
			setCategoryButtonName(listCategories.get(0));
			categorySpinnerAdapter.notifyDataSetChanged();
		}
	
	}

	protected void setCategoryButtonName(final Category category)
	{
		btnKeyCategory.setText(category.getName());
		btnKeyCategory.setTag(category);
	}

	public void onClickSelectCategory(View v)
	{
		if(dlgKey != null)
		{
			dlgKey.show();
		}

	}
	public void onClickUse(View v)
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

			if(!activityRunning.get())
				return;

			if(progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}

			if(allCategoriesCommand != null)
			{
				List<Category> categories = allCategoriesCommand.getCategories();
				updateListCategory(categories);
			}
		}
	}

	private static String[] parseIqeAddResult(String result)
	{
		Log.i(TAG, result);
		String [] resultArray = new String[4];

		try {
			JSONObject jsonObject = new JSONObject(result);
			if(jsonObject.has("comment"))
			{
				resultArray[1] = jsonObject.getString("comment");			

			}
			if(jsonObject.has("obj_id"))
			{
				resultArray[0] = "SUCCESS";
				resultArray[2] = jsonObject.getString("obj_id");	
				if(jsonObject.has("resource_url"))
				{
					resultArray[3] = jsonObject.getString("resource_url");	
				}		
			}else {
				resultArray[0] = "ERROR";
			}


		} catch (JSONException e) 
		{
			Log.e(TAG, e.getMessage());
		}
		return resultArray;
	}

	private void onSuccessAddingKey(String message,boolean status)
	{
		showOkCancelAlertDailog(message, "Key Add", "Go to Home", "Add key Now", status, new IDailogOKClickListener() {

			@Override
			public void onOkClick() {

				goHome();

			}

			@Override
			public void onCancelClick() 
			{			
				finish();				
			}
		});
	}

	public class AddKeyTask extends AsyncTask<Void, Void, String>
	{

		private Context context;
		private ArrayList<File> imageFiles;
		private String name;
		private AddKeyCommand addKeyCommand;
		private int categoryId;
		private boolean isAddedSuccessfully;


		public AddKeyTask(Context context,ArrayList<File> imageFiles,String name,int categoryId) 
		{
			this.context = context;
			this.imageFiles = imageFiles;	
			this.name = name;
			this.categoryId = categoryId;
			this.isAddedSuccessfully = false;

		}
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			showProgressDaoilog(null, "Uploading..", true);
		}
		@Override
		protected String doInBackground(Void... params) 
		{
			String message = "Key Addding Error";
			try {
				String result = iqRemote.upload(imageFiles, name,null,null,true,null);
				String [] keyResultArray = parseIqeAddResult(result);


				if(keyResultArray != null && keyResultArray[0].equalsIgnoreCase("SUCCESS"))
				{
					addKeyCommand = new AddKeyCommand(keyResultArray[2], keyResultArray[3], name, categoryId);
					addKeyCommand.execute(ApiConnection.getInstance(context));
					isAddedSuccessfully = addKeyCommand.isAddedSuccessfully(); 
					message = addKeyCommand.getMessage();
				}else {
					message =  keyResultArray[1];
				}				
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			return message;
		}
		@Override
		protected void onPostExecute(String message) 
		{
			super.onPostExecute(message);
			hideProgressDaoilog();

			onSuccessAddingKey(message, isAddedSuccessfully);
			addKeyCommand = null;
		}

	}
	public static final int FAILED = 0;
	public static final int SUCCESS = 1;

	public class AddCategoryThread extends Thread
	{
		private CreateCategoryCommand createCategoryCommand;
		private String categoryName;

		public AddCategoryThread(String categoryName) 
		{
			super(categoryName);
			this.categoryName = categoryName;				
		}

		@Override
		public void run() 
		{
			createCategoryCommand = new CreateCategoryCommand(categoryName);
			createCategoryCommand.execute(ApiConnection.getInstance(context));
			if(createCategoryCommand.isCreatedSuccessfully())
			{
				listCategories.add(new Category(createCategoryCommand.getId(), categoryName));
				handler.sendEmptyMessage(SUCCESS);
			}else {
				handler.sendEmptyMessage(SUCCESS);
			}	

		}

	}

	public final Handler handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{

			switch (msg.what) {
			case FAILED:
				Toast.makeText(context, "Add Category Failed", Toast.LENGTH_SHORT).show();
				break;
			case SUCCESS:
				categorySpinnerAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}

		};

	};


	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id)
	{
		Category category = categorySpinnerAdapter.getItem(pos);
		if(dlgKey != null && dlgKey.isShowing())
			dlgKey.dismiss();
		setCategoryButtonName(category);

	}
}
