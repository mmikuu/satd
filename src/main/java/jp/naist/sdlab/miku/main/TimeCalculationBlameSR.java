package jp.naist.sdlab.miku.main;


public class TimeCalculationBlameSR extends TimeCalculationBase {


    public TimeCalculationBlameSR(){
        super("deleted_satd.csv", "commitdeletedBlame_SR.csv","2018/06/27 00:00:00 UTC", "2020/06/17 23:59:59 UTC", false);
    }




    public static void main(String[] args) throws Exception {
        TimeCalculationBlameSR calculator = new TimeCalculationBlameSR();
        calculator.run();
    }

}