package com.melnykov.fab.sample.shibie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.intsig.openapilib.OpenApi;
import com.intsig.openapilib.OpenApiParams;
import com.melnykov.fab.sample.R;

public class crmmpw_recognize extends Activity {

	OpenApi openApi = OpenApi.instance("Jg7Er8Ky9We3gaSJbL1V3d0b");
	OpenApiParams params = new OpenApiParams() {
		{
			this.setRecognizeLanguage("");
			this.setReturnCropImage(true);
			this.setSaveCard(true);
		}
	};

	private static final int REQUEST_CODE_RECOGNIZE = 0x1001;
	private void link() {
// TODO Auto-generated method stub
		String url =  openApi.getDownloadLink(); // web address
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crm_activity_main);

		Button btn = (Button) findViewById(R.id.button2);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				link();
			}
		});

		ImageView back = (ImageView) findViewById(R.id.iv_back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				crmmpw_recognize.this.finish();
			}
		});

		testRecognizeCapture();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		crmmpw_recognize.this.finish();
	}

	public void testRecognizeCapture() {
		if(openApi.isCamCardInstalled(this)){
			if ( openApi.isExistAppSupportOpenApi(this) ){
				openApi.recognizeCardByCapture(this, REQUEST_CODE_RECOGNIZE, params);

				Log.e("名片王地址", "camcard download link:" + openApi.getDownloadLink());
			}else{
				Toast.makeText(this, "No app support openapi", Toast.LENGTH_LONG).show();
				System.out.println("camcard download link:"+openApi.getDownloadLink());
			}
		}else{
			//Toast.makeText(this, "No CamCard", Toast.LENGTH_LONG).show();
			Toast.makeText(this, "请根据此链接下载名片全能王" + openApi.getDownloadLink(), Toast.LENGTH_SHORT).show();
			System.out.println("camcard download link:"+openApi.getDownloadLink());
		}
	}

	public void testRecognizeImage(String path) {
		if ( openApi.isExistAppSupportOpenApi(this) ){
			openApi.recognizeCardByImage(this, path, REQUEST_CODE_RECOGNIZE, params);
		}	else {
			Toast.makeText(this, "No app support openapi", Toast.LENGTH_LONG).show();
			System.out.println("camcard download link:"+openApi.getDownloadLink());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_RECOGNIZE) {
				showResult(data.getStringExtra(OpenApi.EXTRA_KEY_VCF),
						data.getStringExtra(OpenApi.EXTRA_KEY_IMAGE));
			}
		} else {
			int errorCode=data.getIntExtra(openApi.ERROR_CODE, 200);
			String errorMessage=data.getStringExtra(openApi.ERROR_MESSAGE);
			System.out.println("ddebug error " + errorCode+","+errorMessage);
			Toast.makeText(this, "Recognize canceled/failed. + ErrorCode " + errorCode + " ErrorMsg " + errorMessage,
					Toast.LENGTH_LONG).show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showResult(String vcf, String path) {
		Intent intent = new Intent(this, crmShowmpwActivity.class);
		intent.putExtra("result_vcf", vcf);
		intent.putExtra("result_trimed_image", path);
		startActivity(intent);
		crmmpw_recognize.this.finish();
	}
}
