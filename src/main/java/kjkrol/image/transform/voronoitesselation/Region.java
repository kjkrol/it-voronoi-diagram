package kjkrol.image.transform.voronoitesselation;

import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * @author Karol Krol
 */
@Data
public abstract class Region {

    void refresh(double sweepLineYPos) {
    }

    abstract Optional<Point2D[]> findIntersection(Parabola parabola);
}

class NormalRegion extends Region {
    private final Point2D center;
    private final Map<Region, Boundary> boundaries;
    private Parabola parabola;

    @Builder
    private NormalRegion(Point2D center) {
        this.center = center;
        this.boundaries = new HashMap<>();
    }

    Point2D getCenter() {
        return center;
    }

    Parabola getParabola() {
        return parabola;
    }

    Map<Region, Boundary> getBoundaries() {
        return boundaries;
    }

    @Override
    public void refresh(double sweepLineYPos) {
        this.parabola = Parabola.create(this.center, sweepLineYPos);
    }

    @Override
    public Optional<Point2D[]> findIntersection(Parabola parabola) {
        return IntersectionFinder.findParabolasIntersection(this.parabola, parabola);
    }
}

@Builder
class VerticalRegion extends Region {

    private final double xValue;

    @Override
    public Optional<Point2D[]> findIntersection(Parabola parabola) {
        return IntersectionFinder.findIntersectionOfParabolaAndVerticalLine(parabola, this.xValue);
    }
}

@Builder
class HorizontalRegion extends Region {

    private final double yValue;

    @Override
    public Optional<Point2D[]> findIntersection(Parabola parabola) {
        return IntersectionFinder.findIntersectionOfParabolaAndHorizontalLine(parabola, this.yValue);
    }
}


