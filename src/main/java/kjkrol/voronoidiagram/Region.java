package kjkrol.voronoidiagram;

import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

/**
 * @author Karol Krol
 */
public interface Region {

    Optional<Point2D[]> findIntersection(Parabola parabola);
}

@Getter
@ToString
class NormalRegion implements Region {

    private final Point2D center;
    private final Map<Region, Boundary> boundaries;
    private Parabola parabola;

    @Builder
    private NormalRegion(Point2D center) {
        this.center = center;
        this.boundaries = new HashMap<>();
    }

    public void refresh(double sweepLineYPos) {
        this.parabola = Parabola.formalDefinitionBuilder()
                .focusPoint(this.center)
                .horizontalLine(sweepLineYPos)
                .build();
    }

    @Override
    public Optional<Point2D[]> findIntersection(Parabola thatParabola) {
        final double aDiff = this.getParabola().getA() - thatParabola.getA();
        final double bDiff = this.getParabola().getB() - thatParabola.getB();
        final double cDiff = this.getParabola().getC() - thatParabola.getC();
        final double d = bDiff / (2.0 * aDiff);
        final double temp = Math.pow(d, 2) - cDiff / aDiff;
        if (temp < 0) {
            return Optional.empty();
        } else if (temp == 0) {
            final Point2D[] result = {new Point2D(d, this.getParabola().apply(d))};
            return Optional.of(result);
        } else {
            final double e = Math.sqrt(temp);
            final double deSum = -d + e;
            final double deDif = -d - e;
            final Point2D[] result = {
                    new Point2D(deDif, this.getParabola().apply(deDif)),
                    new Point2D(deSum, this.getParabola().apply(deSum))};
            return Optional.of(result);
        }
    }
}

@Builder
@ToString
class VerticalRegion implements Region {

    private final double xValue;

    @Override
    public Optional<Point2D[]> findIntersection(Parabola thatParabola) {
        final Point2D[] result = {new Point2D(xValue, thatParabola.apply(xValue))};
        return Optional.of(result);
    }
}

@Builder
@ToString
class HorizontalRegion implements Region {

    private final double yValue;

    @Override
    public Optional<Point2D[]> findIntersection(Parabola thatParabola) {
        final double temp = Math.pow(thatParabola.getB(), 2) - 4 * thatParabola.getA() * (thatParabola.getC() - yValue);
        if (temp < 0) {
            return Optional.empty();
        } else if (temp == 0) {
            final double xRes = -1.0 * thatParabola.getB() / (2.0 * thatParabola.getA());
            final Point2D[] result = {new Point2D(xRes, this.yValue)};
            return Optional.of(result);
        } else {
            final double xRes1 = (-1.0 * thatParabola.getB() - Math.sqrt(temp)) / (2.0 * thatParabola.getA());
            final double xRes2 = (-1.0 * thatParabola.getB() + Math.sqrt(temp)) / (2.0 * thatParabola.getA());
            final Point2D[] result = {
                    new Point2D(xRes1, this.yValue),
                    new Point2D(xRes2, this.yValue)};
            return Optional.of(result);
        }
    }
}