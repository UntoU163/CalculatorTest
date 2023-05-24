import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        sc.useLocale(Locale.US);
        String str = sc.nextLine();
        String brackets = str.replaceAll("[^()]", "");
        System.out.print(isValidBrackets(brackets) ? "Введено правильное выражение" : "Введено неккоректное выражение");
        String dir = System.getProperty("user.dir");

        // directory from where the program was launched
        // e.g /home/mkyong/projects/core-java/java-io
        System.out.println(dir);
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
}