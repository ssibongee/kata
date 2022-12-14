public class JOuter {

    private int x = 0;

    private void bar() {

    }

    public class JInner {
        public void foo() {
            x = 1;
        }
    }

    public static class JSInner {
        public void foo() {

        }
    }
}
