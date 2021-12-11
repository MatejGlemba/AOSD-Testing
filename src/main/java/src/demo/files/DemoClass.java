package src.demo.files;

public class DemoClass {

    public void m0() {
        System.out.println("m0-body");
    }

    public void m1() {
        System.out.println("m1-body");
    }

    public void m2() {
        System.out.println("m2-body");
        m1();
    }

    public void m3() {
        System.out.println("m3-body");
        throw new RuntimeException("Exception from m3()");
    }

    public String m4() {
        System.out.println("m4-body");
        return "test output of m4()";
    }

    public String m5() {
        System.out.println("m5-body");
        return "test output of m5()";
    }
}
