package org.example;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите математическое выражение: ");
        String expressionText = scanner.nextLine();

        System.out.print("Введите значения неизвестных переменных (например для переменных x, y, z: x 5 y 10 z 11): ");
        String variables = scanner.nextLine();

        Integer result = Calculator.calculate(expressionText, variables);

        if (result != null)
            System.out.print(Calculator.calculate(expressionText, variables));
        else
            System.out.print("При работе метода получена ошибка, вызванная некорректным вводом");
    }
}
