import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.EmptyStackException;

public class TestMethodsCalTest {
    @Rule
    public Timeout globalTimeout= Timeout.seconds(2);
    @Test(expected = EmptyStackException.class)
    public void runTest() {
        TestMethods_Cal.calculate(" ");
    }
}