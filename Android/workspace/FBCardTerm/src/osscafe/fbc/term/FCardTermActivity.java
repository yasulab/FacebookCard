package osscafe.fbc.term;

import osscafe.fc.term.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FCardTermActivity extends Activity {
	private static final int FACEBOOK_FORM = 1;
	private static final int IDENTITY_USER_FORM = 2;
	public static Facebook facebook = new Facebook("370240069656817");
	String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button identifyUser = (Button)findViewById(R.id.identifyUser);
		identifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FCardTermActivity.this, IdentifyUserActivity.class);  
                //intent.putExtra("MAIN_FORM_TEXT", text);  
                FCardTermActivity.this.startActivityForResult(intent, IDENTITY_USER_FORM);            
            }
        });
		
		/*
		 * Get existing access_token if any
		 */
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		/*
		 * Only call authorize if the access_token has expired.
		 */
		if (!facebook.isSessionValid()) {

			facebook.authorize(this, new String[] {}, FACEBOOK_FORM, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires",
							facebook.getAccessExpires());
					editor.commit();
				}

				@Override
				public void onFacebookError(FacebookError error) {
				}

				@Override
				public void onError(DialogError e) {
				}

				@Override
				public void onCancel() {
				}
			});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FACEBOOK_FORM) {
			facebook.authorizeCallback(requestCode, resultCode, data);
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		facebook.extendAccessTokenIfNeeded(this, null);
	}
}