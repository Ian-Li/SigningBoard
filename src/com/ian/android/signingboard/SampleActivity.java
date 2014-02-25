package com.ian.android.signingboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ian.android.signingboard.widget.SigningBoardView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * 签名板控件示例
 * 
 * @author Ian(https://github.com/Ian-Li)
 * @version 1.0
 * 
 */
public class SampleActivity extends Activity implements View.OnClickListener {

    private SigningBoardView sbSign;
    private Button bSaveSign;
    private Button bClearSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        sbSign = (SigningBoardView) findViewById(R.id.sbSign);
        bSaveSign = (Button) findViewById(R.id.bSaveSign);
        bClearSign = (Button) findViewById(R.id.bClearSign);

        bSaveSign.setOnClickListener(this);
        bClearSign.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int bId = v.getId();
        switch (bId) {
        case R.id.bSaveSign:
            String signPath = getSignPath(this, "sign.png");
            boolean isSaved = saveSign(sbSign.getSignDrawable(), signPath);

            Toast.makeText(this, isSaved ? "签名保存成功,保存路径：" + signPath : "签名保存失败",
                    Toast.LENGTH_LONG).show();
            break;
        case R.id.bClearSign:
            clearSign();
            break;
        }
    }

    /**
     * 保存签名图像
     * 
     * @param bitmap
     *            签名位图
     * @param path
     *            保存路径
     * @return true,保存成功；false,保存失败
     * 
     */
    private boolean saveSign(Bitmap bitmap, String path) {
        FileOutputStream fOutput = null;
        try {
            fOutput = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fOutput);
        } catch (FileNotFoundException e) {
            return false;
        } finally {
            if (fOutput != null)
                try {
                    fOutput.close();
                } catch (IOException e) {
                }
        }

        return true;
    }

    /**
     * 清空签名
     * 
     */
    private void clearSign() {
        sbSign.clear();
    }

    /**
     * 获取签名图像保存路径
     * 
     * @param context
     * @param fileName
     *            签名图像文件名
     * @return 签名图像路径
     * 
     */
    private String getSignPath(Context context, String fileName) {
        String path = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File dir = context.getExternalFilesDir("sign");
            if (dir.exists()) {
                File f = new File(dir.getAbsolutePath() + File.separator + fileName);
                try {
                    if (f.exists())
                        f.delete();

                    f.createNewFile();
                    path = f.getAbsolutePath();
                } catch (IOException e) {
                }
            }
        }
        return path;
    }
}
