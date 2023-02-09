package jp.naist.sdlab.miku.module;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;

public class SATD {
    String content;
    public Type type;
    public String commitId;
    String fileName;
    int line = 0;

    public enum Type{
        DELETED, ADDED
    }
    public boolean isSATD(){
        return type !=null;
    }
    public String toString(){
        return this.type + ", " + commitId + ", " + fileName + ", " + line + ", \"" + content+"\"";
    }
}
