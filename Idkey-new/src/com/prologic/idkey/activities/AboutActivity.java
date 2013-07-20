package com.prologic.idkey.activities;

import com.prologic.idkey.R;

import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends MainActivity {

	private WebView wvAbout;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	

		setContentView(R.layout.about_us);

		wvAbout = (WebView) findViewById(R.id.wv_about);
		final String mimeType = "text/html";
		final String encoding = "UTF-8";
		
		String html = "<p>Five Tykes Technologies, LLC is a company dedicated to solving everyday problems"+
				"through smartphone applications. It is based in Dallas, Texas.</p>"+
				"<p>Five Tykes Technologies developed ID-a-key to help people label and"+
				" categorize their keys within memory that can be accessed by their smartphone."+
				"Thus, users will never have to ask “what is this key for?” again."+
				"By enabling someone to snap an image of a key, label and categorize it, and then later identify the key by photographing it again, customers will never lose track of what their keys go to."+
				"In addition, keys can be identified anytime and from anywhere a Wi-Fi connection exists to the smartphone.</p>"+
				"<p>Five Tykes Technologies welcomes customer feedback. We can be reached by mail at P.O. Box 25578, Dallas, TX 75225, or via the ID-a-Key website at ID-a-key.com.</p>";
		
		StringBuilder sb = new StringBuilder();
		sb.append("<HTML><HEAD><LINK href=\"mtsheet.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>");
		sb.append(html.toString());
		sb.append("</body></HTML>");
		
		wvAbout.loadDataWithBaseURL("file:///android_asset/", sb.toString(), mimeType, encoding, "");
	}
}
