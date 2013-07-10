/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Hand.Writer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wade
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener, Camera.PreviewCallback
{
    Camera camera;
    SurfaceHolder mHolder;
    Canvas canvas;
    Bitmap tempImage;
    public static Handwriter activity = Handwriter.main;
    boolean pictureTaken = false;
    
    public CameraView(Context context)
    {
        super(context);
        
	mHolder = this.getHolder();
	mHolder.addCallback(this);
        //mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        this.setOnTouchListener(this);   
    } 
  
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {

            camera=Camera.open();

            try
            {
                camera.setPreviewDisplay(mHolder);
            }

            catch (Throwable t)
            {
                Log.e("PictureDemo-surfaceCallback","ExceptioninsetPreviewDisplay()", t);
            }

    }
    
    public void surfaceChanged(SurfaceHolder holder,int format, int width, int height) 
    {

            Camera.Parameters parameters=camera.getParameters();
            parameters.setPreviewSize(width, height);
            parameters.setPictureFormat(PixelFormat.JPEG);
            //parameters.setPictureSize(100,100);
            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            
            camera.setParameters(parameters);

            camera.startPreview();

            camera.autoFocus(new Camera.AutoFocusCallback()
            {
                @Override
                public void onAutoFocus(boolean success, Camera camera) 
                {
                    //Log.d("HOME", "isAutofoucs " +Boolean.toString(success));
                }
            } );

        }


    public void surfaceDestroyed(SurfaceHolder sh) 
    {
           if(camera != null)
           {
                camera.stopPreview();
                camera.release();
                camera=null;
            }
    }
    
    private void pictureTake()
    { 
        if(camera!=null)
        {
            camera.takePicture(null, null, rawCallback); 
        }
    }
      
    private void scaleMod(Bitmap map)
    {
        int divisor = 3;
        
        float divWidth = ((float)map.getWidth()/divisor)/(float)map.getWidth();
        float divHeight = ((float)map.getHeight()/divisor)/(float)map.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(divWidth, divHeight);
        tempImage = Bitmap.createBitmap(map,0,0,map.getWidth(),map.getHeight(),matrix,false);
        
        
    }
    
    
    Camera.PictureCallback rawCallback = new Camera.PictureCallback()
    {
          public void onPictureTaken(byte[] _data, Camera _camera) 
          {
              //Log.d("IMAGE","THIS IS BEING RUN, THE PICTURE WORKS!");
              
              tempImage = BitmapFactory.decodeByteArray(_data, 0, _data.length);
              
                scaleMod(tempImage);
                
              
                AhaView imgView = new AhaView(activity);
                imgView.setImage(tempImage);
                activity.setContentView(imgView);
          }
    };
        
    public boolean onTouch(View view, MotionEvent me) 
    {
        if(!pictureTaken)
        {
            //Log.d("IMAGE","THIS IS BEING RUN NUMBER ONE");
            pictureTaken = true;
            camera.stopPreview();
            pictureTake();
            //Log.d("IMAGESTATUS","THIS IS BEING RUN (ON TOUCH THAT IS)");
        }

        
        return true;
    }

    public void onPreviewFrame(byte[] bytes, Camera camera) 
    {
        //throw new UnsupportedOperationException("Not supported yet.");
        
        
        
    }

    
    
    

    
}
