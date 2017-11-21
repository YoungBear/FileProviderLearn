package com.ysx.fileproviderserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String FILE_PROVIDER_AUTHORITIES
            = "com.ysx.fileproviderserver.fileprovider";

    @BindView(R.id.btn_share)
    Button mBtnShare;
    @BindView(R.id.btn_photo)
    Button mBtnPhoto;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                createFile();
            }
        }).start();

    }

    private void createFile() {
        File dir = new File(mContext.getFilesDir(), "text");
//        File dir = new File(mContext.getExternalFilesDir(null), "text");
        if (!dir.exists()) {
            boolean dirResult = dir.mkdirs();
            Log.d(TAG, "createFile: dirResult: " + dirResult);
        }
        File file = new File(dir, "hello.txt");
        if (!file.exists()) {
            try {
                boolean fileResult = file.createNewFile();
                Log.d(TAG, "createFile: fileResult: " + fileResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write("Hello World!".getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void shareFile() {
        Log.d(TAG, "shareFile: ");
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.ysx.fileproviderclient",
                "com.ysx.fileproviderclient.MainActivity");
        intent.setComponent(componentName);
        File file = new File(mContext.getFilesDir() + "/text", "hello.txt");
//        File file = new File(mContext.getExternalFilesDir(null) + "/text", "hello.txt");
//        File file = new File(Environment.getExternalStorageDirectory() + "/text", "hello.txt");
        Log.d(TAG, "shareFile: file.exists(): " + file.exists());
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(mContext, FILE_PROVIDER_AUTHORITIES, file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setData(data);
        startActivity(intent);
    }

    @OnClick({R.id.btn_share, R.id.btn_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_share:
                shareFile();
                break;
            case R.id.btn_photo:
                startActivity(new Intent(this, PhotoActivity.class));
                break;
            default:
                break;
        }
    }
}
