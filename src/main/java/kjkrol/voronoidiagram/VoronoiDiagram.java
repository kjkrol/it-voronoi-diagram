package kjkrol.voronoidiagram;

import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * @author Karol Krol
 */
public class VoronoiDiagram {

    private static final Logger LOGGER = LoggerFactory.getLogger(VoronoiDiagram.class);

    private static final double EPSILON = 0.1;

    private final int height;
    private final Deque<Point2D> givenPoints;
    @Getter
    private final Set<Region> regions = new HashSet<>();
    private final List<RegionPart> regionsParts = new LinkedList<>();

    private final Region bottomRegion;

    @Builder
    private VoronoiDiagram(int width, int height, List<Point2D> givenPoints) {
        this.height = height;
        Collections.sort(givenPoints, (o1, o2) -> (int) (o1.getY() - o2.getY()));
        this.givenPoints = new LinkedList<>(givenPoints);
        this.bottomRegion = HorizontalRegion.builder().yValue(height).build();
        this.regionsParts.addAll(Arrays.asList(
                        RegionPart.builder()
                                .region(VerticalRegion.builder().xValue(0.0).build())
                                .endX(0.0)
                                .build(),
                        RegionPart.builder()
                                .region(HorizontalRegion.builder().yValue(0.0).build())
                                .endX(width)
                                .build(),
                        RegionPart.builder()
                                .region(VerticalRegion.builder().xValue(width).build())
                                .endX(width)
                                .build()
                )
        );
    }

    public void start() {
        final Point2D firstPoint = this.givenPoints.poll();
        final AtomicReference<Double> previous = new AtomicReference<>(firstPoint.getY());
        this.insertNewRegionPart(firstPoint);
        this.givenPoints.stream().sequential()
                .forEach(point -> {
                    final double end = point.getY();
                    final double start = previous.getAndSet(end);
                    this.moveSweepLine(start, end);
                    this.insertNewRegionPart(point);
                });
        this.moveSweepLine(previous.get(), this.height);
    }

    private void insertNewRegionPart(Point2D point2D) {
        LOGGER.info("insertNewRegionPart");
        final NormalRegion newRegion = NormalRegion.builder().center(point2D).build();
        newRegion.refresh(point2D.getY() + EPSILON);
        final ListIterator<RegionPart> listIterator = this.regionsParts.listIterator();
        while (listIterator.hasNext()) {
            final RegionPart regionPart = listIterator.next();
            if (point2D.getX() < regionPart.getEndX()) {
                final double oldEndX = regionPart.getEndX();
                regionPart.setEndX(point2D.getX());
                listIterator.add(RegionPart.builder().region(newRegion).endX(point2D.getX()).build());
                listIterator.add(RegionPart.builder().region(regionPart.getRegion()).endX(oldEndX).build());
                this.regions.add(newRegion);
                break;
            }
        }
    }

    private void moveSweepLine(final double startY, final double endY) {
        final long limit = Math.round((endY - startY) / EPSILON);
        DoubleStream.iterate(startY, n -> n + EPSILON)
                .limit(Math.round(limit))
                .forEachOrdered(sweepLine -> {
                    this.refreshRegions(sweepLine);
                    this.findRegionPartsIntersections();
                    this.removeUnusedRegionParts();
                    LOGGER.info("regionsParts => {}", regionsParts.stream()
                            .map(rp -> rp.getRegion().getClass().getSimpleName() + " [" + rp.getEndX() + ']')
                            .collect(Collectors.toList())
                    );
                });
    }

    private void refreshRegions(final double sweepLine) {
        this.regions.stream().forEachOrdered(region -> ((NormalRegion) region).refresh(sweepLine));
    }

    private void findRegionPartsIntersections() {
        final AtomicReference<RegionPart> previousRegionPart = new AtomicReference<>(this.regionsParts.get(0));
        this.regionsParts.stream()
                .skip(1)
                .forEach(regionPart -> previousRegionPart.getAndSet(regionPart).fidIntersection(regionPart));
    }

    private void removeUnusedRegionParts() {
        final Iterator<RegionPart> iterator = this.regionsParts.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getDeleteMark().get()) {
                iterator.remove();
            }
        }
    }

}