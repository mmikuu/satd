package jp.naist.sdlab.miku.module;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ReplaceChecker {


    public static final double BIRT_SIMILARITY_THRESHOLD = 0.95;
    public static final double LEVEN_SIMILARITY_THRESHOLD = 0.95;
    public static final double LEVEN_DISTANCE_THRESHOLD = 1;
    public static boolean check(double calcBert, double calcLeven, double distanceLeven) throws SQLException {

        if(distanceLeven<=LEVEN_DISTANCE_THRESHOLD || calcBert >= BIRT_SIMILARITY_THRESHOLD || calcLeven >= LEVEN_SIMILARITY_THRESHOLD){
            System.out.println("Replace ==true");
            return true;

        }else{
            System.out.println("Replace ==false");
            return false;
        }
    }



}
