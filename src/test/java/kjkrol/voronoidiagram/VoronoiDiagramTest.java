package kjkrol.voronoidiagram;

import javafx.geometry.Point2D;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Karol Krol
 */
public class VoronoiDiagramTest {

    @Test
    public void startTest() {
        final VoronoiDiagram voronoiDiagram = VoronoiDiagram.builder()
                .width(10)
                .height(10)
                .givenPoints(Arrays.asList(new Point2D(2, 2), new Point2D(5, 5), new Point2D(8, 8)))
                .build();
        voronoiDiagram.start();
        voronoiDiagram.getRegions().stream().peek(System.out::println).count();
    }
}
