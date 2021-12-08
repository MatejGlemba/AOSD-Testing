
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aspectj.internal.lang.reflect.AdviceImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;

@Aspect
public class AdviceTracer {

    private static boolean on = false;
    private static List<TraceElement> executedAdvices;
    private static List<String> tracedAdvices = new ArrayList();
    private static Pattern reg = Pattern.compile("(.*)_aroundBody\\d*(:\\d*)");


    public static void setAdviceTracerOn() {
        executedAdvices = new ArrayList();
        on = true;
    }

    public static void setAdviceTracerOff() {
        System.out.println("executedAdvices" + executedAdvices);
        System.out.println("tracedAdvices" + tracedAdvices);
        tracedAdvices = new ArrayList();
        on = false;
    }

    public static void setTracedAdvices(List<String> newTracedAdvices) {
        tracedAdvices = newTracedAdvices;
    }

    public static void addTracedAdvice(String advice) {
        tracedAdvices.add(advice);
    }

    public static List getExecutedAdvices() {
        return executedAdvices;
    }

    public static int executedAdvicesNumber() {
        return executedAdvices.size();
    }

    public static boolean containsAdvice(String advice) {
        for (TraceElement e : executedAdvices) {
            if (e.getAdvice().equals(advice)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAdviceAtJoinpoint(String advice, String joinpoint) {
        Iterator iterator = executedAdvices.iterator();

        TraceElement e;
        do {
            if(!iterator.hasNext()) {
                return false;
            }

            e = (TraceElement) iterator.next();
        } while(!e.getAdvice().equals(advice) || !e.getJoinpoint().equals(joinpoint));

        return true;
    }

    @Before("!within(AdviceTracer) && adviceexecution()")
    public void tracer(JoinPoint thisJoinPoint) {
        System.out.println("aspekt z aspektu" + thisJoinPoint);
        if(isOn()) {
            String adviceName = getAdviceName(thisJoinPoint);
            if (adviceName != null) {
                if (adviceName.equals("")) {
                    adviceName = thisJoinPoint.getSignature().getDeclaringTypeName() + ":" + thisJoinPoint.getSourceLocation().getLine();
                }

                StackTraceElement e1 = Thread.currentThread().getStackTrace()[1];
                StackTraceElement e2 = Thread.currentThread().getStackTrace()[2];
                StackTraceElement e3 = Thread.currentThread().getStackTrace()[3];
                StackTraceElement e4 = Thread.currentThread().getStackTrace()[4];
                StackTraceElement e5 = Thread.currentThread().getStackTrace()[5];
                System.out.println("StackTraceElement : 1  ClassName :" + e1.getClassName() + " MethodName: " + e1.getMethodName() + " LineNumber:" + e1.getLineNumber());
                System.out.println("StackTraceElement : 2  ClassName :" + e2.getClassName() + " MethodName: " + e2.getMethodName() + " LineNumber:" + e2.getLineNumber());
                System.out.println("StackTraceElement : 3  ClassName :" + e3.getClassName() + " MethodName: " + e3.getMethodName() + " LineNumber:" + e3.getLineNumber());
                System.out.println("StackTraceElement : 4  ClassName :" + e4.getClassName() + " MethodName: " + e4.getMethodName() + " LineNumber:" + e4.getLineNumber());
                System.out.println("StackTraceElement : 5  ClassName :" + e5.getClassName() + " MethodName: " + e5.getMethodName() + " LineNumber:" + e5.getLineNumber());


                StackTraceElement e = Thread.currentThread().getStackTrace()[3];
                String joinpoint = e.getClassName() + "." + e.getMethodName() + ":" + e.getLineNumber();
                System.out.println("joinpoint" + joinpoint);
                Matcher matcher = reg.matcher(joinpoint);
                System.out.println("joinpoint matcher" + matcher);
                if (matcher.find()) {
                    joinpoint = matcher.group(1) + matcher.group(2);
                }

                executedAdvices.add(new TraceElement(adviceName, joinpoint));
            }
        }
    }

    private static String getAdviceName(JoinPoint jp) {
        String adviceName = "";
        AjType ajType = AjTypeSystem.getAjType(jp.getThis().getClass());
        Method currentAdviceMethod = ((AdviceSignature)jp.getSignature()).getAdvice();
        System.out.println("currentAdviceMethod :" + currentAdviceMethod);
        Field f;
        try {
            f = AdviceImpl.class.getDeclaredField("adviceMethod");
        } catch (NoSuchFieldException var14) {
            return null;
        }

        f.setAccessible(true);
        System.out.println("Field f :" + f);
        if(tracedAdvices.isEmpty()) {
            AdviceKind[] adviceMethod;
            int advice = (adviceMethod = AdviceKind.values()).length;

            for(int var6 = 0; var6 < advice; ++var6) {
                AdviceKind tracedAdvice = adviceMethod[var6];
                Advice[] var12;
                int var11 = (var12 = ajType.getAdvice(new AdviceKind[]{tracedAdvice})).length;

                for(int var10 = 0; var10 < var11; ++var10) {
                    Advice advice1 = var12[var10];

                    try {
                        Method adviceMethod1 = (Method)f.get(advice1);
                        if(adviceMethod1.equals(currentAdviceMethod)) {
                            adviceName = advice1.getName();
                            return adviceName;
                        }
                    } catch (Exception var16) {
                        ;
                    }
                }
            }
        } else {
            Iterator var18 = tracedAdvices.iterator();

            while(var18.hasNext()) {
                String var17 = (String)var18.next();

                try {
                    Advice var19 = ajType.getAdvice(var17);
                    Method var20 = (Method)f.get(var19);
                    if(var20.equals(currentAdviceMethod)) {
                        adviceName = var19.getName();
                        break;
                    }
                } catch (Exception var15) {
                    ;
                }
            }

            if(adviceName.equals("")) {
                adviceName = null;
            }
        }

        return adviceName;
    }

    public static final boolean isOn() {
        return on;
    }

    public class TraceElement {

        private String advice;
        private String joinpoint;


        public TraceElement(String advice, String joinpoint) {
            this.advice = advice;
            this.joinpoint = joinpoint;
        }

        public String getAdvice() {
            return this.advice;
        }

        public String getJoinpoint() {
            return this.joinpoint;
        }
    }
}

