
public class TestMethods{
	public static double average(int numbers[]){
		var sum = 0;
		// add all numbers from 1 to 100
		for (int i = 0; i < numbers.length; i++){
			sum = sum + numbers[i];
		}
		/* anything inside this 
		 * will be ignored
		 * this compute sum / 100
		 */
		// print average
		return ((float) sum - numbers.length);
	}
}
