import com.c3po.helper.CycleDirection;
import com.c3po.helper.Cycler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CyclerTest {
    private <T> void baseTest(List<T> values, CycleDirection direction) {
        ArrayList<T> cycledValues = new ArrayList<>();
        Cycler<T> cycler = new Cycler<>(values, direction);
        for(int i = 0; i < values.size() * 2; i++) {
            cycledValues.add(cycler.next());
        }
        switch (direction) {
            case LEFT_TO_RIGHT -> {
                assertEquals(cycledValues.get(0), values.get(0));
                assertEquals(cycledValues.get(cycledValues.size()-1), values.get(values.size()-1));
            }
            case RIGHT_TO_LEFT -> {
                assertEquals(cycledValues.get(0), values.get(values.size()-1));
                assertEquals(cycledValues.get(cycledValues.size()-1), values.get(0));
            }
        }
    }

    @Test
    public void testLeftToRight() {
        List<Integer> values = List.of(1,2,3);
        baseTest(values, CycleDirection.LEFT_TO_RIGHT);
    }

    @Test
    public void testRightToLeft() {
        List<Integer> values = List.of(1,2,3);
        baseTest(values, CycleDirection.RIGHT_TO_LEFT);
    }
}
