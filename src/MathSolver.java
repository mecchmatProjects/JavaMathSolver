public class MathSolver {
    public static void main(String[] args) {

    }

    // GEO-01: Distance between points
    public static void calculateDistance(String[] args) {
        double x1 = Double.parseDouble(args[1]);
        double y1 = Double.parseDouble(args[2]);
        double x2 = Double.parseDouble(args[3]);
        double y2 = Double.parseDouble(args[4]);

        double d = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        System.out.println("distance = " + d);
    }

    // GEO-02: Circle area
    public static void calculateCircleArea(String[] args) {
        double r = Double.parseDouble(args[1]);
        if (r < 0) {
            System.out.println("Error: radius cannot be negative");
            return;
        }
        double s = Math.PI * r * r;
        System.out.println("circle area = " + s);
    }

    // GEO-03: Circle circumference
    public static void calculateCircleCircumference(String[] args) {
        double r = Double.parseDouble(args[1]);
        if (r < 0) {
            System.out.println("Error: radius cannot be negative");
            return;
        }
        double c = 2 * Math.PI * r;
        System.out.println("circle circumference = " + c);
    }

}
