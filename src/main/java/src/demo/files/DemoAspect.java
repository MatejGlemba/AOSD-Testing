package src.demo.files;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

@Aspect
public class DemoAspect {

    @Pointcut("call(void src.demo.files.DemoClass.m1())")
    private void aspectCallM1() {
    }

    @Pointcut("execution(void src.demo.files.DemoClass.m1())")
    private void aspectExecutionM1() {
    }

    @Pointcut("call(void src.demo.files.DemoClass.m2())")
    private void aspectCallM2() {
    }

    @Pointcut("execution(void src.demo.files.DemoClass.m2())")
    private void aspectExecutionM2() {
    }

    @Pointcut("call(void src.demo.files.DemoClass.m3())")
    private void aspectCallM3() {
    }

    @Pointcut("execution(void src.demo.files.DemoClass.m3())")
    private void aspectExecutionM3() {
    }

    @Pointcut("call(String src.demo.files.DemoClass.m4())")
    private void aspectCallM4() {
    }

    @Pointcut("execution(String src.demo.files.DemoClass.m4())")
    private void aspectExecutionM4() {
    }

    @Pointcut("call(String src.demo.files.DemoClass.m5())")
    private void aspectCallM5() {
    }

    @Pointcut("execution(String src.demo.files.DemoClass.m5())")
    private void aspectExecutionM5() {
    }

    @Pointcut("cflow(execution(void src.demo.files.DemoClass.m2()))")
    private void aspectCflowOfM2Execution() {
    }

    @Pointcut("cflow(call(void src.demo.files.DemoClass.m2()))")
    private void aspectCflowOfM2Call() {
    }


    @Before(value = "aspectCallM1() && aspectCflowOfM2Call()")
    public void aspect() {
        System.out.println("aspektBeforeM1CallInCFlowOfM2Call");
    }

    @Before(value = "aspectCallM2()")
    public void aspect2() {
        System.out.println("aspektBeforeM2Call");
    }

    @After(value = "aspectExecutionM1()")
    public void aspect3() {
        System.out.println("aspektAfterM1Execution");
    }

    @AfterReturning(value = "aspectExecutionM4()", returning="returnVal")
    public void aspect4(String returnVal) {
        System.out.println("aspektAfterReturningM4Execution" + returnVal);
    }

    @AfterThrowing(value = "aspectExecutionM3()", throwing = "ex")
    public void aspect5(Exception ex) throws Throwable {
        System.out.println("aspektAfterThrowingM3Execution: " + ex);
    }

    @Around(value = "aspectCallM5()")
    public Object aspect6(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("aspektAroundM5CallBefore");
        Object returnVal = proceedingJoinPoint.proceed();
        System.out.println("aspektAroundM5CallAfter");
        return returnVal + " around aspekt";
    }
}
