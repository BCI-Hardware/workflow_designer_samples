package data;

import cz.zcu.kiv.WorkflowDesigner.Annotations.*;

import java.io.Serializable;

import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;

@BlockType(type ="CONSTANT", family = "MATH")
public class ConstantBlock implements Serializable {


    @BlockProperty(name = "Value", type = NUMBER, defaultValue = "0")
    private int val=7;

    @BlockOutput(name = "Operand", type = NUMBER)
    private int op=0;

    @BlockExecute
    public void process(){
        op=val;
    }
}
