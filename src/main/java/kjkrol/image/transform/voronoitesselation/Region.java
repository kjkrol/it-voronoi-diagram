package kjkrol.image.transform.voronoitesselation;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Karol Krol
 */
public class Region {

    private final Point2D center;
    private final List<Boundary> boundaries = new ArrayList<>();

    public Region(Point2D center) {
        this.center = center;
    }

    public Point2D getCenter() {
        return center;
    }
}
