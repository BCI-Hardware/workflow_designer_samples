package data;

import cz.zcu.kiv.WorkflowDesigner.Annotations.*;

import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;
import static cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality.ONE_TO_ONE;

@BlockType(type ="CONSTANT", family = "MATH", runAsJar = true)
public class ConstantBlock {


    @BlockProperty(name = "Value", type = NUMBER, defaultValue = "0")
    private int val=7;

    @BlockOutput(name = "Operand", type = NUMBER, cardinality = ONE_TO_ONE)
    private int op=0;

    @BlockExecute
    public void process(){
        op=val;
    }
}
