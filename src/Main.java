import javax.xml.validation.Validator;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        sc.useLocale(Locale.US);
        String str = sc.nextLine().replaceAll(",","."); // toDollars( 737р + toRubles( $85.4 ) )
        String brackets = str.replaceAll("[^()]", "");
        System.out.println(isValidBrackets(brackets) ? "Введено правильное выражение" : "Введено неккоректное выражение");

        String[] mass = str.split(" ");
        double[] resultMain = new double[2];
        resultMain =  parserString(mass, 0);

        System.out.println((resultMain[1] == 0 || resultMain[1] == 1) ? String.format("%,.2f", resultMain[0])  + "р." : "$" + String.format("%,.2f", resultMain[0]) );
    }

    public static boolean isValidBrackets(String bracketString){
        Map<Character, Character> brackets = new HashMap<>();
        brackets.put(')','(');

        Deque<Character> stack = new LinkedList<>();
        for (Character c: bracketString.toCharArray()){
            if (brackets.containsValue(c)){
                stack.push(c);
            } else if (brackets.containsKey(c)) {
                if (stack.isEmpty() || stack.pop() != brackets.get(c)){
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
    public static double[] parserString(String[] arr, double curr) {
        Map<Character, Character> brackets = new HashMap<>();
        brackets.put(')', '(');

        Deque<Character> stack = new LinkedList<>();
        double[] value = new double[2];
        //value[0] = 0;
        value[1] = curr;
// Массив будет возвращать сумму и валюту
// Второй элемент хранит валюту: 1 - RUB, 2 - USD
        int i = 0;
        int j = 0;
        String act = "";
        if (arr.length == 0) return value;

        while (i <= arr.length - 1){
            switch (arr[i]){
                case "(":
                    j = i;
                    stack.push('(');
                    do {
                        j++;
                        if (arr[j].contains("(")) stack.push('(');
                        else if (arr[j].contains(")")) stack.pop();
                    } while (!stack.isEmpty());

                    switch (act) {
                        case "", "add" -> value[0] += parserString(Arrays.copyOfRange(arr, i + 1, j), value[1])[0];
                        default -> value[0] -= parserString(Arrays.copyOfRange(arr, i + 1, j), value[1])[0];
                    }
                    i = j + 1;
                    break;
                case "-":
                    act = "sub";
                    i++;
                    break;
                case "+":
                    act = "add";
                    i++;
                    break;
                case "toRubles(":
                    j = i;
                    stack.push('(');
                    do {
                        j++;
                        if (arr[j].contains("(")) stack.push('(');
                        else if (arr[j].contains(")")) stack.pop();
                    } while (!stack.isEmpty());

                    switch (act) {
                        case "", "add" -> value[0] += toRubles(Arrays.copyOfRange(arr, i + 1, j), value[1])[0];
                        default -> value[0] -= toRubles(Arrays.copyOfRange(arr, i + 1, j), value[1])[0];
                    }
                    value[1] = 1;
                    i = j + 1;
                    break;
                case "toDollars(":
                    j = i;
                    stack.push('(');
                    do {
                        j++;
                        if (arr[j].contains("(")) stack.push('(');
                        else if (arr[j].contains(")")) stack.pop();
                    } while (!stack.isEmpty());

                    switch (act) {
                        case "", "add" -> value[0] += toDollars(Arrays.copyOfRange(arr, i + 1, j), value[1])[0];
                        default -> value[0] -= toDollars(Arrays.copyOfRange(arr, i + 1, j), value[1])[0];
                    }
                    value[1] = 2;
                    i = j + 1;
                    break;
                default:
                    if (arr[i].contains("р")){
                        if (value[1] != 1 && value[1] != 0) {
                            System.out.println("Валюты не сопоставимы");
                            System.exit(0);
                        }
                        if (act.equals("") || act.equals("add")) value[0] += Double.parseDouble(arr[i].replaceAll("р", ""));
                        else value[0] -= Double.parseDouble(arr[i].replaceAll("р", ""));
                        value[1] = 1;
                        i++;
                    } else if (arr[i].contains("$")) {
                        if (value[1] != 2 && value[1] != 0) {
                            System.out.println("Валюты не сопоставимы");
                            System.exit(0);
                        }
                        if (act.equals("") || act.equals("add")) value[0] += Double.parseDouble(arr[i].replaceAll("\\$", ""));
                        else value[0] -= Double.parseDouble(arr[i].replaceAll("\\$", ""));
                        value[1] = 2;
                        i++;
                    } else {
                        System.out.println("Введенно некорректное значение");
                        System.exit(0);
                    }
                    break;
            }
        }

        return value;
    }

    public static double[] toRubles(String[] arrr, double curr){
        if (curr == 2){
            System.out.println("Валюты не сопоставимы");
            System.exit(0);
        }
        double value = 0;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("src\\config.txt"))) {
            value = Double.parseDouble(reader.readLine().replaceAll(",", "."));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        double[] resultMain = new double[2];
        resultMain =  parserString(arrr, 2);
        resultMain[0] *= value;
        return resultMain;
    }

    public static double[] toDollars(String[] arrd, double curd){
        if (curd == 1){
            System.out.println("Валюты не сопоставимы");
            System.exit(0);
        }

        double value = 0;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("src\\config.txt"))) {
            value = Double.parseDouble(reader.readLine().replaceAll(",", "."));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        double[] resultMain = new double[2];
        resultMain =  parserString(arrd, 1);
        resultMain[0] /= value;
        return resultMain;
    }
}