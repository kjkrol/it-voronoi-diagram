package kjkrol.image.transform.voronoitesselation;

import javafx.geometry.Point2D;

/**
 * @author Karol Krol
 */
public class Boundary {

    private Point2D point1;
    private Point2D point2;

    public Point2D getPoint1() {
        return point1;
    }

    public Boundary setPoint1(Point2D point1) {
        this.point1 = point1;
        return this;
    }

    public Point2D getPoint2() {
        return point2;
    }

    public Boundary setPoint2(Point2D point2) {
        this.point2 = point2;
        return this;
    }
}
