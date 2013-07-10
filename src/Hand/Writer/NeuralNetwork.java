/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Hand.Writer;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.sax.Element;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Wade
 */
public class NeuralNetwork 
{

    private int[] indices;
    
    private Neuron[][] allNeurons;
    
    public boolean needChange = true;
    
    private String savePoint = "training.xml";
    
    public NeuralNetwork(int[] _indices)
    {
        indices = _indices;
        
        allNeurons = new Neuron[indices.length][];
        
        for(int i = 0 ; i < allNeurons.length ; i++)
        {
            allNeurons[i] = new Neuron[indices[i]];
        }
        
        //Populate each layer with neurons connected to inputs and outputs
        
        
        for(int i = 0 ; i < allNeurons.length ; i++)
        {
            for(int j = 0 ; j < allNeurons[i].length ; j++)
            {
                if(i==0)
                {
                    allNeurons[i][j] = new Neuron(1,allNeurons[i+1],null); 
                    //allNeurons[i][j] = new Neuron(indices[i],allNeurons[i+1],null); 
                }
                else if(i==(allNeurons.length-1))
                {
                    allNeurons[i][j] = new Neuron(indices[i-1],null,allNeurons[i-1]);
                }
                else        
                {
                    allNeurons[i][j] = new Neuron(indices[i-1],allNeurons[i+1],allNeurons[i-1]);
                }
                
            }
            
        }
        
        
        /*allNeurons[0] = inputNeurons;
        allNeurons[1] = hiddenNeurons;
        allNeurons[2] = outputNeurons;*/
        
    }
    
    public NeuralNetwork cloneNetwork()
    {
        //Creates new Neural Network with the same parameters as the main network, 
        //and copies the weights over.
        //For use in separate threads of the android app to prevent changed inputs
        //causing issues.
        
        NeuralNetwork tempNetwork = new NeuralNetwork(indices.clone());
        
        for(int i = 0 ; i < allNeurons.length ; i++)
        {
            for(int j = 0 ; j < allNeurons[i].length ; j++)
            {
                Neuron tempNeuron = allNeurons[i][j];
                
                for(int k = 0 ; k < tempNeuron.inputWeights.length ; k++)
                {
                    tempNetwork.allNeurons[i][j].inputWeights[k] = tempNeuron.inputWeights[k];
                }
            }
        }
        
        return tempNetwork;
    }
    
    public void xmlTrain(Context context) throws ParserConfigurationException, SAXException, IOException
    {
        /*Trains the Neural Network from the created XML file. Gets the weights
         *from the structure of the network for each neuron.
         *
         * NeuralNetwork - Layer - Neuron - Weight
         *
         **/
        
        //Log.d("TAG","INT: "+R.xml.training);
        
        XmlResourceParser xml = context.getResources().getXml(R.xml.training);
        
        
        
        //Parsing the XML File. Due to poor quality implementation in Android this
        //method is a bit hacky.
        
        //Log.d("TAG","INT: SAWTED");
        
        
        try 
        {
            
            int layerIncrement = -1;
            int neuronIncrement = -1;
            int weightIncrement = -1;
            
            while(xml.getEventType()!=XmlPullParser.END_DOCUMENT)
            {
                
                if(xml.getEventType()==XmlPullParser.START_TAG)
                {
                    if(xml.getName().equals("layer"))
                    {
                        layerIncrement++;
                        neuronIncrement = -1;
                    }
                    else if (xml.getName().equals("Neuron"))
                    {
                        neuronIncrement++;
                        weightIncrement = -1;
                    }
                    else if (xml.getName().equals("Weight"))
                    {
                        weightIncrement++;
                       
                    }
                    
                }
                else if (xml.getEventType()==XmlPullParser.TEXT)
                {
                    //Log.d("TAG","ITERATION: "+Double.valueOf(xml.getText()));
                    allNeurons[layerIncrement][neuronIncrement].inputWeights[weightIncrement] = Double.valueOf(xml.getText());
                    //Log.d("TAG","WEIGHT: "+allNeurons[layerIncrement][neuronIncrement].inputWeights[weightIncrement]);
                }
                xml.next();
            }

            
        } 
        catch (XmlPullParserException ex) 
        {
            Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public double getOutput(int index)
    {
        //Loops through the entire network pushing each neuron's output, 
        //then gets the output of the chosen neuron
        
        for(int i = 0 ; i<allNeurons.length ; i++)
        {
            for (int j = 0 ; j<allNeurons[i].length ; j++)
            {
                allNeurons[i][j].pushOutputs(j);                  
            }
        }
        
        return allNeurons[allNeurons.length-1][index].output;
    }
    
    public double[] getAllOutputs()
    {
        //Loops through the entire network pushing each neuron's output, 
        //then gets an array of all outputs on the output layer
        
        double[] tempArray = new double[allNeurons[allNeurons.length-1].length];
        
        for(int i = 0 ; i<allNeurons.length ; i++)
        {
            for (int j = 0 ; j<allNeurons[i].length ; j++)
            {
                allNeurons[i][j].pushOutputs(j);   
                if(allNeurons[i] == allNeurons[allNeurons.length-1])
                {
                    tempArray[j] = allNeurons[allNeurons.length-1][j].output;
                }
                
            }
        }
        
        return tempArray;
    }
    
    
    public void flushErrorSignal()
    {
        //Sets every error signal to 0, so they can be updated in the next
        //training update
        
        for(int i = 0 ; i < allNeurons.length ; i++)
        {
            for (int j = 0 ; j<allNeurons[i].length ; j++)
            {
                //System.out.println(i+" : "+j+" : "+allNeurons[i][j].errorSignal);
                allNeurons[i][j].errorSignal = 0;
                
            }            
            
        }
    }
    

    public void trainNetwork(int index, double value)
    {
        //Trains the network, getting errors and updating the neurons accordingly
        
        flushErrorSignal();
        
        getOutput(index);
        
        
        //Set the initial error signal of the output neuron, Target - Current
        allNeurons[allNeurons.length-1][index].errorSignal = value - allNeurons[allNeurons.length-1][index].output;
        
        //Loop through all neurons and push the errors back
        for(int i = allNeurons.length-1 ; i >= 0 ; i--)
        {
            Neuron[] nArray = allNeurons[i];
            
            if(nArray == allNeurons[allNeurons.length-1])
            {
                nArray[index].pushError();
            }
            else
            {
                for(Neuron tempNeuron : nArray)
                {
                    tempNeuron.pushError();
                }
            }
        }
        
        //Loop through neurons and run training function
        for(Neuron[] nArray : allNeurons)
        {
            for(Neuron tempNeuron : nArray)
            {
                tempNeuron.updateWeights();
            }
            
        }
        
        
        
        
    }
    
    
    
    public void setInput(int index, double input)
    {
        //Set the inputs of neurons on the input layer

        /*for(int i = 0 ; i < allNeurons[0].length ; i++)
        {
            allNeurons[0][i].inputValues[index] = input;
        }*/
        
        allNeurons[0][index].inputValues[0] = input;
    }

    
    
}
