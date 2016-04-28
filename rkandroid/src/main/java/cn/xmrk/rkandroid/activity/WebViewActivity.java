package cn.xmrk.rkandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cn.xmrk.rkandroid.R;


public class WebViewActivity extends BackableBaseActivity {

	public static final String REQUEST_EXTRA_TITLE = "extraTitle";
	public static final String REQUEST_EXTRA_URL = "extraUrl";
	public static final String REQUEST_EXTRA_SHARE = "extraShare";

	protected WebView wv;

	protected String url;

	protected String title;

	public static void start(Activity activity, String title, String url, boolean share) {
		Intent _intent = new Intent(activity, WebViewActivity.class);
		_intent.putExtra(REQUEST_EXTRA_TITLE, title);
		_intent.putExtra(REQUEST_EXTRA_SHARE, share);
		_intent.putExtra(REQUEST_EXTRA_URL, url);
		activity.startActivity(_intent);
	}

	@Override
	public void onBackPressed() {
		if (wv != null && wv.canGoBack()) {
			wv.goBack();
		} else {
			super.onBackPressed();
		}
	}

	private boolean startTypeActivity(String url) {
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		wv = (WebView) findViewById(R.id.wv);

//		Button _leftBtn = getTitleLeftBtn();
//		_leftBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_delete_b, 0);
//		getTitleLeftBtn().setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});

		Intent intent = getIntent();
		url = intent.getStringExtra(REQUEST_EXTRA_URL);
		title = intent.getStringExtra(REQUEST_EXTRA_TITLE);
		setTitle(title);

		boolean isShare = intent.getBooleanExtra(REQUEST_EXTRA_SHARE, false);

		wv.loadUrl(url);

//		wv.setWebViewClient(new WebViewClient() {
//			@Override
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				if (startTypeActivity(url)) {
//					// finish();
//				} else {
//					view.loadUrl(url);
//				}
//				return true;
//			}
//
//		});

		wv.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				if (title == null) {
					setTitle(title);
				}
			}

		});

		WebSettings setting = wv.getSettings();
		setting.setJavaScriptEnabled(true);
		setting.setJavaScriptCanOpenWindowsAutomatically(false);
		setting.setSupportMultipleWindows(false);
		setting.setDisplayZoomControls(false);
		setting.setSupportZoom(true);
		setting.setBuiltInZoomControls(true);
		setting.setUseWideViewPort(true);
		setting.setLoadWithOverviewMode(true);
		wv.setInitialScale(1);

	}

	@Override
	protected void onDestroy() {
		((ViewGroup) wv.getParent()).removeView(wv);
		wv.destroy();
		super.onDestroy();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// super.onCreateOptionsMenu(menu);
	// menu.add(0, 0, 0, R.string.open_with_browser);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// if (item.getItemId() == 0) {
	// CommonUtil.openLink(wv.getUrl());
	// finish();
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

}
