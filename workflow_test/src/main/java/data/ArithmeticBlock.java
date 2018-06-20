package data;

import cz.zcu.kiv.WorkflowDesigner.Annotations.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static cz.zcu.kiv.WorkflowDesigner.Type.NUMBER;
import static cz.zcu.kiv.WorkflowDesigner.Type.STRING;
import static cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality.ONE_TO_MANY;
import static cz.zcu.kiv.WorkflowDesigner.WorkflowCardinality.ONE_TO_ONE;

@BlockType(type ="ARITHMETIC", family = "MATH", runAsJar = true)
public class ArithmeticBlock {

    @BlockInput(name = "Operand1", type = NUMBER, cardinality = ONE_TO_ONE)
    private int op1=0;

    @BlockInput(name = "Operand2", type = NUMBER, cardinality = ONE_TO_ONE)
    private int op2=0;

    @BlockOutput(name = "Operand3", type = NUMBER, cardinality = ONE_TO_MANY)
    private static int op3=0;

    @BlockProperty(name ="Operation", type = STRING ,defaultValue = "add")
    private String operation;

    @BlockProperty(name ="OutputType", type = STRING ,defaultValue = "String")
    private String outputType;

    @BlockExecute
    public Object process() throws IOException {
        switch (operation){
            case "add":
                op3=op1+op2;
                break;
            case "subtract":
                op3=op1-op2;
                break;
            case "multiply":
                op3=op1*op2;
                break;
            case "divide":
                op3=op1/op2;
                break;
        }
        switch (outputType.toLowerCase()){
            case "string":
                return String.valueOf(op3);
            case "file":
                File outputFile = new File("demo.txt");
                FileUtils.writeStringToFile(outputFile,String.valueOf(op3),Charset.defaultCharset());
                return outputFile;
            default:
                return String.valueOf(op3);
        }


    }

    public static int getOp3() {
        return op3;
    }
}
