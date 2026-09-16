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
    
    // GEO-04: Rectangle area  
    public static void calculateRectangleArea(String[] args) {  
        double a = Double.parseDouble(args[1]);  
        double b = Double.parseDouble(args[2]);  
        if (a < 0 || b < 0) {  
            System.out.println("Error: side lengths cannot be negative");  
            return;  
        }  
        double s = a * b;  
        System.out.println("rectangle area = " + s);  
    }
    
    // GEO-05: Rectangle perimeter
    public static void calculateRectanglePerimeter(String[] args) {
        double a = Double.parseDouble(args[1]);
        double b = Double.parseDouble(args[2]);
        if (a < 0 || b < 0) {
            System.out.println("Error: side lengths cannot be negative");
            return;
        }
        double p = 2 * (a + b);
        System.out.println("rectangle perimeter = " + p);
    }

    // GEO-06: Euclidean distance from origin  
    public static void calculateOriginDistance(String[] args) {  
        double x = Double.parseDouble(args[1]);  
        double y = Double.parseDouble(args[2]);

        double d = Math.sqrt(x * x + y * y);  
        System.out.println("origin distance = " + d);  
    }

}
