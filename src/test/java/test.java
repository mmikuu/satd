import org.junit.Test;
import satd_detector.core.utils.SATDDetector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    @Test
    public void test_u(){
        String line="//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$";
        SATDDetector detector1 = new SATDDetector();

        String regex = "\\/\\/(.*)";
        Pattern p = Pattern.compile(regex);
        System.out.println(line);
        Matcher m = p.matcher(line);
        if(m.find()){
            String check = m.group(1);
            System.out.println(m.group(1));
            boolean result = detector1.isSATD(check);
            System.out.println("結果"+result);

        }
    }
}
