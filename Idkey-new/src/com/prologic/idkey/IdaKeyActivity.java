package com.prologic.idkey;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.iqengines.sdk.IQE;
import com.iqengines.sdk.IQE.OnResultCallback;
import com.prologic.idkey.activities.MainActivity;

public class IdaKeyActivity extends MainActivity {

	static final String KEY = "4cb870a67b43493aa668e94ca9095b18";	
	static final String SECRET = "c001b534b5f34f788653cf26c2ea9172";
	private Handler handler;

	static final boolean SEARCH_OBJECT_REMOTE = true;
	static IQE iqe;
	private AtomicBoolean activityRunning = new AtomicBoolean(false);
	private QueryProgressDialog pd;
	private Runnable postponedToastAction;

	private String lastPostedQid = null;

	// Maximum duration of a remote search.
	static final long REMOTE_MATCH_MAX_DURATION = 10000;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		handler = new Handler();
		
		initIqSdk();

	}


	private void freezePreview() {
		// on old device freezing preview only shows a black screen
		Log.d(TAG, "preview is freezed");
		//preview.stopPreview();

	}

	private void unfreezePreview() {
		//preview.startPreview();

	}

	private void initIqSdk() {
		iqe = new IQE(this, SEARCH_OBJECT_REMOTE,
				onResultCallback, KEY, SECRET);
	}

	private class QueryProgressDialog extends ProgressDialog {

		private boolean isShowing;

		public QueryProgressDialog(Activity activity) {
			super(activity);
			this.isShowing=false;
		}

		public void pdShowing(){
			this.isShowing=true;
		}

		public void pdDismissed(){
			this.isShowing=false;
		}

		public boolean isShowing(){
			return isShowing;
		}

	}

	public QueryProgressDialog showCenteredProgressDialog(final String msg) {

		pd.setCanceledOnTouchOutside(false);
		pd.setCancelable(false);
		pd.show();
		pd.pdShowing();
		handler.post(new Runnable() {
			@Override
			public void run() {
				pd.setMessage(msg);
			}
		});
		return pd;

	}

	private void processImageSnap(final YuvImage yuv) {

		postponedToastAction = new Runnable() {
			public void run() {
				if (SEARCH_OBJECT_REMOTE) {
					Toast.makeText(
							IdaKeyActivity.this,
							"This may take a minute... We will notify you when your photo is recognized.",
							Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
				pd.pdDismissed();
				unfreezePreview();

			}
		};
		handler.postDelayed(postponedToastAction, REMOTE_MATCH_MAX_DURATION);

		Log.d(TAG," snap decode message");
		iqe.sendMessageAtFrontOfQueue(iqe.obtainMessage(IQE.CMD_DECODE, IQE.snap, 0, yuv));
	}

	private OnResultCallback onResultCallback = new OnResultCallback() {

		/**
		 * Called whenever a query is done (In the demo app, we call it on every
		 * snap queries and on Successful scan queries)
		 * 
		 * @param queryId
		 *            A {@link String}, the unique Id of the query.
		 * @param path
		 *            A {@link String}, the path of the picture associated with
		 *            the query.
		 * @param callType
		 *            An {@link Integer}, defines if it's a snap or a scan call.
		 */

		@Override
		public void onQueryIdAssigned(String queryId, String path, int callType) {

			switch (callType) {


			case (IQE.snap):

				if (SEARCH_OBJECT_REMOTE) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							pd.setMessage("Searching...");
						}
					});
				}
			break;
			}
		}

		/**
		 * Handle the results.
		 * 
		 * @param queryId
		 *            A {@link String}, the unique Id of the query.
		 * @param objId
		 *            A {@link String}, the unique Id identifying the object on
		 *            our server. * @param objId A {@link String}, the object
		 *            label. * @param objId A {@link String}, the object
		 *            metadata. * @param objId A {@link Integer}, determines
		 *            which engine made the match (barcode, local, remote).
		 * @param callType
		 *            An {@link Integer}, defines if it's a snap or a scan call.
		 */

		@Override
		public void onResult(String queryId, String objId, String objName,
				String objMeta, int engine, final int callType) {

			final String qId = queryId;
			final String oNm = objName;


			//if it is a remote match

			if (queryId.equals(lastPostedQid)) {
				handler.removeCallbacks(postponedToastAction);
			}
			Uri uri = null;
			// match's Metadata set as URI.
			if (objMeta != null) {

				try {
					uri = Uri.parse(objMeta);
				} catch (Exception e1) {
					uri = null;
				}
			}
			// if no Metadata : match's name set as URI.
			if (uri == null) {

				if (objName != null) {

					try {
						uri = Uri.parse(objName);
					} catch (Exception e1) {
						uri = null;
					}
				}
			}
			final Uri fUri = uri;
			handler.post(new Runnable() {
				@Override
				public void run() {
					// process and display the results
					processSearchResult(qId, oNm, fUri, callType);
				}
			});
		}
		
		private void processSearchResult(String searchId, String label, Uri uri,
				final int callType) {

		}


		/**
		 * When no match are found, or exception occurs.
		 * 
		 * 
		 */

		@Override
		public void onNoResult(int callType, Exception e, File imgFile) {

			// if an exception occured
			if (e != null) {
				if (e instanceof IOException) {
					Log.w(TAG, "Server call failed", e);
					handler.post(new Runnable() {
						@Override
						public void run() {
							handler.removeCallbacks(postponedToastAction);
							Toast.makeText(
									IdaKeyActivity.this,
									"Unable to connect to the server. "
											+ "Check your intenet connection.",
											Toast.LENGTH_LONG).show();
							pd.dismiss();
							pd.pdDismissed();
							unfreezePreview();
						}
					});
				} else {
					Log.e(TAG, "Unable to complete search", e);
				}
				return;
			}
			// if just nothing found
			switch (callType) 
			{

			case (IQE.snap):
				//displayResult(null, IQE.snap, imgFile);
			break;
			}
		}
	};

}
