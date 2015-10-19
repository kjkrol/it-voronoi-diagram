package kjkrol.voronoidiagram;

import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.function.Function;

/**
 * Parabola function.
 *
 * @author Karol Krol
 */
@Builder
@Getter
@ToString
public class Parabola implements Function<Double, Double> {

    private final double a;
    private final double b;
    private final double c;

    @Builder(builderClassName = "FormalDefinitionBuilder", builderMethodName = "formalDefinitionBuilder")
    private static Parabola create(final Point2D focusPoint, final double horizontalLine) {
        final double bcSum = focusPoint.getY() + horizontalLine;
        final double bcDif = focusPoint.getY() - horizontalLine;
        final double a = 1.0 / 2.0 / bcDif;
        final double b = -focusPoint.getX() / bcDif;
        final double c = Math.pow(focusPoint.getX(), 2.0) * a + bcSum / 2.0;
        return new Parabola(a, b, c);
    }

    @Override
    public Double apply(Double x) {
        return Math.pow(x, 2.0) * this.a + x * this.b + this.c;
    }
}
