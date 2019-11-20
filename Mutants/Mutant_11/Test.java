package ca.mcgill.ecse429.mutation.input;

public class Test{
	public static void main(String args[]){
		int sum = 0;
		// add all numbers from 1 to 100
		for (int i = 1; i <= 100; i++){
			sum = sum + i;
		}
		/* anything inside this 
		 * will be ignored
		 * this compute sum / 100
		 */
		// print average
		System.out.println(sum - 100.0);
	}
}
