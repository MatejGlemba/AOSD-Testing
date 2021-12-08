
public class DemoClass {

    public void m1() {
        System.out.println("m1-body");
    }

    public void m2() {
        System.out.println("m2-body");
        m1();
    }
}
