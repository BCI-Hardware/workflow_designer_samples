package cz.zcu.kiv.eeg.basil.workflow;

import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockInput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockOutput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockProperty;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockType;
import cz.zcu.kiv.eeg.basil.data.EEGDataPackageList;
import cz.zcu.kiv.eeg.basil.data.FeatureVector;
import cz.zcu.kiv.eegdsp.common.ISignalProcessor;

import java.io.Serializable;
import java.util.List;

import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;

/**
 * Created by Tomas Prokop on 15.10.2018.
 */
@BlockType(type="MemdBlock",family = "FeatureExtraction", runAsJar = true)
public class MemdBlock {

    @BlockInput(name = "EEGData", type = "EEGDataList")
    private EEGDataPackageList epochs;

    @BlockOutput(name = "FeatureVectors", type = "List<FeatureVector>")
    private List<FeatureVector> featureVectors;

    public MemdBlock(){}


}
