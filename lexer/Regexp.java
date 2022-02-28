package labs.lexer;

import java.util.*;
import java.util.regex.*;

public class Regexp {

    private static Map<String, Pattern> lexems = new HashMap<>();

    static {

        lexems.put("VAR", Pattern.compile("[a-z][a-z0-9]*"));
        lexems.put("DIGIT", Pattern.compile("0|([1-9][0-9]*)"));
        lexems.put("ASSIGN_OP", Pattern.compile("[=]"));
        lexems.put("PLUS_SIGN", Pattern.compile("[+]"));
        lexems.put("MINUS_SIGN", Pattern.compile("[-]"));
        lexems.put("MULTIPLY_SIGN", Pattern.compile("[*]"));
        lexems.put("DIVIDE_SIGN", Pattern.compile("[/]"));
    }


    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        System.out.println("Input a string: ");

        List<String> strings = new LinkedList<>();

        for (int i = 0; true; i++){
            strings.add(i, in.nextLine());
            if (strings.get(i).isEmpty()){
                strings.remove(i);
                break;
            }
        }


        System.out.println("num of strings: "+strings.size());


        List<Token> tokens = new LinkedList<>();

        for (int i = 0; i < strings.size(); i++) {
            for (String lexemName : lexems.keySet()) {
                Matcher m = lexems.get(lexemName).matcher(strings.get(i));
                while (m.find()) {
                    System.out.println(lexemName + " found ");
                    tokens.add(new Token(lexemName, strings.get(i).substring(m.start(), m.end())));
                }
            }
        }
        for (Token token: tokens) {
            System.out.println(token);
        }

        for (Token token: tokens) {
            System.out.print(token.getValue() + " ");
        }

    }

}

class Token {

    private String type;
    private String value;

    public Token(String type, String value){
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }



    @Override
    public String toString(){
        return "TOKEN[type=\"" + this.type + "\", value=\"" + this.value + "\"]";
    }

}
