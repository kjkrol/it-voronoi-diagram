package kjkrol.voronoidiagram;

import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Karol Krol
 */
@Data
class RegionPart {

    private static final Point2D[] EMPTY_ARRAY = new Point2D[0];

    private final Region region;
    private double endX;
    private Point2D[] crossPoints = EMPTY_ARRAY;
    private AtomicBoolean deleteMark = new AtomicBoolean();

    @Builder
    public RegionPart(Region region, double endX) {
        this.region = region;
        this.endX = endX;
    }

    public void find3PointsEvent(Point2D[] point2Ds, double precision) {
        Arrays.stream(this.crossPoints)
                .filter(point -> Arrays.stream(point2Ds)
                        .anyMatch(p -> p.distance(point) < precision))
                .peek(System.out::println)
                .findFirst()
                .ifPresent(point2D -> deleteMark.set(true));
    }
}
