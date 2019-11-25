import static org.junit.Assert.assertEquals;

import calculator.Calculator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.EmptyStackException;

public class TestMethodsCalTest {
    @Rule
    public Timeout globalTimeout= Timeout.seconds(2);

    @Test(expected = EmptyStackException.class)
    public void emptyStringTest() {
        Calculator.calculate(" ");
    }

    @Test
    public void AddTest() {
        var result = Calculator.calculate("1+2");
        assertEquals(3, result, 0.01);
    }


    @Test
    public void MinusTest() {
        var result = Calculator.calculate("1-2");
        assertEquals(-1, result, 0.01);
    }

    @Test
    public void MultiplyTest() {
        var result = Calculator.calculate("1*2");
        assertEquals(2, result, 0.01);
    }

    @Test
    public void DivisionTest() {
        var result = Calculator.calculate("1/2");
        assertEquals(0.5, result, 0.01);
    }
}