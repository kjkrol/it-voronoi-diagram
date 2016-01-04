package kjkrol.voronoidiagram;

import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Karol Krol
 */
@Data
class RegionPart {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegionPart.class);

    private static final double EPSILON = 0.01;

    private final Region region;
    private double endX;
    private Point2D crossPoint;
    private AtomicBoolean deleteMark = new AtomicBoolean();

    @Builder
    RegionPart(Region region, double endX) {
        this.region = region;
        this.endX = endX;
    }

    void fidIntersection(final RegionPart next) {
        NormalRegion normalRegion;
        Region otherRegion;
        if (this.getRegion() instanceof NormalRegion) {
            normalRegion = (NormalRegion) this.getRegion();
            otherRegion = next.getRegion();

            otherRegion.findIntersection(normalRegion.getParabola()).ifPresent(point2Ds -> {
                final int index = point2Ds.length > 1 ? 1 : 0;
                this.setEndX(point2Ds[index].getX());
                next.setCrossPoint(point2Ds[index]);
                this.findTripleIntersection(point2Ds, EPSILON);
            });

        } else if (next.getRegion() instanceof NormalRegion) {
            normalRegion = (NormalRegion) next.getRegion();
            otherRegion = this.getRegion();

            otherRegion.findIntersection(normalRegion.getParabola()).ifPresent(point2Ds -> {
                this.setEndX(point2Ds[0].getX());
                next.setCrossPoint(point2Ds[0]);
                this.findTripleIntersection(point2Ds, EPSILON);
            });
        }
    }


    private void findTripleIntersection(Point2D[] point2Ds, double precision) {
        Arrays.stream(point2Ds)
                .filter(p -> this.crossPoint!= null && p.distance(this.crossPoint) < precision)
                .peek(o -> LOGGER.info("3pe = {}", o))
                .findFirst()
                .ifPresent(point2D -> deleteMark.set(true));
    }
}
