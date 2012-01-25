package osscafe.fbc.term;

import java.net.URL;

import org.json.JSONObject;

import osscafe.fc.term.R;
import osscafe.fc.term.R.id;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Facebook;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class IdentifyUserActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.identify_user);
		Button startBarCodeReader = (Button) findViewById(R.id.startBarCodeReader);
		startBarCodeReader.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentIntegrator.initiateScan(IdentifyUserActivity.this);
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult != null) {
			Log.d("IdentifyUserActivity", "scan = " + scanResult.getContents());
			if (scanResult.getContents().length() == 12) {
				readFacebook(scanResult.getContents().substring(2, 12));
			} else {
				Toast.makeText(this,
						"Illegal data, " + scanResult.getContents(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void readFacebook(String facebookId) {
		try {
			Facebook facebook = FCardTermActivity.facebook;
			String json = facebook.request(facebookId);
			JSONObject rootObject = new JSONObject(json);
			String name = rootObject.getString("name");
			TextView userName = (TextView) findViewById(id.userName);
			userName.setText(name);

			ImageView userpicture = (ImageView) findViewById(R.id.userPic);
			URL img_value = new URL("http://graph.facebook.com/" + facebookId
					+ "/picture?type=large");
			Bitmap mIcon1 = BitmapFactory.decodeStream(img_value
					.openConnection().getInputStream());
			userpicture.setImageBitmap(mIcon1);

		} catch (Exception e) {

		}
	}
}
