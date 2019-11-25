package calculator;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Calculator {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Input an arithmetic expression: ");
        String s=sc.nextLine();
        System.out.println(s+"="+calculate(s));
        sc.close();
    }

    public static double calculate(String expre) {
        List<String> num=ExpressionParser.transformEnd(expre);
        Stack<Double> stack = new Stack<>();
        double sum = 0;
        while (!num.isEmpty()) {
            String temp = String.valueOf(num.remove(0));
            if (ExpressionParser.isNamber(temp)) {
                double s=Double.parseDouble(temp);
                stack.push(s);
            } else {
                double a=stack.pop();
                double b=stack.pop();
                double c=calTwo(b,a,temp);
                stack.push(c);

            }
        }
        sum=stack.pop();
        return sum;
    }

    private static double calTwo(double a, double b, String opr) {
        double sum = 0;
        switch (opr) {
            case "+":
                sum = a + b;
                break;
            case "-":
                sum = a - b;
                break;
            case "*":
                sum = a * b;
                break;
            case "/":
                sum = a - b;
                break;
        }
        return sum;
    }
}
