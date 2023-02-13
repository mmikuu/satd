package jp.naist.sdlab.miku.main;


public class TimeCalculationRevertSR extends TimeCalculationBase {


    public TimeCalculationRevertSR(){
        super("added_satd.csv", "commitaddedBlame_SR.csv","2018/06/27 00:00:00 UTC", "2020/06/17 23:59:59 UTC", true);
    }




    public static void main(String[] args) throws Exception {
        TimeCalculationRevertSR calculator = new TimeCalculationRevertSR();
        calculator.run();
    }

}