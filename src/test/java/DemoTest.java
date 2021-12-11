import org.junit.Assert;
import org.junit.Test;
import src.demo.files.DemoClass;

import java.util.Arrays;

public class DemoTest {

    @Test
    public void testNoAdviceExecutions() {
        DemoClass c = new DemoClass();
        AdviceTracer.setAdviceTracerOn();
        c.m0();
        AdviceAssert.assertAdviceExecutionsEquals(0);
        AdviceTracer.setAdviceTracerOff();
    }

    @Test
    public void testOneAdviceExecutionWithAdviceSpecification() {
        DemoClass c = new DemoClass();
        AdviceTracer.addTracedAdvice("aspect3");
        AdviceTracer.setAdviceTracerOn();
        c.m1();
        AdviceAssert.assertAdviceExecutionsEquals(1);
        AdviceAssert.assertAfterAdvice("aspect3");
        AdviceTracer.setAdviceTracerOff();
    }

    @Test
    public void testOneAdviceExecutionWithoutAdviceSpecification() {
        DemoClass c = new DemoClass();
        AdviceTracer.setAdviceTracerOn();
        c.m1();
        AdviceAssert.assertAdviceKind("after", "m1");
        AdviceAssert.assertAdviceExecutionsEquals(1);
        AdviceTracer.setAdviceTracerOff();
    }

    @Test
    public void testOneAdviceExecutionsWithAdviceSpecification() {
        DemoClass c = new DemoClass();
        AdviceTracer.addTracedAdvice("aspect");
        AdviceTracer.setAdviceTracerOn();
        c.m2();
        AdviceAssert.assertAdviceExecutionsEquals(1);
        AdviceAssert.assertExecutedAdviceAtJoinpoint("aspect","src.demo.files.DemoClass.m2:15");
        AdviceTracer.setAdviceTracerOff();
    }

    @Test
    public void testThreeAdviceExecutionsWithoutAdviceSpecification() {
        DemoClass c = new DemoClass();
        AdviceTracer.setAdviceTracerOn();
        c.m2();
        AdviceAssert.assertAdviceExecutionsEquals(3);
        AdviceAssert.assertMethodWasAdviced("m1");
        AdviceAssert.assertMethodWasAdviced("m1", "aspect");
        AdviceAssert.assertMethodWasAdviced("m1", "aspect3", "after");
        AdviceTracer.setAdviceTracerOff();
    }

    @Test
    public void testAfterThrowingAdvice() {
        DemoClass c = new DemoClass();
        AdviceTracer.addTracedAdvice("aspect5");
        AdviceTracer.setAdviceTracerOn();
        try {
            c.m3();
        } catch (RuntimeException ignored) {}
        AdviceAssert.assertAdviceExecutionsEquals(1);
        AdviceAssert.assertAfterThrowingAdvice("aspect5");
        AdviceAssert.assertExecutedAdviceAtJoinpoint("aspect5","src.demo.files.DemoClass.m3:20");
        AdviceTracer.setAdviceTracerOff();
    }

    @Test
    public void testAfterReturningAdvice() {
        DemoClass c = new DemoClass();
        AdviceTracer.addTracedAdvice("aspect4");
        AdviceTracer.setAdviceTracerOn();
        String returnVal = c.m4();
        AdviceAssert.assertAdviceExecutionsEquals(1);
        AdviceAssert.assertAfterReturningAdvice("aspect4");
        AdviceAssert.assertAdvicePointcutExpression("execution(String src.demo.files.DemoClass.m4())", "aspect4");
        AdviceAssert.assertTypeOfPointcutOfAdvice("aspect4", "execution");
        AdviceAssert.assertControlFlow("m4", Arrays.asList("beforeTracer", "aspect4", "m4", "testAfterReturningAdvice"));
        AdviceAssert.assertExecutedAdviceAtJoinpoint("aspect4","src.demo.files.DemoClass.m4:25");
        Assert.assertEquals("test output of m4()", returnVal);
        AdviceTracer.setAdviceTracerOff();
    }

    @Test
    public void testAroundAdvice() {
        DemoClass c = new DemoClass();
        AdviceTracer.addTracedAdvice("aspect6");
        AdviceTracer.setAdviceTracerOn();
        String returnVal = c.m5();
        AdviceAssert.assertAdviceExecutionsEquals(1);
        AdviceAssert.assertAroundAdvice("aspect6");
        Assert.assertEquals("test output of m5() around aspekt", returnVal);
        AdviceTracer.setAdviceTracerOff();
    }
}
