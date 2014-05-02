package com.rickyqiu.myAndroidPractice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import com.rickyqiu.myAndroidPractice.R;
import com.umeng.analytics.MobclickAgent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PhotoActivity extends Activity {

	private Button btn_take, btn_select;  
    private LinearLayout ll_show; 
    
	private static final int TAKE_PICTURE = 0;
	private ImageView imageView = null;
	private Button btn = null;
	
	// 请求  
    private static final int CAMERA_TAKE = 1;  
    private static final int CAMERA_SELECT = 2;  
  
    // 图片名  
    public String name;  
  
    // 存储路径  
    private static final String PATH = Environment.getExternalStorageDirectory() + "/DCIM/Camera";  
    private boolean isBig = false;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		
		btn_take = (Button) findViewById(R.id.btn_take);  
        btn_select = (Button) findViewById(R.id.btn_select);  
        ll_show = (LinearLayout) findViewById(R.id.ll_show);  
  
        btn_take.setOnClickListener(new OnClickListener() {  
  
            @Override  
            public void onClick(View v) {  
                takePhoto();  
            }  
        });  
  
        btn_select.setOnClickListener(new OnClickListener() {  
  
            @Override  
            public void onClick(View v) {  
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
                intent.setType("image/*");  
                startActivityForResult(intent, CAMERA_SELECT);  
            }  
        });     
	}

	protected void onResume() {  
        super.onResume();  
        Log.e("photoActivity", "onResume"); 
        MobclickAgent.onResume(this);
    }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        Log.e("photoActivity", "onPause"); 
        MobclickAgent.onPause(this);
    }  
    
	public void takePhoto() {  
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用系统相机  
        new DateFormat();  
        name = DateFormat.format("yyyyMMdd_hhmmss",  
                Calendar.getInstance(Locale.CHINA))  
                + ".jpg";  
        Uri imageUri = Uri.fromFile(new File(PATH, name));  
  
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);  
  
        startActivityForResult(intent, CAMERA_TAKE);  
    }  
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}
    	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
  
        if (resultCode == RESULT_OK) {  
            switch (requestCode) {  
            case CAMERA_TAKE:  
                Bitmap bitmap = BitmapFactory.decodeFile(PATH + "/" + name);  
                Toast.makeText(this, name, Toast.LENGTH_LONG).show();  
                System.out.println(bitmap.getHeight() + "======"  
                        + bitmap.getWidth());  
  
                // 获取屏幕分辨率  
                DisplayMetrics dm = new DisplayMetrics();  
                getWindowManager().getDefaultDisplay().getMetrics(dm);  
  
                // 图片分辨率与屏幕分辨率  
                float scale = bitmap.getWidth() / (float) dm.widthPixels;  
  
                Bitmap newBitMap = null;  
                if (scale > 1) {  
                    newBitMap = zoomBitmap(bitmap, bitmap.getWidth() / scale,  
                            bitmap.getHeight() / scale);  
                    bitmap.recycle();  
                    isBig = true;  
                }  
                //添加relative布局放置img，btn  
                final RelativeLayout rl_show_2 = new RelativeLayout(this);  
                rl_show_2.setLayoutParams(new LayoutParams(  
                        RelativeLayout.LayoutParams.WRAP_CONTENT,  
                        RelativeLayout.LayoutParams.WRAP_CONTENT));  
  
                ImageButton imgBtn_del_2 = new ImageButton(this);  
                imgBtn_del_2.setBackgroundResource(android.R.drawable.ic_delete);  
  
                //设置按钮的布局规则   
                RelativeLayout.LayoutParams rl_2 = new RelativeLayout.LayoutParams(  
                        new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,  
                                RelativeLayout.LayoutParams.WRAP_CONTENT));  
                rl_2.addRule(RelativeLayout.ALIGN_PARENT_TOP);  
                rl_2.addRule(RelativeLayout.ALIGN_RIGHT, 2);  
                  
                  
                // 将图片显示到界面  
                ImageView img = new ImageView(this);  
                img.setId(2);  
                img.setLayoutParams(new LayoutParams(  
                        LinearLayout.LayoutParams.WRAP_CONTENT,  
                        LinearLayout.LayoutParams.WRAP_CONTENT));  
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);  
                img.setPadding(2, 0, 0, 5);  
                if (isBig) {  
                    img.setImageBitmap(newBitMap);  
                    isBig = false;  
                } else  
                    img.setImageBitmap(bitmap);  
                  
                rl_show_2.addView(img);  
                rl_show_2.addView(imgBtn_del_2,rl_2);  
                  
                  
                ll_show.addView(rl_show_2);  
                  
                imgBtn_del_2.setOnClickListener(new OnClickListener() {  
                      
                    @Override  
                    public void onClick(View v) {  
                        ll_show.removeView(rl_show_2);  
                    }  
                });  
                  
                break;  
  
            case CAMERA_SELECT:  
                ContentResolver resolver = getContentResolver();  
  
                // 照片的原始资源地址  
                Uri imgUri = data.getData();  
  
                try {  
                    // 使用ContentProvider通过Uri获取原始图片  
                    Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,  imgUri);  
  
                    // 获取屏幕分辨率  
                    DisplayMetrics dm_2 = new DisplayMetrics();  
                    getWindowManager().getDefaultDisplay().getMetrics(dm_2);  
  
                    // 图片分辨率与屏幕分辨率  
                    float scale_2 = photo.getWidth() / (float) dm_2.widthPixels;  
  
                    Bitmap newBitMap_2 = null;  
                    if (scale_2 > 1) {  
                        newBitMap_2 = zoomBitmap(photo, photo.getWidth()  
                                / scale_2, photo.getHeight() / scale_2);  
                        photo.recycle();  
                        isBig = true;  
                    }  
  
                    final RelativeLayout rl_show = new RelativeLayout(this);  
                    rl_show.setLayoutParams(new LayoutParams(  
                            RelativeLayout.LayoutParams.WRAP_CONTENT,  
                            RelativeLayout.LayoutParams.WRAP_CONTENT));  
  
                    ImageButton imgBtn_del = new ImageButton(this);  
                    imgBtn_del.setBackgroundResource(android.R.drawable.ic_delete);  
  
                    //设置按钮的布局规则   
                    RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(  
                            new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,  
                                    RelativeLayout.LayoutParams.WRAP_CONTENT));  
                    rl.addRule(RelativeLayout.ALIGN_PARENT_TOP);  
                    rl.addRule(RelativeLayout.ALIGN_RIGHT, 1);  
  
                    // 将图片显示到界面  
                    ImageView img_2 = new ImageView(this);  
                    img_2.setId(1);  
                    img_2.setLayoutParams(new LayoutParams(  
                            LinearLayout.LayoutParams.WRAP_CONTENT,  
                            LinearLayout.LayoutParams.WRAP_CONTENT));  
                    img_2.setScaleType(ImageView.ScaleType.CENTER_CROP);  
                    img_2.setPadding(2, 0, 0, 5);  
                    if (scale_2 > 1) {  
                        img_2.setImageBitmap(newBitMap_2);  
                        isBig = false;  
                    } else  
                        img_2.setImageBitmap(photo);  
                      
                    //将img，btn添加  
                    rl_show.addView(img_2);  
                    rl_show.addView(imgBtn_del,rl);  
  
                    ll_show.addView(rl_show);  
  
                    imgBtn_del.setOnClickListener(new OnClickListener() {  
  
                        @Override  
                        public void onClick(View v) {  
                            ll_show.removeView(rl_show);  
                        }  
                    });  
  
                } catch (Exception e) {  
                    // TODO: handle exception  
                }  
  
                break;  
            }  
        }  
	}
	
     // 对分辨率较大的图片进行缩放  
    public Bitmap zoomBitmap(Bitmap bitmap, float width, float height) {  
  
        int w = bitmap.getWidth();  
        int h = bitmap.getHeight();  
        Matrix matrix = new Matrix();   
        float scaleWidth = ((float) width / w);  
        float scaleHeight = ((float) height / h);  
  
        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出  
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);  
  
        return newbmp;  
  
    }  
}
