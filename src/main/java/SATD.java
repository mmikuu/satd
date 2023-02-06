public class SATD {
    String content;
    Type type;
    String commitId;
    String fileName;
    int line = 0;

    public enum Type{
        DELETED, ADDED
    }
    public boolean isSATD(){
        return type !=null;
    }
    public String toString(){
        return this.type + ", " + commitId + ", " + fileName + ", " + line + ", " + content;
    }
}
