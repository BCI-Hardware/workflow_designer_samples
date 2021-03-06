package cz.zcu.kiv.eeg.basil.workflow.io.xdf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.nd4j.shade.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Created by Tomas Prokop on 14.01.2019.
 */
@JacksonXmlRootElement(localName = "info")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileHeader {
    private double version;

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }
}
