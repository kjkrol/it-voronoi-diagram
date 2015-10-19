package kjkrol.voronoidiagram;

import javafx.geometry.Point2D;
import junit.framework.Assert;
import org.junit.Test;

import java.util.function.Function;

/**
 * @author Karol Krol
 */
public class ParabolaTest {

    @Test
    public void parabolaTest() {
        final Function<Double, Double> parabola = Parabola.formalDefinitionBuilder()
                .focusPoint(new Point2D(3, -1))
                .horizontalLine(-3.0)
                .build();
        final double yValue = parabola.apply(1.0);
        Assert.assertEquals(-1.0, yValue, 0.01);
    }
}
