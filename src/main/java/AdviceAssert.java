import org.junit.Assert;

public class AdviceAssert {

    public static void assertAdviceExecutionsEquals(String message, int i) {
        if(AdviceTracer.getExecutedAdvices() == null) {
            Assert.assertEquals(message, Integer.valueOf(i), Integer.valueOf(0));
        } else {
            Assert.assertEquals(message, Integer.valueOf(i), Integer.valueOf(AdviceTracer.getExecutedAdvices().size()));
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
}