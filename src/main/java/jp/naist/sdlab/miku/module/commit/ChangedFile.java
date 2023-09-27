package jp.naist.sdlab.miku.module.commit;


import java.io.Serializable;
import java.util.List;
import java.util.Objects;


import org.eclipse.jgit.patch.FileHeader;

/**
 * ChangedFile provides information about a changed file
 * This class is used to store/get data from Database (hibernate)
 */
public class ChangedFile implements Serializable {

    /**
     * this ID is automatically generated for each table by hibernate
     */

    public long changeFileId;
    /**
     * path after changes
     */
    public String newPath;
    /**
     * path before changes
     */
    public String oldPath;

    /**
     * The number of lines that are added
     */
    public int addedLines;
    /**
     * The number of lines that are deleted
     */
    public int deletedLines;
    /**
     * The mode of change types. See Class Definition
     */
    public Mode mode;

    /**
     * This method receives FileHeader given by JGit.
     *
     * @param header
     */
    public ChangedFile(FileHeader header) {
        this.newPath = header.getNewPath();
        this.oldPath = header.getOldPath();
        if (!this.newPath.equals("/dev/null") && !this.oldPath.equals("/dev/null")) {
            this.mode = Mode.MODIFY;
        } else if (this.oldPath.equals("/dev/null")) {
            this.mode = Mode.ADD;
        } else if (this.newPath.equals("/dev/null")) {
            this.mode = Mode.DELETE;
        } else {
            throw new Error();
        }
    }

    /**
     * The type of change in File
     * If the file is added: ADD
     * If the file is deleted: DELETE
     * If the file is modified: MODIFY
     * This enum is determined by JGit
     */
    public enum Mode{
        ADD, DELETE, MODIFY
    }

    /**
     * Chunks in this change
     */
    public List<Chunk> chunks;

    public void setLines(){
        addedLines = 0;
        deletedLines = 0;
        for(Chunk c: chunks){
            addedLines += c.getAddedLines();
            deletedLines += c.getDeletedLines();
        }
    }
    public ChangedFile(){}
    @Override
    public int hashCode(){
        return Objects.hash(newPath, oldPath);
    }

}

