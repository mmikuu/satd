package jp.naist.sdlab.miku.main;

import jp.naist.se.commentlister.FileAnalyzer;
import jp.naist.se.commentlister.reader.CommentReader;

import java.io.File;
import java.io.IOException;

public class renshuu {
    public static void main(String[] args) throws IOException {
        File f = new File("/Users/watanabemiku/sdlab/satd-analytics/CommentLister/src/main/java/jp/naist/se/commentlister/GitFileCount.java");
        CommentReader cr = FileAnalyzer.extractComments(f.toPath());
        while(cr.next()){
            System.out.println(cr.getLine());//コメント始まりの行数
            System.out.println(cr.getText());//コメントの内容
            System.out.println(cr.getText().split("\n").length);//ブロックコメント内の改行の数
            System.out.println(cr.getLine()+cr.getText().split("\n").length-1);//ブロックコメント最後の行数
        }

    }
}
