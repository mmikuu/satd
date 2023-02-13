package jp.naist.sdlab.miku.main;


public class TimeCalculationRevertTR extends TimeCalculationBase {


    public TimeCalculationRevertTR(){
        super("added_satd.csv", "commitaddedBlame_TR.csv","2016/06/22 00:00:00 UTC", "2018/06/26 23:59:59 UTC", true);
    }




    public static void main(String[] args) throws Exception {
        TimeCalculationRevertTR calculator = new TimeCalculationRevertTR();
        calculator.run();
    }

}