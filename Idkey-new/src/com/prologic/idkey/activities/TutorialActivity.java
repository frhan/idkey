package com.prologic.idkey.activities;

import com.prologic.idkey.R;

import android.os.Bundle;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;

public class TutorialActivity extends MainActivity
{
	//private TextView tvTutorial;
	private WebView wvTutorial;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_layout);

		wvTutorial = (WebView) findViewById(R.id.wv_tutorial);
		final String mimeType = "text/html";
		final String encoding = "UTF-8";
		String html = "<h3>Not sure what all the keys on your keychain go to anymore? Too many keys to keep up with? </h3>"+
				"<p>ID-a-Key solves these problems by storing and identifying keys entered into your smartphone.  Entries are made using your phone’s built-in camera. Then, at any time or place thereafter, you can take a picture of an unknown key and the app will identify it for you.</p>"+
				"<p>There are several main functions of the ID-a-Key app:</p>"+
				"<ol>"+
				"<li>Creating an ID-a-Key account,</li>"+
				"<li>Adding additional key capability via in-app purchase,</li>"+
				"<li> Entering new keys into your phone,</li>"+
				"<li>Identifying a previously labeled key,</li>"+
				"<li>Viewing or searching within your key list,</li>"+
				"<li>Viewing your category list,</li></ol>"+
				"<h3>Creating an ID-a-Key Account</h3>"+
				"<p>The first time you open the ID-a-Key app, you will be prompted to:<br/>"+
				"a. accept the Five Tykes Technologies license agreement,<br/>"+ 
				"b. enter your email address (for purposes of identification of your key set), and <br/>"+
				"c. create a password for entry into the program. <br/></p>"+
				"<p>Once these steps are completed, you will be taken to the Home screen for further navigation.</p>"+
				"<p>Please save your login information. If you forget your password, you will have to create a new one which will be sent to you at your email address.<p>"+
				"<h3>Adding Keys via In-App Purchases </h3>"+
				"<p>Your purchase of the ID-a-Key app entitles you to enter 2 keys for storage and later identification."+
				"If you wish to purchase additional storage capability, select the &#34;My Account&#34; tab. You will see several options for adding keys."+
				"Once you make your selection, your payment will be processed through your iTunes or Google Play account.</p>"+
				"<h3>Entering New Keys</h3>"+
				"<p>Place your key flat on a white background with good lighting.Touch the &#34;Add New Key&#34; button either from the Home screen or from the Tab Menu.  Center the image within the key silhouette for best results, and take the picture (the flash is automatically suppressed to prevent distortion).Then choose either &#34;Use&#34; or &#34;Retake&#34;.Once the picture is selected, a Key Name and Category (optional) can be added.  Selecting &#34;Save&#34; sends the key image and details to your database for storage.<p>"+
				"<h3>Identifying a Key</h3>"+
				"<p>Place the key flat on a white background with good lighting.Touch the &#34;Identify a Key&#34; button either from the Home page or the Tab Menu.Once the key is centered in the key silhouette, take the picture.Then select either &#34;Use&#34; or &#34;Retake&#34;.When the &#34;Use&#34; button is selected, the app will search your database.  If the key has been previously entered, the app will identify it and display the associated label information.  If the key is not found, the app will allow the user to enter it into the system.</p>"+
				"<h3>Viewing Your List of Keys</h3>"+
				"<p>Select the &#34;Key Listing&#34; button from the Home screen or the Keys tab button to see a list of all stored keys.The default system lists keys by date of entry, though they can be organized and sorted by number, name, date and category.  There is also a search button that allows search by key name.</p>"+
				"<h3>Viewing Your List of Categories</h3>"+
				"<p>Keys entered into the app can be organized according to category.  Each key detail screen identifies the key’s category (optional), and the key list page displays category assignments.  In order to view the categories, touch the “Categories” button on the Home screen or the Key List page.  This will bring up all the Categories with a notation of how many keys are in each category.  Selecting a category enables  viewing of all the keys in that category.  Categories can be edited by selecting the &#34;Edit&#34; button on the Categories screen.  Touching the green &#34;Plus&#34; button adds a new category.  Selecting the category name brings up the “Edit Category Name” screen.  The red &#34;Delete&#34; button appears next to each category—selecting this button will delete the category.  (Please note that categories with keys in them cannot be deleted.  Similarly, the default &#34;Unassigned&#34; category cannot be edited/deleted.</p>";
		//tvTutorial.setText(Html.fromHtml(getResources().getString(R.string.tutorial_text)));
		StringBuilder sb = new StringBuilder();
		sb.append("<HTML><HEAD><LINK href=\"mtsheet.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>");
		sb.append(html.toString());
		sb.append("</body></HTML>");
		
		wvTutorial.loadDataWithBaseURL("file:///android_asset/", sb.toString(), mimeType, encoding, "");
	}

}
