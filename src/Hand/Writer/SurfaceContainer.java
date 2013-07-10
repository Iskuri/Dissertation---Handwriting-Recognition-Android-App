package Hand.Writer;


import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Wade
 */
public class SurfaceContainer extends TableLayout
{
    
    TextView text;
    NotepadView padView;
    
    public SurfaceContainer(final Context context)
    {
        super(context);
        
        //Setup display data
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels/8;
        int height = metrics.heightPixels/4;
        
        
        //Setup Input view window for text
        text = new TextView(context);
        text.setHeight(height);
        this.addView(text);    
        
        //Create buttons for common un-drawable characters
        TableRow row = new TableRow(context);
        Button spaceButton = new Button(context);
        spaceButton.setText("Space");
        Button stopButton = new Button(context);
        stopButton.setText(".");
        Button commaButton = new Button(context);
        commaButton.setText(",");
        Button backspaceButton = new Button(context);
        backspaceButton.setText("Back");
        
        //Add buttons to display
        row.addView(stopButton);
        row.addView(commaButton);
        row.addView(spaceButton);
        row.addView(backspaceButton);
        this.addView(row);
        
        //Make buttons fit in a visually appealing way
        spaceButton.setWidth(width*4);
        stopButton.setWidth(width*1);
        commaButton.setWidth(width*1);
        backspaceButton.setWidth(width*2);
        
        
        //Add button for accessing the camera testing mode
        Button cameraMode = new Button(context);
        cameraMode.setText("Camera Function");
        cameraMode.setWidth(width*8);
        this.addView(cameraMode);
        
        //Create draw space class
        padView = new NotepadView(context,text);
        this.addView(padView);
        
        
        //Setup text functions for each button
        spaceButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View view) 
            {
                text.append(" ");
            }
        });
        
        
        
        stopButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View view) 
            {
                text.append(".");
            }
        });
        
        commaButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View view) 
            {
                text.append(",");
            }
        });
        
        backspaceButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View view) 
            {
                if(text.getText().length()>0)
                {
                    //Remove one character
                    text.setText(text.getText().toString().substring(0,text.getText().length()-1));
                }
            }
        });
        
        cameraMode.setOnClickListener(new OnClickListener()
        {
            public void onClick(View view) 
            {
                //Go to camera mode
                CameraView camView = new CameraView(context);
                Handwriter.main.setContentView(camView);
            }
        });
        
        
        
    }

    
    
}
