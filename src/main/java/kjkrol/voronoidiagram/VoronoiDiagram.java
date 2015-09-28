package kjkrol.voronoidiagram;

import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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

    public void start() {
        final AtomicReference<Point2D> prevPointRef = new AtomicReference<>(this.points.poll());
        this.insertNewRegion(prevPointRef.get(), prevPointRef.get().getX() + EPSILON).ifPresent(regions::add);
        points.stream().sequential().forEach(point -> {
            for (double sweepLine = prevPointRef.get().getY(); sweepLine < point.getY(); sweepLine += EPSILON) {
                sweepLineStep(sweepLine);
            }
            this.insertNewRegion(point, point.getX() + EPSILON).ifPresent(regions::add);
            prevPointRef.set(point);
        });
    }

    private void init() {
        this.workingList.addAll(Arrays.asList(
                RegionPart.builder().region(leftRegion).endX(0.0).build(),
                RegionPart.builder().region(topRegion).endX(this.width).build(),
                RegionPart.builder().region(rightRegion).endX(this.width).build()));
    }

    private void sweepLineStep(double sweepLine) {
        final ListIterator<RegionPart> listIterator = this.workingList.listIterator();
        RegionPart first = listIterator.next();
        first.getRegion().refresh(sweepLine);
        RegionPart second = listIterator.next();
        second.getRegion().refresh(sweepLine);
        RegionPart third = listIterator.next();
        third.getRegion().refresh(sweepLine);

        this.findEvent(first, second, third);

        while (listIterator.hasNext()) {
            first = second;
            second = third;
            third = listIterator.next();
            third.getRegion().refresh(sweepLine);
            this.findEvent(first, second, third);
        }
    }

    private void findEvent(final RegionPart first, final RegionPart second, final RegionPart third) {
        fidIntersection(first, second);
        fidIntersection(second, third);

        //TODO: continue...
    }

    private Optional<Point2D[]> fidIntersection(final RegionPart first, final RegionPart second) {
        Optional<Point2D[]> result;
        if (first.getRegion() instanceof NormalRegion) {
            return second.getRegion().findIntersection(((NormalRegion) first.getRegion()).getParabola());
        } else if (second.getRegion() instanceof NormalRegion) {
            return first.getRegion().findIntersection(((NormalRegion) second.getRegion()).getParabola());
        } else {
            result = Optional.empty();
        }
        result.ifPresent(o -> first.setEndX(o[0].getX()));
        return result;
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
