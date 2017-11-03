package org.androidtown.materialpractice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class CaptureActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceListener);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                takePicture();
                finish();
            }
        };
        timer.schedule(timerTask,1000);
    }

    public SurfaceHolder.Callback surfaceListener = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //카메라 오픈
                camera = android.hardware.Camera.open(1);
                camera.setDisplayOrientation(90);
                 try{
                    camera.setPreviewDisplay(surfaceHolder);
                }catch(Exception e){
                    e.printStackTrace();
                }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            try{
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(640,480);
                parameters.setPictureSize(640,480);
                camera.startPreview();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    };

    public void takePicture()
    {
        if(camera != null)
        {
            camera.takePicture(null,null,takePicture);
        }
    }

    public Camera.PictureCallback takePicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
            bitmap = imgRotate(bitmap);

            if(bitmap == null)
                Log.v("비트맵","널");

            String ext = Environment.getExternalStorageDirectory().toString();
            ext += "/CHECK";

            File folder = new File(ext);
            folder.mkdirs();

            try {
                FileOutputStream out = new FileOutputStream(ext+"TEST.jpg");
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,out);
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private Bitmap imgRotate(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        bmp.recycle();

        return resizedBitmap;

    }

}
