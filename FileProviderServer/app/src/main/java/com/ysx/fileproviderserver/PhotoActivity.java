package com.ysx.fileproviderserver;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author ysx
 * @date 2017/11/21
 * <p>
 * 调用系统照相机，指定照片存储路径
 */
public class PhotoActivity extends AppCompatActivity {
    private static final String TAG = "PhotoActivity";
    @BindView(R.id.btn_take_photo)
    Button mBtnTakePhoto;
    @BindView(R.id.iv_show)
    ImageView mIvShow;

    private static final String FILE_PROVIDER_AUTHORITIES
            = "com.ysx.fileproviderserver.fileprovider";

    private static final String TYPE = "text";

    private String mPhotoPath;
    private Uri mPhotoUri;

    private static final int REQ_CODE_PERMISSION = 1000;

    private static final int REQ_CODE_TAKE_PHOTO = 10;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        mContext = this;
        mPhotoPath = mContext.getExternalFilesDir(TYPE).getAbsolutePath() + File.separator + "hello.jpg";
        Log.d(TAG, "onCreate: mPhotoPath: " + mPhotoPath);
        createDir();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(PhotoActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Do not have the permission of camera, request it.
            ActivityCompat.requestPermissions(PhotoActivity.this,
                    new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_tips, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
                default:
                    break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: resultCode: " + resultCode + ", requestCode: " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_CODE_TAKE_PHOTO) {
                mIvShow.setImageBitmap(BitmapFactory.decodeFile(mPhotoPath));
            }
        }
    }

    @OnClick(R.id.btn_take_photo)
    public void onViewClicked() {

        File file = new File(mPhotoPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mPhotoUri = FileProvider.getUriForFile(mContext, FILE_PROVIDER_AUTHORITIES, file);
        } else {
            mPhotoUri = Uri.fromFile(file);
        }
        takePicture(PhotoActivity.this, mPhotoUri, REQ_CODE_TAKE_PHOTO);



    }

    private void createDir() {
        File dir = new File(mContext.getExternalFilesDir(TYPE).getAbsolutePath());
        if (!dir.exists() || !dir.isDirectory()) {
            boolean mkdirs = dir.mkdirs();
            Log.d(TAG, "createDir: photoDir created: " + mkdirs);
        }
    }

    /**
     * @param activity    当前activity
     * @param imageUri    拍照后照片存储路径
     * @param requestCode 调用系统相机请求码
     */
    public static void takePicture(Activity activity, Uri imageUri, int requestCode) {
        Intent intentCamera = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intentCamera, requestCode);
    }
}
