package cz.zcu.kiv.eeg.basil.workflow.classification;

import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockExecute;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockInput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockOutput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockType;
import cz.zcu.kiv.WorkflowDesigner.Visualizations.Table;
import cz.zcu.kiv.eeg.basil.data.ClassificationStatistics;
import cz.zcu.kiv.eeg.basil.data.EEGDataPackageList;
import cz.zcu.kiv.eeg.basil.data.EEGMarker;
import cz.zcu.kiv.eeg.basil.data.FeatureVector;



import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nd4j.evaluation.classification.Evaluation;

import static cz.zcu.kiv.WorkflowDesigner.Type.STREAM;

/**
 * Average a list of trainingEEGData using one stimuli marker
 * @author lvareka
 *
 */
@BlockType(type="NeuralNetClassifier",family = "Classification", runAsJar = true)
public class NeuralNetClassifierBlock implements Serializable {

	@BlockInput(name = "Markers",type="EEGMarker[]")
	private List<EEGMarker> markers;

	@BlockInput (name = "TrainingFeatureVectors", type = STREAM)
    private PipedInputStream trainingPipeIn = new PipedInputStream();

    @BlockInput(name = "TestingFeatureVectors", type = STREAM)
    private PipedInputStream testingPipeIn  = new PipedInputStream();
    
    
    /**
     * Loads the configuration of the trained NN from file instead of training 
     */
    @BlockInput(name = "ClassifierConfiguration", type = STREAM)
    private PipedInputStream configurationPipeIn;
    
    /**
     * Stores the configuration of the trained NN into file
     */
    @BlockOutput(name = "ClassifierConfiguration", type = STREAM)
    private PipedOutputStream configurationPipeOut = new PipedOutputStream();
    

    private List<FeatureVector> trainingEEGData;

    private List<FeatureVector> testingEEGData;

    @BlockInput(name="Layers", type="NeuralNetworkLayerChain")
    private NeuralNetworkLayerChain layerChain;

    @BlockOutput(name="ClassificationResult", type ="ClassificationStatistics")
    private ClassificationStatistics statistics;

	public NeuralNetClassifierBlock(){
		//Required Empty Default Constructor for Workflow Designer
	}
	
	
	class StreamReader extends Thread {
		private ObjectInputStream ois;
		private List<FeatureVector> featureVectors;

	    public StreamReader(ObjectInputStream ois, List<FeatureVector> featureVectors) {
	       this.ois = ois;
	       this.featureVectors = featureVectors;
	    }

	    @Override
		public void run() {
			FeatureVector fv;
			try {
				while ((fv  = (FeatureVector) ois.readObject()) != null) {
				    featureVectors.add(fv);
				}
			} catch (ClassNotFoundException | IOException e) {
				System.err.println("Failed to read feature vectors from stream: " +  e.getMessage());
			}
		}
	}
	

	@BlockExecute
    public Object process() throws  IOException, ClassNotFoundException, InterruptedException {
		// testing data
        testingEEGData  = new ArrayList<>();
        ObjectInputStream  testObjectIn   = new ObjectInputStream(testingPipeIn);
        FeatureVector test = null;
        boolean testF = true;
        StreamReader readTest = new StreamReader(testObjectIn, testingEEGData);
        readTest.start();
		
		// training data
        SDADeepLearning4jClassifier classification = new SDADeepLearning4jClassifier(layerChain.layerArraylist);
        Evaluation eval = null;
                
        if (configurationPipeIn == null) { // configuration stream not available -> read training data and train
	        trainingEEGData = new ArrayList<>();
	        ObjectInputStream  trainObjectIn  = new ObjectInputStream(trainingPipeIn);
	        FeatureVector train = null;
	        boolean trainF = true;
	        StreamReader readTrain = new StreamReader(trainObjectIn, trainingEEGData);
	        readTrain.start();
	        readTrain.join();
	        eval = classification.train(trainingEEGData, 10);
	        trainObjectIn.close();
	        trainingPipeIn.close();
        } else {
        	classification.load(configurationPipeIn);
        }
        
		readTest.join();
	    testObjectIn.close();
        testingPipeIn.close();
        
        
        // testing
        if (testingEEGData != null && testingEEGData.size() != 0) {
        	// collect expected labels
        	List<Double> expectedLabels = new ArrayList<Double>();
        	for (FeatureVector featureVector: testingEEGData) {
        		expectedLabels.add(featureVector.getExpectedOutput());
        	}
            statistics = classification.test(testingEEGData, expectedLabels);
        	System.out.println(statistics.toString());
            return statistics.toString();

        }
        
        // save to output stream
        // classification.save(configurationPipeOut);

        Table table = new Table();
        List<List<String>> rows = new ArrayList<>();
        
        rows.add(Arrays.asList("Precision",String.valueOf(eval.precision())));
        rows.add(Arrays.asList("Recall",String.valueOf(eval.recall())));
        rows.add(Arrays.asList("Accuracy",String.valueOf(eval.accuracy())));
        rows.add(Arrays.asList("F1 Score",String.valueOf(eval.f1())));

        //Confusion Matrix
        rows.add(Arrays.asList("True +ve",String.valueOf(eval.getTruePositives().totalCount())));
        rows.add(Arrays.asList("True -ve",String.valueOf(eval.getTrueNegatives().totalCount())));
        rows.add(Arrays.asList("False +ve",String.valueOf(eval.getFalsePositives().totalCount())));
        rows.add(Arrays.asList("False -ve",String.valueOf(eval.getFalseNegatives().totalCount())));
        table.setRows(rows);
        table.setCaption("Testing Dataset Results");

        return table;
    }


    public List<EEGMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<EEGMarker> markers) {
        this.markers = markers;
    }

    public void setTrainingEEGData(List<FeatureVector> trainingEEGData) {
        this.trainingEEGData = trainingEEGData;
    }
}
