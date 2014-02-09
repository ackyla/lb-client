package com.lb.ui;

import static com.lb.Intents.EXTRA_USER;
import static com.lb.RequestCodes.SELECT_PICTURE;
import com.lb.Intents;
import com.lb.R;
import com.lb.api.User;
import com.lb.api.client.LbClient;
import com.lb.model.Session;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SetAvatarActivity extends ActionBarActivity {

    private static final int AVATAR_WIDTH = 200;
    private static final int AVATAR_HEIGHT = 200;
    private ProgressDialog mProgressDialog;
    private ImageView ivAvatar;
    private User user;

    public static Intent createIntent(User user) {
        return new Intents.Builder("set.avatar.VIEW").user(user).toIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_avatar);
        setTitle("アバター設定");

        View v = (View) findViewById(R.id.ll_avatar);
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        user = (User) getIntent().getExtras().getSerializable(EXTRA_USER);

        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        if (user != null) Picasso.with(this).load(user.getAvatar()).into(ivAvatar);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode || data == null) return;

        switch (requestCode) {
            case SELECT_PICTURE:
                Uri uri = data.getData();
                Picasso.with(this).load(uri).resize(AVATAR_WIDTH, AVATAR_HEIGHT).centerCrop().into(target);
                return;
        }

    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            ivAvatar.setImageBitmap(bitmap);
            try {
                final File tempFile = File.createTempFile("avatar", ".jpeg");
                LbClient client = new LbClient();
                client.setToken(Session.getToken());
                client.updateAvatar(bitmap, tempFile, new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        if (tempFile.exists()) tempFile.delete();
                        Toast.makeText(SetAvatarActivity.this, "アバターを変更しました", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        if (tempFile.exists()) tempFile.delete();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {

        }

        @Override
        public void onPrepareLoad(Drawable drawable) {

        }
    };
}

            /*
            final ImageView iv = (ImageView) findViewById(R.id.iv_avatar);
            ImageLoader loader = ImageLoader.getInstance();
            ImageSize imageSize = new ImageSize(AVATAR_WIDTH, AVATAR_HEIGHT);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();
            loader.loadImage(uri.toString(), imageSize, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    Log.i("dump", "uri="+imageUri);
                }
            });*/
                /*
                @Override
                public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
					API.updateAvatar(Session.getUser(), ThumbnailUtils.extractThumbnail(loadedImage, AVATAR_WIDTH, AVATAR_HEIGHT), new JsonHttpResponseHandler() {
                        @Override
						public void onStart() {
							if (mProgressDialog == null) mProgressDialog = Utils.createProgressDialog(SetAvatarActivity.this);
							mProgressDialog.show();
						}
						
						@Override
						public void onSuccess(JSONObject json) {
                            try {
                                User user = UserGen.get(json.toString());
                                ImageLoader loader = ImageLoader.getInstance();
                                DisplayImageOptions options = new DisplayImageOptions.Builder()
                                        .cacheInMemory(true)
                                        .cacheOnDisc(true)
                                        .build();
                                loader.displayImage(user.getAvatar(), iv, options);
                                Toast.makeText(SetAvatarActivity.this, "アバターを変更しました", Toast.LENGTH_LONG).show();
                                Utils.updateSessionUserInfo(user);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JsonFormatException e) {
                                e.printStackTrace();
                            }
                        }

						@Override
						public void onFailure(Throwable throwable) {
							Toast.makeText(SetAvatarActivity.this, "アバターを変更できませんでした", Toast.LENGTH_LONG).show();
							Log.i("game","updateAvatarOnFailure="+ throwable);
						}
						
						@Override
						public void onFinish() {
							mProgressDialog.dismiss();
						}
					});

                }
            });
    */

