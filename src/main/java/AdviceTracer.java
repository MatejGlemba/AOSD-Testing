import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.aspectj.internal.lang.reflect.AdviceImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;

import static java.lang.Class.forName;

@Aspect
public class AdviceTracer {

    private static boolean on = false;
    private static List<TraceElement> executedAdvices;
    private static Map<String, List<StackTraceElement>> stackTraceForAdvice = new HashMap<>();
    private static Map<String, AdviceWithAspect> tracedAdvicesWithAdviceKind = new HashMap<>();
    private static List<String> tracedAdvices = new ArrayList<>();
    private static Pattern reg = Pattern.compile("(.*)_aroundBody\\d*(:\\d*)");

    public static void setAdviceTracerOn() {
        stackTraceForAdvice = new HashMap<>();
        executedAdvices = new ArrayList();
        tracedAdvicesWithAdviceKind = new HashMap<>();
        on = true;
    }

    public static void setAdviceTracerOff() {
        tracedAdvices = new ArrayList();
        on = false;
    }

    public static void setTracedAdvices(List<String> newTracedAdvices) {
        tracedAdvices = newTracedAdvices;
        for (String advice : newTracedAdvices) {
            tracedAdvicesWithAdviceKind.put(advice, null);
        }
    }

    public static void addTracedAdvice(String advice) {
        tracedAdvicesWithAdviceKind.put(advice, null);
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
        Iterator<TraceElement> iterator = executedAdvices.iterator();
        TraceElement traceElement;
        do {
            if(!iterator.hasNext()) {
                return false;
            }
            traceElement = iterator.next();
        } while(!traceElement.getAdvice().equals(advice) || !traceElement.getJoinpoint().equals(joinpoint));
        return true;
    }

    public static String getAdviceKind(String advice) {
        if (tracedAdvicesWithAdviceKind.containsKey(advice)) {
            return tracedAdvicesWithAdviceKind.get(advice).getAdvice().getKind().name().toLowerCase();
        }
        return "";
    }

    public static String getAdviceKindBasedOnMethod(String method) {
        for (Map.Entry<String, List<StackTraceElement>> entry : stackTraceForAdvice.entrySet()) {
            for (StackTraceElement element : entry.getValue()) {
                if (element.getMethodName().equals(method)) {
                    if (tracedAdvicesWithAdviceKind.containsKey(entry.getKey())) {
                        return tracedAdvicesWithAdviceKind.get(entry.getKey()).getAdvice().getKind().name().toLowerCase();
                    }
                }
            }
        }
        return "";
    }

    public static boolean methodWasAdvicedBy(String method, String advice) {
        if (tracedAdvicesWithAdviceKind.containsKey(advice)) {
            for (Map.Entry<String, List<StackTraceElement>> entry : stackTraceForAdvice.entrySet()) {
                for (StackTraceElement element : entry.getValue()) {
                    if (element.getMethodName().equals(method)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean methodWasAdvicedByAdviceKind(String method, String advice, String adviceKind) {
        if (tracedAdvicesWithAdviceKind.containsKey(advice)) {
            for (Map.Entry<String, List<StackTraceElement>> entry : stackTraceForAdvice.entrySet()) {
                for (StackTraceElement element : entry.getValue()) {
                    if (element.getMethodName().equals(method) && advice.equals(entry.getKey())) {
                        if (tracedAdvicesWithAdviceKind.containsKey(entry.getKey())
                                && tracedAdvicesWithAdviceKind.get(advice).getAdvice().getKind().name().toLowerCase().equals(adviceKind)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static String getPointcutExpressionByAdvice(String advice) {
        if (tracedAdvicesWithAdviceKind.containsKey(advice)) {
            Class aspect = tracedAdvicesWithAdviceKind.get(advice).getAspect();
            Advice advice1 = tracedAdvicesWithAdviceKind.get(advice).getAdvice();
            Method[] declaredMethods = aspect.getDeclaredMethods();
            String pointcutExpression = advice1.getPointcutExpression().asString();
            String[] ArrayOfPointcuts = pointcutExpression.split(" && ");
            for (Method method : declaredMethods) {
                for (String pointcutExpr : ArrayOfPointcuts) {
                    if (pointcutExpr.split("\\(")[0].equals(method.getName())) {
                        Annotation pointcut = Arrays.stream(method.getAnnotations())
                                .filter(e -> e.toString().contains("Pointcut"))
                                .findFirst()
                                .orElse(null);
                        if (pointcut != null) {
                            int start = pointcut.toString().indexOf("\"") + 1;
                            int end = pointcut.toString().indexOf("\", argNames");
                            return pointcut.toString().substring(start, end);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean getTypeOfPointcutInAdvice(String advice, String typeOfPointcut) {
        if (tracedAdvicesWithAdviceKind.containsKey(advice)) {
            String pointcutExpressionByAdvice = getPointcutExpressionByAdvice(advice);
            return pointcutExpressionByAdvice != null && pointcutExpressionByAdvice.contains(typeOfPointcut);
        }
        return false;
    }

    public static List<String> getControlFlowOfMethod(String method, List<String> expectedCflow) {
        for (Map.Entry<String, List<StackTraceElement>> entry : stackTraceForAdvice.entrySet()) {
            for (StackTraceElement element : entry.getValue()) {
                if (element.getMethodName().equals(method)) {
                    List<String> actualCFlow = entry.getValue().stream()
                            .map(StackTraceElement::getMethodName)
                            .collect(Collectors.toList());
                    if (actualCFlow.containsAll(expectedCflow)) {
                        return expectedCflow;
                    } else {
                        int beforeTracerIndex = actualCFlow.indexOf("beforeTracer");
                        return actualCFlow.subList(beforeTracerIndex, expectedCflow.size() + beforeTracerIndex);
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    @Before("!within(AdviceTracer) && adviceexecution()")
    public void beforeTracer(JoinPoint thisJoinPoint) {
        System.out.println("before tracer " + thisJoinPoint);
        if(isOn()) {
            String adviceName = getAdviceName(thisJoinPoint);
            if (adviceName != null) {
                if (adviceName.equals("")) {
                    adviceName = thisJoinPoint.getSignature().getDeclaringTypeName() + ":" + thisJoinPoint.getSourceLocation().getLine();
                }

                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                List<StackTraceElement> stackTraceElementList = new ArrayList<>(Arrays.asList(stackTrace).subList(0, 10));
                if (!stackTraceForAdvice.containsKey(adviceName)) {
                    stackTraceForAdvice.put(adviceName, stackTraceElementList);
                    List<String> temp = stackTraceElementList.stream().map(StackTraceElement::getMethodName).collect(Collectors.toList());
                    int indexOfAspectClass = temp.indexOf("beforeTracer") + 1;
                    String aspectClassName = stackTraceElementList.get(indexOfAspectClass).getClassName();
                    try {
                        tracedAdvicesWithAdviceKind.get(adviceName).setAspect(forName(aspectClassName));
                    } catch (ClassNotFoundException exception) {
                        System.out.println("Class with name: " + aspectClassName + " was not found.");
                    }

                }

                for (StackTraceElement e : stackTraceElementList) {
                    System.out.println("StackTraceElement : 1  ClassName :" + e.getClassName() + " MethodName: " + e.getMethodName() + " LineNumber:" + e.getLineNumber());
                }

                StackTraceElement e = Thread.currentThread().getStackTrace()[3];
                String joinpoint = e.getClassName() + "." + e.getMethodName() + ":" + e.getLineNumber();
                //System.out.println("joinpoint" + joinpoint);
                Matcher matcher = reg.matcher(joinpoint);
                //System.out.println("joinpoint matcher" + matcher);
                if (matcher.find()) {
                    joinpoint = matcher.group(1) + matcher.group(2);
                }
                executedAdvices.add(new TraceElement(adviceName, joinpoint));
            }
        }
    }

    @After("!within(AdviceTracer) && adviceexecution()")
    public void afterTracer(JoinPoint thisJoinPoint) {
        System.out.println("after tracer " + thisJoinPoint);
    }

    private static String getAdviceName(JoinPoint jp) {
        String adviceName = "";
        AjType ajType = AjTypeSystem.getAjType(jp.getThis().getClass());
        Method currentAdviceMethod = ((AdviceSignature)jp.getSignature()).getAdvice();
        //System.out.println("currentAdviceMethod :" + currentAdviceMethod);
        Field field;
        try {
            field = AdviceImpl.class.getDeclaredField("adviceMethod");
        } catch (NoSuchFieldException e) {
            return null;
        }
        field.setAccessible(true);
        //System.out.println("Field f :" + f);
        if(tracedAdvices.isEmpty()) {
            AdviceKind[] adviceMethodArray = AdviceKind.values();
            ///System.out.println("adviceMethodArray :" + adviceMethodArray);
            for (AdviceKind tracedAdvice : adviceMethodArray) {
               // System.out.println("tracedAdvice :" + tracedAdvice);
                Advice[] adviceArray = ajType.getAdvice(tracedAdvice);
                for (Advice advice : adviceArray) {
                 //   System.out.println("advice :" + advice);
                    try {
                        Method adviceMethod1 = (Method) field.get(advice);
                      //  System.out.println("field :" + field);
                     //   System.out.println("adviceMethod1 :" + adviceMethod1);
                        if (adviceMethod1.equals(currentAdviceMethod)) {
                            adviceName = advice.getName();
                            tracedAdvicesWithAdviceKind.put(adviceName, new AdviceWithAspect(advice));
                           // System.out.println("advice pointcutExpression :" + advice.get);
                            return adviceName;
                        }
                    } catch (Exception ignored) {}
                }
            }
        } else {
            for (String tracedAdvice : tracedAdvices) {
                try {
                    Advice advice = ajType.getAdvice(tracedAdvice);
                    Method method = (Method) field.get(advice);
                    if (method.equals(currentAdviceMethod)) {
                        adviceName = advice.getName();
                        tracedAdvicesWithAdviceKind.put(tracedAdvice, new AdviceWithAspect(advice));
                        break;
                    }
                } catch (Exception ignored) {}
            }
            if(adviceName.equals("")) {
                adviceName = null;
            }
        }
        return adviceName;
    }

    public static boolean isOn() {
        return on;
    }

    public static class TraceElement {

        private String advice;
        private String joinpoint;


        TraceElement(String advice, String joinpoint) {
            this.advice = advice;
            this.joinpoint = joinpoint;
        }

        String getAdvice() {
            return this.advice;
        }

        String getJoinpoint() {
            return this.joinpoint;
        }
    }

    public static class AdviceWithAspect {

        private Advice advice;
        private Class aspect;


        AdviceWithAspect(Advice advice, Class aspect) {
            this.advice = advice;
            this.aspect = aspect;
        }

        AdviceWithAspect(Advice advice) {
            this.advice = advice;
        }

        void setAspect(Class aspect) {
            this.aspect = aspect;
        }

        Advice getAdvice() {
            return this.advice;
        }

        Class getAspect() {
            return this.aspect;
        }
    }
}

