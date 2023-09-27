package jp.naist.sdlab.miku.module.commit;

import org.eclipse.jgit.diff.Edit;

import java.util.Objects;

public class LineChange {
    String newPath;
    String oldPath;
    private int startLineOfChunkInNewPath;
    private int endLineOfChunkInNewPath;
    private int startLineOfChunkInOldPath;
    private int endLineOfChunkInOldPath;

    private Edit.Type editType;
    int hash;

    public LineChange(String newPath, String oldPath, Chunk chunk) {
        this.newPath = newPath;
        this.oldPath = oldPath;
        this.setChunk(chunk);
        this.setEditType(chunk.getType());
    }
    private void setEditType(Edit.Type type){
        this.editType = type;
    }
    private void setChunk(Chunk chunk){//chunkの行計算は０からスタート
        this.startLineOfChunkInNewPath = chunk.getNewStartNo()+1;
        this.endLineOfChunkInNewPath = chunk.getNewEndNo();
        this.startLineOfChunkInOldPath = chunk.getNewEndNo()+1;
        this.endLineOfChunkInOldPath = chunk.getOldEndNo();
    }

    public Edit.Type getEditType() {
        return editType;
    }
    @Override
    public int hashCode(){
        return Objects.hash(newPath, oldPath, startLineOfChunkInNewPath, endLineOfChunkInNewPath, startLineOfChunkInOldPath, endLineOfChunkInOldPath);
    }

}
