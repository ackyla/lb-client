package com.lb.ui;

import java.util.ArrayList;
import java.util.List;

import com.lb.Auth;
import com.lb.AuthDao;
import com.lb.DaoSession;
import com.lb.Intents;
import com.lb.R;
import com.lb.api.User;
import com.lb.api.client.LbClient;
import com.lb.model.Session;
import com.lb.model.Utils;
import com.lb.ui.user.GameActivity;
import com.lb.ui.user.MainActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TitleActivity extends ActionBarActivity {

    private ProgressDialog mProgressDialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getResources().getBoolean(R.bool.debug_mode)) {
            getMenuInflater().inflate(R.menu.title, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
            default:
                Intent intent = new Intent();
                intent.setClass(TitleActivity.this,
                        PreferenceScreenActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        Session session = (Session) getApplication();
        DaoSession daoSession = session.getDaoSession();
        final AuthDao authDao = daoSession.getAuthDao();

        Button bt2 = (Button) findViewById(R.id.bt_debug_user);
        Button bt3 = (Button) findViewById(R.id.bt_signup);

        if (getResources().getBoolean(R.bool.debug_mode)) {
            if (authDao.count() > 0) {
                List<Auth> authList = authDao.loadAll();
                List<String> users = new ArrayList<String>();
                for (int i = 0; i < authList.size(); i++) {
                    Auth auth = authList.get(i);
                    users.add(auth.getUrl() + ", " + auth.getName());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("使用するユーザを選択して下さい");
                builder.setSingleChoiceItems(users.toArray(new String[users.size()]), 0, new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Auth auth = authDao.loadAll().get(which);
                        startGame(auth);
                        dialog.dismiss();
                    }

                });
                final AlertDialog userDialog = builder.create();

                bt2.setVisibility(View.VISIBLE);
                bt2.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        userDialog.show();
                    }

                });
            }

            bt3.setVisibility(View.VISIBLE);
            bt3.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(SignupActivity.createIntent());
                    finish();
                }

            });

        } else {
            // ユーザ登録してる時(データベースにユーザ情報がある時)はゲーム画面に移動，ないときだけ登録画面へのボタン表示
            if (authDao.count() > 0) {
                Auth auth = authDao.loadAll().get((int) authDao.count() - 1);
                startGame(auth);
            } else {
                bt3.setVisibility(Button.VISIBLE);
                bt3.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(SignupActivity.createIntent());
                        finish();
                    }

                });
            }
        }
    }

    private void startGame(final Auth auth) {
        mProgressDialog = Utils.createProgressDialog(TitleActivity.this);

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
