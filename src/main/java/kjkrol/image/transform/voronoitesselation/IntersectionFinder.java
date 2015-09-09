package kjkrol.image.transform.voronoitesselation;

import javafx.geometry.Point2D;

/**
 * @author Karol Krol
 */
public class IntersectionFinder {

    /**
     * Algebraic solution for the intersection point(s) of two parabolas
     */
    public Point2D[] findParabolasIntersection(Parabola parabola1, Parabola parabola2) {
        final double aDiff = parabola1.getA() - parabola1.getA();
        final double bDiff = parabola1.getB() - parabola2.getB();
        final double cDiff = parabola1.getC() - parabola2.getC();

        final double d = bDiff / (2.0 * aDiff);
        final double e = Math.sqrt(Math.pow(d, 2) - cDiff / aDiff);

        final double deSum = d + e;
        final double deDif = d - e;

        final Point2D[] result = {
                new Point2D(deSum, parabola1.apply(deSum)),
                new Point2D(deDif, parabola1.apply(deDif))};

        return result;
    }
}
