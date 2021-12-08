import org.junit.Test;

public class DemoTest {

    @Test
    public void test1() {
        DemoClass c = new DemoClass();
        AdviceTracer.addTracedAdvice("aspect");
        AdviceTracer.setAdviceTracerOn();
        c.m1();
        AdviceTracer.setAdviceTracerOff();
        AdviceAssert.assertAdviceExecutionsEquals(0);
    }

    @Test
    public void test2() {
        DemoClass c = new DemoClass();
        AdviceTracer.addTracedAdvice("aspect");
        AdviceTracer.setAdviceTracerOn();
        c.m2();
        AdviceTracer.setAdviceTracerOff();
        AdviceAssert.assertAdviceExecutionsEquals(1);
        AdviceAssert.assertExecutedAdviceAtJoinpoint("aspect","DemoClass.m1:5");
    }
}
