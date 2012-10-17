package com.zhai.work;

import com.zhai.work.utils.Constant;
import com.zhai.work.utils.URLUtil;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class ResultInfoResultActivity extends BaseActivity {
	private WebView resultWebView;
	private TextView back;
	private TextView refresh;
	private String mUrl="http://www.zhengzhoubus.com/index.aspx";
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.result_info);
		if(getIntent()!=null){
			if(getIntent().getStringExtra(Constant.TYPE)!=null&&getIntent().getStringExtra(Constant.KEYWORD)!=null){
				String type =getIntent().getStringExtra(Constant.TYPE);
				String keyword=getIntent().getStringExtra(Constant.KEYWORD);
				mUrl=URLUtil.setUrl(type, keyword);
			}
		}
		init();
	}

	private void init() {
		back=(TextView)this.findViewById(R.id.top_back);
		back.setOnClickListener(this);
		refresh=(TextView)this.findViewById(R.id.top_refresh);
		refresh.setOnClickListener(this);
		resultWebView=(WebView)this.findViewById(R.id.result_webview);
		resultWebView.setWebViewClient(new WebViewClient(){       
            public boolean shouldOverrideUrlLoading(WebView view, String url) {       
                view.loadUrl(url);       
                return true;       
            } 
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(ResultInfoResultActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
              }
		});   
		WebSettings webSettings = resultWebView.getSettings();       
        webSettings.setJavaScriptEnabled(true);  
        dialog();
		resultWebView.loadUrl(mUrl);
		resultWebView.setWebChromeClient(new WebChromeClient() {
			   public void onProgressChanged(WebView view, int progress) {
			     // Activities and WebViews measure progress with different scales.
			     // The progress meter will automatically disappear when we reach 100%
			     ResultInfoResultActivity.this.setProgress(progress * 1000);
			     dismissDialog();
			   }
			 });
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && resultWebView.canGoBack()) {
			resultWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back:
			if ( resultWebView.canGoBack()) {
				resultWebView.goBack();
			}else{
				finish();
			}
//			finish();
			break;
		case R.id.top_refresh:
			resultWebView.reload();
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	/**
	 * 取消等待加载圈
	 */
	private void dismissDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog=null;
		}
	}

	/**
	 * 显示加载对话框
	 */
	private void dialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.loadTitle) + ","
				+ getString(R.string.LoadContent));
		progressDialog.show();
	}
}
