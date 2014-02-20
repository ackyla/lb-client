package com.lb.ui;

import com.lb.Auth;
import com.lb.AuthDao;
import com.lb.DaoSession;
import com.lb.Intents;
import com.lb.R;
import com.lb.api.Authorization;
import com.lb.api.User;
import com.lb.api.client.LbClient;
import com.lb.model.Session;
import com.lb.model.Utils;
import com.lb.ui.user.GameActivity;
import com.lb.ui.user.MainActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignupActivity extends Activity implements OnClickListener {

    private ProgressDialog mProgressDialog;
    private EditText userNameInput;
    private AuthDao authDao;
    private Auth auth;

    public static Intent createIntent() {
        return new Intents.Builder("signup.VIEW").toIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userNameInput = (EditText) findViewById(R.id.editText1);
        Button button = (Button) findViewById(R.id.buttonRegister);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mProgressDialog = Utils.createProgressDialog(SignupActivity.this);

        LbClient client = new LbClient();
        client.createUser(userNameInput.getText().toString(), new Callback<Authorization>() {
            @Override
            public void success(Authorization authorization, Response response) {
                String url = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this).getString(PreferenceScreenActivity.PREF_KEY_DEBUG_MODE_URL, getResources().getString(R.string.server_url));
                Session session = (Session) getApplication();
                DaoSession daoSession = session.getDaoSession();
                authDao = daoSession.getAuthDao();
                auth = new Auth(null, authorization.getId(), authorization.getToken(), authorization.getName(), url);
                authDao.insert(auth);
                startGame(auth);
                mProgressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "登録に失敗しました!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startGame(final Auth auth) {
        mProgressDialog = Utils.createProgressDialog(this);

        LbClient client = new LbClient();
        client.setToken(auth.getToken());
        client.getUser(new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Session.setToken(auth.getToken());
                startActivity(MainActivity.createIntent(user));
                overridePendingTransition(0, 0);
                finish();
                mProgressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(getApplicationContext(), "プレイヤー情報を取得できませんでした", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
            }
        });
    }
}
