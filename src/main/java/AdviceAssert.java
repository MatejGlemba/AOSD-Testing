import org.aspectj.weaver.AdviceKind;
import org.junit.Assert;

import java.util.List;

public class AdviceAssert {

    public static void assertAdviceExecutionsEquals(String message, int i) {
        if(AdviceTracer.getExecutedAdvices() == null) {
            Assert.assertEquals(message, Integer.valueOf(i), Integer.valueOf(0));
        } else {
            Assert.assertEquals(message, Integer.valueOf(i), Integer.valueOf(AdviceTracer.executedAdvicesNumber()));
        }

    }

    public static void assertAdviceExecutionsEquals(int i) {
        assertAdviceExecutionsEquals(null, i);
    }

    public static void assertExecutedAdvice(String message, String advice) {
        String m = message == null?"":message + " ";
        m = m + "Advice " + advice + " has not been executed";
        Assert.assertTrue(m, AdviceTracer.containsAdvice(advice));
    }

    public static void assertExecutedAdvice(String advice) {
        assertExecutedAdvice(null, advice);
    }

    public static void assertExecutedAdviceAtJoinpoint(String message, String advice, String joinpoint) {
        String m = message == null ? "" : message + " ";
        m = m + "Advice " + advice + " has not been executed at " + joinpoint;
        Assert.assertTrue(m, AdviceTracer.containsAdviceAtJoinpoint(advice, joinpoint));
    }

    public static void assertExecutedAdviceAtJoinpoint(String advice, String joinpoint) {
        assertExecutedAdviceAtJoinpoint(null, advice, joinpoint);
    }

    /*-------------------------------------------------------------------------------*/

    public static void assertBeforeAdvice(String advice) {
        Assert.assertEquals(AdviceKind.Before.getName(), AdviceTracer.getAdviceKind(advice));
    }

    public static void assertAfterAdvice(String advice) {
        Assert.assertEquals(AdviceKind.After.getName(), AdviceTracer.getAdviceKind(advice));
    }

    public static void assertAroundAdvice(String advice) {
        Assert.assertEquals(AdviceKind.Around.getName(), AdviceTracer.getAdviceKind(advice));
    }

    public static void assertAfterReturningAdvice(String advice) {
        Assert.assertEquals(AdviceKind.AfterReturning.getName().toLowerCase(), AdviceTracer.getAdviceKind(advice).replace("_", ""));
    }

    public static void assertAfterThrowingAdvice(String advice) {
        Assert.assertEquals(AdviceKind.AfterThrowing.getName().toLowerCase(), AdviceTracer.getAdviceKind(advice).replace("_", ""));
    }

    public static void assertAdviceKind(String adviceKind, String method) {
        Assert.assertEquals(adviceKind.toLowerCase(), AdviceTracer.getAdviceKindBasedOnMethod(method).replace("_", ""));
    }

    public static void assertMethodWasAdviced(String method) {
        Assert.assertNotNull(AdviceTracer.getAdviceKindBasedOnMethod(method));
    }

    public static void assertMethodWasAdviced(String method, String advice) {
        Assert.assertTrue(AdviceTracer.methodWasAdvicedBy(method, advice));
    }

    public static void assertMethodWasAdviced(String method, String advice, String adviceKind) {
        Assert.assertTrue(AdviceTracer.methodWasAdvicedByAdviceKind(method, advice, adviceKind));
    }

    public static void assertControlFlow(String method, List<String> expectedCflow) {
        Assert.assertEquals(expectedCflow, AdviceTracer.getControlFlowOfMethod(method, expectedCflow));
    }

    public static void assertAdvicePointcutExpression(String pointcutExpression, String advice) {
        Assert.assertEquals(pointcutExpression, AdviceTracer.getPointcutExpressionByAdvice(advice));
    }

    public static void assertTypeOfPointcutOfAdvice(String advice, String typeOfPointcut) {
        Assert.assertTrue(AdviceTracer.getTypeOfPointcutInAdvice(advice, typeOfPointcut));
    }
}