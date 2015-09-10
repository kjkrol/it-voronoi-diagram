package kjkrol.image.transform.voronoitesselation;

import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * @author Karol Krol
 */
public class VoronoiDiagram {

    private static final double EPSILON = 0.1;

    private final int width;
    private final int height;
    private final Deque<Point2D> points;
    private final Set<Region> regions;
    private final List<RegionPart> actionList;

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
        this.regions = new HashSet<>(points.size());
        this.actionList = new ArrayList<>();
    }

    private void init() {
        this.actionList.addAll(Arrays.asList(new RegionPart[]{
                RegionPart.builder().region(leftRegion).endX(0.0).build(),
                RegionPart.builder().region(topRegion).endX(this.width).build(),
                RegionPart.builder().region(rightRegion).endX(this.width).build()}));
    }

    public void start() {
        final Point2D first = points.poll();
        final Region region = NormalRegion.builder().center(first).build();
        this.regions.add(region);
        this.insert(first);

        for (double sweepLine = first.getY(); sweepLine < this.height; sweepLine += EPSILON) {

        }
    }

    private void step() {

    }

    private void insert(Point2D point2D) {
        final ListIterator<RegionPart> listIterator = this.actionList.listIterator();
        while(listIterator.hasNext()) {
            final RegionPart regionPart = listIterator.next();
            if (point2D.getX() < regionPart.getEndX()) {
                final double oldEndX = regionPart.getEndX();
                regionPart.setEndX(point2D.getX());
                final Region newRegion = NormalRegion.builder().center(point2D).build();
                this.actionList.add(new RegionPart(newRegion, point2D.getX()));
                this.actionList.add(new RegionPart(regionPart.getRegion(), oldEndX));
                break;
            }
        }
    }

    @Data
    private static class RegionPart {

        private final Region region;
        private double endX;

        @Builder
        private RegionPart(Region region, double endX) {
            this.region = region;
            this.endX = endX;
        }

    }

}
