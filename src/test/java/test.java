import org.junit.Assert;
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

    String lineNoRegrex = "@@\\s-(\\d+).*\\s\\+(\\d+).*\\s@@";
    @Test
    public void testLineNoRegrex1(){
        String line="@@ -212,35 +200,10 @@ void recordStructuralDependency(IProject prereqProject, State prereqState) {";

        Pattern p = Pattern.compile(lineNoRegrex);

        Matcher m = p.matcher(line);
        if(m.find()){
            Assert.assertEquals("212", m.group(1));
            Assert.assertEquals("200", m.group(2));
        }else{
            Assert.fail();
        }
    }

    @Test
    public void testLineNoRegrex2(){
        String line="@@ -65 +65 @@ public class JrtUtilTest extends TestCase {";

        Pattern p = Pattern.compile(lineNoRegrex);

        Matcher m = p.matcher(line);
        if(m.find()){
            Assert.assertEquals("65", m.group(1));
            Assert.assertEquals("65", m.group(2));
        }else{
            Assert.fail();
        }
    }
}
