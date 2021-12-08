import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class DemoAspect {


    @Pointcut("cflow(execution(void DemoClass.m2()))")
    private void aspectTraces() {
    }

    @Pointcut("execution(void DemoClass.m1())")
    private void aspectExe() {
    }

    @Before(value = "aspectExe() && aspectTraces()")
    public void aspect() {
        System.out.println("aspekt");
    }

    /*@Pointcut("execution(void DemoClass.m2())")
    private void aspectExe2() {
    }

    @Before(value = "aspectExe2()")
    public void aspect2() {
        System.out.println("aspekt");
    }

    @Pointcut("execution(void DemoClass.m1())")
    private void aspectExe3() {
    }

    @Before(value = "aspectExe3()")
    public void aspect3() {
        System.out.println("aspekt");
    }*/
}
