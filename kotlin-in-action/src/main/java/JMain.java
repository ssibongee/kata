public class JMain {

    public static void main(String[] args) {
        JOuter.JInner inner = new JOuter().new JInner();
        JOuter.JSInner sinner = new JOuter.JSInner();
    }
}
