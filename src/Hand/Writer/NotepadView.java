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
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Wade
 */
public class NotepadView extends View implements OnTouchListener
{
    private NeuralNetwork network = new NeuralNetwork(new int[] {8*8+2,100,62});
    
    private Bitmap drawMap;
    private double penSize;
    
    private Bitmap lookMap;
    private ArrayList<Bitmap> lookMaps = new ArrayList();
    
    int xLow=10000;
    int yLow=10000;
    int xHigh;
    int yHigh;
    int timeOut;
    
    //String text = "";
    
    String[] characterLoop;
    
    boolean canCheck = false;
    
    double highestLast = 0;
    
    //String generatedXml = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>";
    
    float lastX = 0;
    float lastY = 0;
    
    Context _context;
    
    TextView textApplier;
    
    public NotepadView(Context context, TextView _textApplier)
    {
        super(context);
        
        //Setup some values
        
        textApplier = _textApplier;
        
        this.setOnTouchListener(this);
        
        _context = context;
        
        
        //Change the network's weights to correspond with the xml file
        try {
            network.xmlTrain(context);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(NotepadView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(NotepadView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NotepadView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        generateCharList();
        
        //Create drawing data and pen size
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        
        drawMap = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
        
        penSize = (double)Math.min(width, height)/25;
        
    }

    public boolean onTouch(View view, MotionEvent me) 
    {
        canCheck = true;
        
        //Create canvas for being drawn on and setup pen parameters
        
        Canvas canv = new Canvas(drawMap);
        
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth((float)penSize*2);
        
        //Switch case for deciding how to draw, depending on what action is occuring
        switch(me.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                canv.drawCircle(me.getX(), me.getY(), (int)penSize, paint);

                break;
            case MotionEvent.ACTION_UP:
                canv.drawCircle(me.getX(), me.getY(), (int)penSize, paint);              
                break;                
            case MotionEvent.ACTION_MOVE:
                canv.drawLine(lastX, lastY, me.getX(), me.getY(), paint);
                canv.drawCircle(me.getX(), me.getY(), (int)penSize, paint);   
                break;
        }
        
        
        //Check lowest and highest X and Y values and changing if required
        lastX = me.getX();
        lastY = me.getY();
        
        
        if((me.getX()-penSize)<xLow)
        {
            xLow = (int)(me.getX()-penSize);
        }

        if((me.getY()-penSize)<yLow)
        {
            yLow = (int)(me.getY()-penSize);
        }
        
        if((me.getX()+penSize)>xHigh)
        {
            xHigh = (int)(me.getX()+penSize);
        }

        if((me.getY()+penSize)>yHigh)
        {
            yHigh = (int)(me.getY()+penSize);
        }
        
        timeOut = 0;
        
        return true;
    }
    
    @Override
    public void onDraw(Canvas canvas) 
    {
        
        //Draw image so far
        canvas.drawBitmap(drawMap,0,0,null);
        
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        
        //Check data so far
        checkInc();
        
        //Force a redraw
        invalidate();
    }
    
    private void checkInc()
    {        
        //Checks if it has hit the timeout        
        if(timeOut>50 && canCheck)
        {
            canCheck = false;
            
            timeOut = 0;
            
            Bitmap newMap = Bitmap.createBitmap(drawMap,xLow,yLow,xHigh-xLow,yHigh-yLow);
            
            //Set inputs and attain outputs character
            inputResolutions(newMap);
            
            textApplier.append(getCharacter());
            
            //Reset box and bitmap
            xLow = 10000;
            xHigh = 0;
            yLow = 10000;
            yHigh = 0;

            drawMap = Bitmap.createBitmap(drawMap.getWidth(),drawMap.getHeight(),Bitmap.Config.RGB_565);
        }
        
        timeOut++;
    }
    
    private String getCharacter()
    {
        //Loop through neural network output neurons and check the highest output neuron
        //It relates this output index to the characterLoop array
        double[] outputs = network.getAllOutputs();
                
        int index = 0;
        double lowestOut = 25;
        
        for(int i = 0 ; i < outputs.length ; i++)
        {
            if(outputs[i]<lowestOut)
            {
                index = i;
                lowestOut = outputs[i];
            }
            
        }
        highestLast = lowestOut;
        return characterLoop[index];        
    }
    
    private void generateCharList()
    {
        //Generate list of characters, see CharacterTrainer for more info
        
        characterLoop = new String[62];
        //characterLoop = new String[10];
        int charCount = 0;
        
        for(int i = 48 ; i < 58 ; i++)
        {
            characterLoop[charCount] = Character.toString((char)i);
            charCount++;
        }
        
        for(int i = 65; i < 91 ; i++)
        {
            characterLoop[charCount] = Character.toString((char)i);
            charCount++;
        }
        
        for(int i = 97; i < 123 ; i++)
        {
            characterLoop[charCount] = Character.toString((char)i);
            charCount++;
        }
        
        /*for(int i = 0 ; i  < characterLoop.length ; i++)
        {
            System.out.println(characterLoop[i]);
        }*/
        
    }
    
    private int grayScale(int rgb)
    {
        //Generic grayscale function
        
        double r = (rgb & 0xFF0000) >> 16;
        double g = (rgb & 0xFF00) >> 8;
        double b = (rgb & 0xFF);              
        return (int)(r*0.3+g*0.59+b*0.11);
    }
    
    
    private void inputResolutions(Bitmap tempMap)
    {
        int increment = 0;
        
        //Create New bitmap class
        
        lookMap = Bitmap.createBitmap(8,8,Bitmap.Config.RGB_565);
        
        //Loop through the bitmap in chunks to get the average
        //Set inputs using these chunks
        for(int i = 0 ; i < 8 ; i++)
        {
            for(int j = 0 ; j < 8 ; j++)
            {
                double chunk = avgChunk(i,j,tempMap);
                lookMap.setPixel(i,j,Color.rgb((int)chunk, (int)chunk, (int)chunk));
                network.setInput(increment,chunk/255);
                increment++;
            }
        }
        
        double width = tempMap.getWidth();
        double height = tempMap.getHeight();
        double div = width+height;
        
        //Set normalised weight and height for last two inputs
        network.setInput(increment, width/div);
        increment++;
        network.setInput(increment, height/div);
         
        if(lookMaps.size() >= 62)
        {
            lookMaps.clear();
        }
        
        lookMaps.add(lookMap);
        
    }
    
    private int avgChunk(int x, int y,Bitmap tempMap)
    {
        //Get the average value of a chunk of the image, looping through the defined parts
        
        int width = tempMap.getWidth();
        int height = tempMap.getHeight();
        
        int divX = width/8;
        int divY = height/8;
        
        int cumulator = 0;
        
        int inc = 0;
        
        for(int i = 0 ; i < divX;i++)
        {
            for(int j = 0 ; j < divY; j++)
            {
                cumulator += grayScale(tempMap.getPixel(i+x*divX, j+y*divY));
                inc++;
            }
        }
        
               
        //return 255-cumulator/inc;        
        return cumulator/inc;
    }
    
    
}
