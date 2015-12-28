package com.example.buchiosan.loginsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*-- facebook SDK --*/
        final MainActivity activity = this;
        //1.onClick内にログインのパーミッションを設定(LoginButtonを使用しない場合)
        Button btnFacebook = (Button) findViewById(R.id.login_btnFacebook);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.1 パーミッション設定
                Log.d("login", "facebookLogin");
                LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));    //profileとemailの情報を取得
            }
        });

        //2.facebook利用する時のInitialize
        FacebookSdk.sdkInitialize(this);

        //3.コールバック登録
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //3.1 ログイン成功
                        Log.d("login", "success");
                        loginData(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        //3.2 ログインキャンセル
                        Log.d("login", "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        //3.3 ログイン失敗
                        Log.d("login", "error");
                        Log.e("login", exception.toString());
                    }
                });
       /*-- /facebook SDK --*/
    }

    private void loginData(LoginResult loginResult) {
        //1.AccessToken取得し、Requestを作る
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            //1.1 コールバックメソッド。Jsonで結果が返ってきます。
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("loginData", "respons" + response.toString());
                // Get facebook data from login
                setProfile(object);
            }
        });

        //2.取得する情報を羅列,実行
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email"); // Parámetros que pedimos a facebook
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void setProfile(JSONObject object) {
        try {
            String id = object.getString("id");
            String name = object.getString("name");
            String email = object.getString("email");

            TextView nameView = (TextView)findViewById(R.id.name_text);
            nameView.setText(name);

            TextView emailView = (TextView)findViewById(R.id.email_text);
            emailView.setText(email);

            setImage(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setImage(String id) {
        ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.thumbnail_image);
        profilePictureView.setProfileId(id);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
