package jp.naist.sdlab.miku.module.commit;

import jp.naist.sdlab.miku.module.SATD;
import org.eclipse.jgit.diff.Edit;

public class Comment {
    public int lineNo;
    public String context;
    public String filepath;
    public Edit.Type type;
    public int hash;
    public Comment(int lineNo, String context, LineChange lc, String filepath){
        this.lineNo = lineNo;
        this.context = context;
        this.type = lc.getEditType();
        this.hash = lc.hashCode();
        this.filepath = filepath;
    }

    @Override
    public String toString() {
        return "FilePath:"+this.filepath+"\nTYPE:"+this.type + "LINE:" + lineNo + "\nCONTEXT:" + context+"\n";
    }
}
