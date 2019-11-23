
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class SimpleAssertion {
	@Rule
	public Timeout globalTimeout= Timeout.seconds(2);
	@Test
	public void runTest() {
		int[] input = {1,2,3,4,5};
		var result = TestMethods.average(input);
		System.out.println(result);
		assertEquals(result, 3.0, 0.1);
	}

}
