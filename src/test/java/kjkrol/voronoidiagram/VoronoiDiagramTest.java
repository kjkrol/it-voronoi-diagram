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
                .points(Arrays.asList(new Point2D(5, 4), new Point2D(5, 6)))
                .build();
        voronoiDiagram.start();
        voronoiDiagram.getRegions().stream().peek(System.out::println).count();
    }
}
