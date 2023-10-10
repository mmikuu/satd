package jp.naist.sdlab.miku.module;

import jp.naist.sdlab.miku.module.commit.Comment;
import org.eclipse.jgit.diff.Edit;

public class SATD {
    public String content;
    public Type type;
    public String commitId;
    public String fileName;
    public Integer line;
    public boolean isParent;
    public String chunkHash;
    public int pathHash;

    public SATD(String commitId, String path, Comment comment, boolean isParent) {
        this.commitId = commitId;
        this.fileName = path;
        this.line = comment.lineNo;
        this.content = comment.context;
        this.setType(comment.type);
        this.isParent = isParent;
        this.pathHash = comment.hash;
    }

    public enum Type{
        DELETED, ADDED,REPLACE
    }

//    public void setLine(Integer line){
//        this.line=line;
//    }
//    public int getLine(){
//        return line;
//    }
    public boolean isSATD(){
        return type !=null;
    }
    public String toString(){
        return "\nTYPE:"+this.type + "\ncommitID:" + commitId + "\nHashcode"+pathHash+"\nIsParent:"+isParent+"\npath:" + fileName + "\nLINE:" + line + "\nCOMMENT:" + content+"\"";
    }

    public int getHash(){
        return this.pathHash;
    }

    public String  getType(){
        if(this.type== Type.DELETED){
            return "DELETE";
        }else if(this.type == Type.ADDED){
            return "ADDED";
        }else if(this.type == Type.REPLACE){
            return "REPLACE";
        }else{
            return null;
        }
    }

    private void setType(Edit.Type type) {
        SATD.Type commentType;
        if(type.equals(Edit.Type.EMPTY)){//TODO:EMPTYは前のファイルが空になること，削除のDELETEにしてもいいのか？　OK!
            this.type = null;
        } else if(type.equals(Edit.Type.DELETE)){
            this.type = SATD.Type.DELETED;
        } else if (type.equals(Edit.Type.INSERT)) {
            this.type = SATD.Type.ADDED;
        }else{
            this.type = SATD.Type.REPLACE;
        }

    }
}
