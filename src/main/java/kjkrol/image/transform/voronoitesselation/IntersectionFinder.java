package kjkrol.image.transform.voronoitesselation;

import javafx.geometry.Point2D;

import java.util.Optional;

/**
 * This class provides algebraic solution for the:
 * <ul>
 * <li>intersection point(s) of two parabolas</li>
 * <li>intersection point(s) of a parabola and a line</li>
 * </ul>
 *
 * @author Karol Krol
 */
public final class IntersectionFinder {

    private IntersectionFinder() {

    }

    public static Optional<Point2D[]> findParabolasIntersection(Parabola parabola1, Parabola parabola2) {
        final double aDiff = parabola1.getA() - parabola1.getA();
        final double bDiff = parabola1.getB() - parabola2.getB();
        final double cDiff = parabola1.getC() - parabola2.getC();
        final double d = bDiff / (2.0 * aDiff);
        final double temp = Math.pow(d, 2) - cDiff / aDiff;
        if (temp < 0) {
            return Optional.empty();
        } else if (temp == 0) {
            final Point2D[] result = {new Point2D(d, parabola1.apply(d))};
            return Optional.of(result);
        } else {
            final double e = Math.sqrt(temp);
            final double deSum = d + e;
            final double deDif = d - e;
            final Point2D[] result = {
                    new Point2D(deSum, parabola1.apply(deSum)),
                    new Point2D(deDif, parabola1.apply(deDif))};
            return Optional.of(result);
        }
    }

    public static Optional<Point2D[]> findIntersectionOfParabolaAndHorizontalLine(Parabola parabola, double yValue) {
        final double temp = Math.pow(parabola.getB(), 2) - 4 * parabola.getA() * (parabola.getC() - yValue);
        if (temp < 0) {
            return Optional.empty();
        } else if (temp == 0) {
            final double xRes = -1.0 * parabola.getB() / (2.0 * parabola.getA());
            final Point2D[] result = {new Point2D(xRes, parabola.apply(xRes))};
            return Optional.of(result);
        } else {
            final double xRes1 = (-1.0 * parabola.getB() - Math.sqrt(temp)) / (2.0 * parabola.getA());
            final double xRes2 = (-1.0 * parabola.getB() + Math.sqrt(temp)) / (2.0 * parabola.getA());
            final Point2D[] result = {
                    new Point2D(xRes1, parabola.apply(xRes1)),
                    new Point2D(xRes2, parabola.apply(xRes2))};
            return Optional.of(result);
        }
    }

    public static Optional<Point2D[]> findIntersectionOfParabolaAndVerticalLine(Parabola parabola, double xValue) {
        final Point2D[] result = {new Point2D(xValue, parabola.apply(xValue))};
        return Optional.of(result);
    }
}
