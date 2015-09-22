package kjkrol.image.transform.voronoitesselation;

import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.DoubleStream;

/**
 * @author Karol Krol
 */
public class VoronoiDiagram {

    private static final double EPSILON = 0.1;

    @Data
    @Builder
    private static class RegionPart {
        private final Region region;
        private double endX;
    }

    private final int width;
    private final int height;
    private final Deque<Point2D> points;
    private final Set<Region> regions = new HashSet<>();
    private final List<RegionPart> workingList = new ArrayList<>();

    private final Region topRegion;
    private final Region bottomRegion;
    private final Region leftRegion;
    private final Region rightRegion;

    @Builder
    private VoronoiDiagram(int width, int height, List<Point2D> points) {
        this.width = width;
        this.height = height;
        Collections.sort(points, (o1, o2) -> (int) (o1.getY() - o2.getY()));
        this.points = new LinkedList<>(points);

        this.topRegion = HorizontalRegion.builder().yValue(0.0).build();
        this.bottomRegion = HorizontalRegion.builder().yValue(height).build();
        this.leftRegion = VerticalRegion.builder().xValue(0.0).build();
        this.rightRegion = VerticalRegion.builder().xValue(width).build();
    }

    private void init() {
        this.workingList.addAll(Arrays.asList(
                RegionPart.builder().region(leftRegion).endX(0.0).build(),
                RegionPart.builder().region(topRegion).endX(this.width).build(),
                RegionPart.builder().region(rightRegion).endX(this.width).build()));
    }

    public void start() {

        final AtomicReference<Point2D> prevPointRef = new AtomicReference<>(this.points.poll());
        this.insertNewRegion(prevPointRef.get(), prevPointRef.get().getX() + EPSILON).ifPresent(regions::add);
        points.stream().sequential().forEach(point -> {
            for (double sweepLine = prevPointRef.get().getY(); sweepLine < point.getY(); sweepLine += EPSILON) {
                scan(sweepLine);
            }
            this.insertNewRegion(point, point.getX() + EPSILON).ifPresent(regions::add);
            prevPointRef.set(point);
        });

    }

    private void scan(double sweepLine) {
        final ListIterator<RegionPart> listIterator = this.workingList.listIterator();
        RegionPart first = listIterator.next();
        RegionPart second = listIterator.next();
        RegionPart third = listIterator.next();

        fidIntersection(first.getRegion(), second.getRegion()).ifPresent(o -> first.setEndX(o.));
        fidIntersection(second.getRegion(), third.getRegion());

        while (listIterator.hasNext()) {
            first = second;
            second = third;
            third = listIterator.next();
        }
    }

    private Optional<Point2D[]> fidIntersection(Region first, Region second) {
        if (second instanceof VerticalRegion || second instanceof HorizontalRegion) {
            if (first instanceof NormalRegion) {
                return second.findIntersection(((NormalRegion) first).getParabola());
            }
        } else {
            if (second instanceof NormalRegion) {
                return first.findIntersection(((NormalRegion) second).getParabola());
            }
        }
        return Optional.empty();
    }

    private Optional<Region> insertNewRegion(Point2D point2D, double sweepLine) {
        final Region newRegion = NormalRegion.builder().center(point2D).build();
        newRegion.refresh(sweepLine);
        final ListIterator<RegionPart> listIterator = this.workingList.listIterator();
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

}
