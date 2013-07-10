/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Hand.Writer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 *
 * @author Wade
 */
public class AhaView extends View
{
    public Bitmap usedImage;
    Bitmap tempImage;
    int[][] mapArray;
    public AhaView(Context context)
    {
        super(context);
        
         Log.d("TAG","AHA VIEW HAS STARTED THIS");
              
         for(int k = 0 ; k < 8 ; k++)
         {
             float mul = (float) ((float)k*45/180*Math.PI);

             int Xup = (int) Math.round(Math.sin(mul));
             int Yup = (int) Math.round(Math.cos(mul));
             //Log.d("DIRECTIONS", "XY: "+ Xup + " : "+Yup+" : "+Math.round(Math.sin(mul)));
         }
    }
    
    private int sine(double input)
    {
        if(input == Math.abs(input))
        {
            return 1;
        }
        else if(input == 0)
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
    
    public void setImage(Bitmap map)
    {
        usedImage = map;
        
        //mapArray = edgeDetect(usedImage,15);
        
        tempImage = usedImage.copy(Bitmap.Config.RGB_565, true);
        
        //new ScanTask().execute(usedImage,tempImage);
        
        edgeDetect(usedImage,tempImage,15);
        
        //rgbMax(usedImage);
        invalidate();
    }
    
    private void horizontalVariance(Bitmap controlMap, Bitmap tempImage, double coeff)
    {
        for(int i = 0 ; i < controlMap.getWidth() ; i++)
        {
            for(int j = 0 ; j < controlMap.getHeight() ; j++)
            {
                int rgb = controlMap.getPixel(i, j);
                double r = Color.red(rgb);
                double g = Color.green(rgb);
                double b = Color.blue(rgb);
                
                double calc = (1/21) * (Math.pow(r-coeff,2)+Math.pow(g-coeff,2)+Math.pow(b-coeff,2));
                
            }
        }
        
        
        
    }
    
    
    private int[][] edgeDetect(Bitmap controlMap,Bitmap tempImage, double threshold)
    {
        int[][] newInt = new int[controlMap.getWidth()][controlMap.getHeight()];     
        //tempImage = Bitmap.createBitmap(controlMap.getWidth(),controlMap.getHeight(),Bitmap.Config.RGB_565);
        //Log.d("TAG","STARTING THE EDGE DETECT");
        //tempImage = controlMap.copy(Bitmap.Config.RGB_565, true);
        
        
        
        for(int i = 0 ; i < controlMap.getWidth() ; i++)
        {
            //Log.d("TAG","STARTING THE EDGE DETECT: "+i);
            for(int j = 0 ; j < controlMap.getHeight() ; j++)
            {
                
                int darkestPixel = 255;
                int lightestPixel = 0;
                
                for(int k = 0 ; k < 8 ; k++)
                {
                    float mul = (float) ((float)k*45/180*Math.PI);

                    int Xup = (int) Math.round(Math.sin(mul));
                    int Yup = (int) Math.round(Math.cos(mul));
                    
                    int modX = Xup+i;
                    int modY = Yup+j;
                    
                    if(modX > 0 && modX < controlMap.getWidth() && modY > 0 && modY < controlMap.getHeight())
                    {
                        int scale = grayScale(controlMap.getPixel(modX, modY));
                        
                        if(darkestPixel>scale)
                        {
                            darkestPixel = scale;
                        }
                        
                        if(lightestPixel<scale)
                        {
                            lightestPixel = scale;
                        }
                        
                        //Log.d(k+"PIXELS","PIXEL DARKEST: "+darkestPixel+" PIXEL LIGHTEST: "+lightestPixel);
                    }
                    
                    if((lightestPixel-darkestPixel)>threshold)
                    {
                        newInt[i][j] = 1;
                        tempImage.setPixel(i,j,Color.BLACK);
                        //Log.d("TAG","STARTING THE LIGHT");
                    }
                    else
                    {
                        newInt[i][j] = 0;
                        tempImage.setPixel(i,j,Color.WHITE);
                        //Log.d("TAG","STARTING THE DARK");
                    }
                }
                
                
                
            }
        }
        
        //Log.d("TAG","STARTING THE END");
        
        return newInt;
    }
 
    private int grayScale(int pixelData)
    {
        double r = (pixelData >> 16) & 0xff;
        double g = (pixelData >> 8) & 0xff;
        double b = pixelData & 0xff;
        return (int)(r*0.3+g*0.59+b*0.11);    
    }
    
    private void rgbMax(Bitmap bmp)
    {
        tempImage = bmp.copy(Bitmap.Config.RGB_565, true);
        
        for(int i = 0 ; i < tempImage.getWidth() ; i++)
        {
            for(int j = 0 ; j < tempImage.getHeight() ; j++)
            {
               int col =  tempImage.getPixel(i, j);                
               
               double r = (col >> 16) & 0xff;
               double g = (col >> 8) & 0xff;
               double b = col & 0xff;
               double max = Math.max(r, Math.max(g, b));
               
               if(max == r)
               {
                   tempImage.setPixel(i,j,Color.RED);
               }
               else if(max == g)
               {
                   tempImage.setPixel(i,j,Color.GREEN);
               }
               else if(max == b)
               {
                   tempImage.setPixel(i,j,Color.BLUE);
               }
            }
        }
    }
    
    
    @Override
    public void onDraw(Canvas canvas) 
    {
        
        Paint pain = new Paint();
        pain.setColor(Color.RED);
        canvas.drawBitmap(usedImage, 0, 0,pain);
        
        if(tempImage != null)
        {
                pain = new Paint();
                pain.setColor(Color.RED);
                canvas.drawBitmap(tempImage, 0, 0,pain);
        }

        
        invalidate();
    }
    
    
}
