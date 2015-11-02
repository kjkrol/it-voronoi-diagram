package kjkrol.voronoidiagram;

import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.DoubleStream;

/**
 * @author Karol Krol
 */
public class VoronoiDiagram {

    private static final double EPSILON = 0.1;

    private final int height;
    private final Deque<Point2D> givenPoints;
    @Getter
    private final Set<Region> regions = new HashSet<>();
    private final List<RegionPart> currentParts = new LinkedList<>();

    private final Region bottomRegion;

    @Builder
    private VoronoiDiagram(int width, int height, List<Point2D> givenPoints) {
        this.height = height;
        Collections.sort(givenPoints, (o1, o2) -> (int) (o1.getY() - o2.getY()));
        this.givenPoints = new LinkedList<>(givenPoints);
        this.bottomRegion = HorizontalRegion.builder().yValue(height).build();
        this.currentParts.addAll(Arrays.asList(
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
        this.insertNewRegion(firstPoint).ifPresent(this.regions::add);
        this.givenPoints.stream().sequential()
                .forEach(point -> {
                    this.moveSweepLine(previous.getAndSet(point.getY()), point.getY());
                    this.insertNewRegion(point).ifPresent(this.regions::add);
                });
        this.moveSweepLine(previous.get(), this.height);
    }

    private Optional<Region> insertNewRegion(Point2D point2D) {
        final NormalRegion newRegion = NormalRegion.builder().center(point2D).build();
        newRegion.refresh(point2D.getY() + EPSILON);
        final ListIterator<RegionPart> listIterator = this.currentParts.listIterator();
        while (listIterator.hasNext()) {
            final RegionPart regionPart = listIterator.next();
            if (point2D.getX() < regionPart.getEndX()) {
                final double oldEndX = regionPart.getEndX();
                regionPart.setEndX(point2D.getX());
                listIterator.add(RegionPart.builder().region(newRegion).endX(point2D.getX()).build());
                listIterator.add(RegionPart.builder().region(regionPart.getRegion()).endX(oldEndX).build());
                return Optional.of(newRegion);
            }
        }
        return Optional.empty();
    }

    private void moveSweepLine(final double startY, final double endY) {
        final long limit = Math.round((endY - startY) / EPSILON);
        DoubleStream.iterate(startY, n -> n + EPSILON)
                .limit(Math.round(limit))
                .forEachOrdered(this::performSweepLineStep);
    }
    
    private void performSweepLineStep(final double sweepLine) {
        final AtomicReference<RegionPart> previousRegionPart = new AtomicReference<>(this.currentParts.get(0));
        this.currentParts.stream()
                .skip(1)
                .forEach(regionPart -> {
                            this.fidIntersection(previousRegionPart.get(), regionPart)
                                    .ifPresent(point2Ds -> {
                                        regionPart.setCrossPoints(point2Ds);
                                        previousRegionPart.get().find3PointsEvent(point2Ds, EPSILON);
                                    });
                            previousRegionPart.set(regionPart);
                        }
                );
        final Iterator<RegionPart> iterator = this.currentParts.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getDeleteMark().get()) {
                iterator.remove();
            }
        }
    }

    private Optional<Point2D[]> fidIntersection(final RegionPart first, final RegionPart second) {
        Optional<Point2D[]> result;
        if (first.getRegion() instanceof NormalRegion) {
            result = second.getRegion().findIntersection(((NormalRegion) first.getRegion()).getParabola());
        } else if (second.getRegion() instanceof NormalRegion) {
            result = first.getRegion().findIntersection(((NormalRegion) second.getRegion()).getParabola());
        } else {
            result = Optional.empty();
        }
        result.ifPresent(o -> first.setEndX(o[0].getX()));
        return result;
    }

}