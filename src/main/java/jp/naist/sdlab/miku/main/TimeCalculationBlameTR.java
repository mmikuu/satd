package jp.naist.sdlab.miku.main;


public class TimeCalculationBlameTR extends TimeCalculationBase {


    public TimeCalculationBlameTR(){
        super("deleted_satd.csv", "commitdeletedBlame_TR.csv","2016/06/22 00:00:00 UTC", "2018/06/26 23:59:59 UTC", false);
    }




    public static void main(String[] args) throws Exception {
        TimeCalculationBlameTR calculator = new TimeCalculationBlameTR();
        calculator.run();
    }

}