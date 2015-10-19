package kjkrol.voronoidiagram;

import javafx.geometry.Point2D;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Karol Krol
 */
public class RegionTest {

    private static final double DELTA = 0.0001;

    @Test
    public void normalRegionTest() {
        final NormalRegion region = NormalRegion.builder().center(new Point2D(3, -1)).build();
        region.refresh(-3.0);
        final Parabola parabola = Parabola.builder().a(1.0).b(-2.0).c(-1.0).build();
        final Point2D[] points = region.findIntersection(parabola)
                .orElseThrow(() -> new AssertionError("Intersection not found."));
        assertEquals(-1.0, points[0].getX(), DELTA);
        assertEquals(2.0, points[0].getY(), DELTA);
        assertEquals(1.6666666666666667, points[1].getX(), DELTA);
        assertEquals(-1.5555555555555554, points[1].getY(), DELTA);
    }

    @Test
    public void verticalRegionTest() {
        final Region region = VerticalRegion.builder().xValue(0.0).build();
        final Parabola parabola = Parabola.formalDefinitionBuilder()
                .focusPoint(new Point2D(3, -1))
                .horizontalLine(-3.0)
                .build();
        final Point2D[] points = region.findIntersection(parabola)
                .orElseThrow(() -> new AssertionError("Intersection not found."));
        assertEquals(new Point2D(0, 0.25), points[0]);
    }

    @Test
    public void horizontalRegionTest() {
        final Region region = HorizontalRegion.builder().yValue(-1.0).build();
        final Parabola parabola = Parabola.formalDefinitionBuilder()
                .focusPoint(new Point2D(3, -1))
                .horizontalLine(-3.0)
                .build();
        final Point2D[] points = region.findIntersection(parabola)
                .orElseThrow(() -> new AssertionError("Intersection not found."));
        assertEquals(new Point2D(1.0, -1.0), points[0]);
        assertEquals(new Point2D(5.0, -1.0), points[1]);
    }

}
