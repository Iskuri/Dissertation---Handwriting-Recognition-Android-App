    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Hand.Writer;

/**
 *
 * @author Wade
 */
public class Neuron 
{
    public double[] inputValues;
    public double[] inputWeights;
    public Neuron[] inputNeurons;
    public Neuron[] outputNeurons;
    public double output = 0;
    public boolean needChange = true;
    public double errorSignal = 0;
    public double learningCoefficient = 0.1;
    
    
    public Neuron(int valCount, Neuron[] _outNeuron, Neuron[] _inNeuron)
    {
        outputNeurons = _outNeuron;
        inputNeurons = _inNeuron;
        
        inputValues = new double[valCount];
        inputWeights = new double[valCount];
        
        randomizeWeights();
    }
    
    public void randomizeWeights()
    {
        //Randomize all of the weights in the neuron in order for them to be trained later
        
        for(int i = 0 ; i < inputWeights.length ; i++)
        {
            inputWeights[i] = Math.random()*10-5;
        }
    }
    
    public void pushOutputs(int index)
    {
        
        //Calculate the output value        
        calcOutput();
        
        //Push output value to neurons connected to output
        if(outputNeurons!=null)
        {
            for(int i = 0 ; i < outputNeurons.length ; i++)
            {
                outputNeurons[i].inputValues[index] = output;            
            }
        }
    }
    
    public void updateWeights()
    {
        
        double derivative = calcDerivative();
        
        //Calculating the change in weight for the neuron
        if(inputNeurons==null)
        {
            for(int i = 0 ; i < inputWeights.length ; i++)
            {
                inputWeights[i] = inputWeights[i] + learningCoefficient * errorSignal * derivative * inputValues[i];
            }
        }
        else
        {
            for(int i = 0 ; i < inputWeights.length ; i++)
            {
                inputWeights[i] = inputWeights[i] + learningCoefficient * errorSignal * derivative * inputNeurons[i].output;
            }            
        }
        
    }
    
    public double calcDerivative()
    {
        //Gets the summation of the input values * their weights and then runs the derivative 
        //function on it.
        
        double increment = 0;
        
        for(int i = 0 ; i < inputValues.length ; i++)
        {
            increment += inputValues[i]*inputWeights[i];
        }
        
        increment = derivativeFunction(increment);
        
        return increment;
    }
    
    public double calcOutput()
    {
        //Gets the summation of the input values * their weights and then runs the activation 
        //function on it.        
        double increment = 0;
        
        for(int i = 0 ; i < inputValues.length ; i++)
        {
            //System.out.println(inputValues[i]+" : "+inputWeights[i]+" : "+inputValues[i]*inputWeights[i]);            
            increment += inputValues[i]*inputWeights[i];
        }
        
        increment = activationFunction(increment);
        //System.out.println("OUTPUT: "+increment);
        
        output = increment;
        return increment;
    }
    
    public double activationFunction(double input)
    {
        //Activation function for the neuron, Log-Sigmoid in this case
        return 1/(1+Math.exp(-input));
        //return Math.sin(input);
        
    }
    
    public double derivativeFunction(double input)
    {
       //Derivative of the Log-Sigmoid
       return activationFunction(input)*(1-activationFunction(input));
       //return Math.cos(input);
    }
    
    public void pushError()
    {
        //Adds to the error of the neurons it is getting inputs from, taking its
        //own error and multiplying it by the input weight, and using that
        if(inputNeurons!=null)
        {
            for(int i = 0 ; i < inputNeurons.length ; i++)
            {
                inputNeurons[i].errorSignal += errorSignal * inputWeights[i];
            }
        }
    }
    
    
}
