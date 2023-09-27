package jp.naist.sdlab.miku.module.commit;

import org.eclipse.jgit.diff.Edit;

import java.util.Objects;

/**
 * Chunk is a block of a change.
 * This class is used to store/get data from Database (hibernate)
 */
public class Chunk {
    /**
     * this ID is automatically generated for each table by hibernate
     */
    public long chunk_id;
    /**
     * JGit's edit type including
     *         INSERT,
     *         DELETE,
     *         REPLACE,
     *         EMPTY;
     */
    private Edit.Type type;
    /**
     * The line number where this chunk starts from in the file before change
     */
    private  int oldStartNo;
    /**
     * The line number where this chunk end by in the file before change
     */
    private  int oldEndNo;
    /**
     * The line number where this chunk starts from in the file before change
     */
    private  int newStartNo;
    /**
     * The line number where this chunk ends by in the file before change
     */
    private  int newEndNo;
    /**
     * The number of added lines
     */
    private  int addedLines;
    /**
     * The number of deleted lines
     */
    private  int deletedLines;

    private String text;

    /**
     * To store this to database,
     * this constructor transports the contents of Edit class provided by JGit to this class.
     *
     * @param edit
     */

    public Chunk(Edit edit) {
        this.type = edit.getType();
        this.oldStartNo = edit.getBeginA();
        this.oldEndNo = edit.getEndA();
        this.newStartNo = edit.getBeginB();
        this.newEndNo = edit.getEndB();
        this.addedLines = edit.getLengthB();
        this.deletedLines = edit.getLengthA();
    }

    public int getNewEndNo() {
        return newEndNo;
    }

    public int getNewStartNo() {
        return newStartNo;
    }

    public int getOldEndNo() {
        return oldEndNo;
    }

    public int getOldStartNo() {
        return oldStartNo;
    }

    public Edit.Type getType() {
        return type;
    }

    public int getAddedLines() {
        return addedLines;
    }

    public int getDeletedLines() {
        return deletedLines;
    }

    public String getText(){return text;}
    public Chunk(){}
//    @Override
//    public int hashCode(){
//    return Objects.hash( oldStartNo,newStartNo,oldEndNo,newEndNo );
//    }
}
