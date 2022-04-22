package labs.lexer;

import java.util.*;
import java.util.regex.*;

import static java.util.Collections.swap;


public class Regexp {

    private static Map<String, Pattern> lexemes = new HashMap<>();

    static {

        lexemes.put("VAR", Pattern.compile("[a-z][a-z0-9]*"));
        lexemes.put("DIGIT", Pattern.compile("0|([1-9][0-9]*)"));
        lexemes.put("ASSIGN_OP", Pattern.compile("[=]{1,2}"));
        lexemes.put("PLUS_SIGN", Pattern.compile("[+]"));
        lexemes.put("MINUS_SIGN", Pattern.compile("[-]"));
        lexemes.put("MULTIPLY_SIGN", Pattern.compile("[*]"));
        lexemes.put("DIVIDE_SIGN", Pattern.compile("[/]"));
        //lexemes.put("WHILE", Pattern.compile("^while +\\( *([a-z]\\w*) *((?:!=|[=<>]=?)) *([a-z]\\w*|\\d+) *\\)"));
        //lexemes.put("FOR", Pattern.compile(""));
    }


    public static boolean checkErrors(String str) {
        if (str.matches(".+?[=]{3,}.+?")) {
            return false;
        }
        if (str.matches("^=")) {
            return false;
        }
        if (str.matches("^[0-9-+=/]+.+?|.+?[=&]")) {
            return false;
        }
        if (str.matches(".+?[=&]")) {
            return false;
        }

        return true;
    }

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_RESET = "\u001B[0m";


    /*  public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";*/


    static List<Token> tokens = new LinkedList<>();
    static List<String> strings = new LinkedList<>();


    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        System.out.println("Input a string: ");


        for (int i = 0; true; i++) {
            strings.add(i, in.nextLine());
            if (strings.get(i).isEmpty()) {
                strings.remove(i);
                break;
            }
        }


        System.out.println("num of strings: " + strings.size());



        for (String string : strings) {
            Regexp object = new Regexp();
            if (checkErrors(string)) {
                object.lexer(string, strings.indexOf(string));
                object.parser(string);

            } else {
                Error error = new Error(null, string);
                error.setType(error.recognizeErrorType(string));

                System.out.println(
                        ANSI_RED +
                                "Error in the " +
                                strings.indexOf(string) +
                                "th line" +
                                '\n' +
                                '\t' +
                                ANSI_RESET +
                                ANSI_RED_BACKGROUND +
                                error.getType() +
                                ANSI_RESET +
                                '\n' +
                                '\t' +
                                "provided line: " +
                                ANSI_RED +
                                error.getValue()
                                + ANSI_RESET);
            }
        }
        for (Token token : tokens) {
            System.out.println(token);
        }

        for (Token token : tokens) {
            System.out.print(token.getValue() + " ");
        }

    }


    public void lexer(String line, int stringNum) {
        for (String lexemName : lexemes.keySet()) {
            {
                Matcher m = lexemes.get(lexemName).matcher(line);
                while (m.find()) {

                    Map<Integer, String> unsortedMap = new HashMap<>();
                    unsortedMap.put(line.indexOf(line.substring(m.start(), m.end())), line.substring(m.start(), m.end()));

                    Map<Integer, String> sortedMap = new TreeMap<Integer, String>(unsortedMap);
                    sortedMap.putAll(unsortedMap);
                    System.out.println(sortedMap);
                    for (Integer lex : sortedMap.keySet()) {
                        if (lexemName.equals("VAR") &&
                                (
                                        sortedMap.get(lex).equals("for") |
                                                sortedMap.get(lex).equals("do") |
                                                sortedMap.get(lex).equals("while") |
                                                sortedMap.get(lex).equals("while")
                                )
                        ) {
                            System.out.println("CYCLE FOUND");
                        } else {
                            tokens.add(new Token(lexemName, sortedMap.get(lex), stringNum, lex));
                        }
                    }
                }
            }
        }
    }

    public void parser(String line) {
        System.out.println("line from method:" + '\t' + line);

        if (line.matches("^while.+?")) {
            List<Token> tokensLine = new LinkedList<>();

            System.out.println("im stupid recognizer but im found while");
            System.out.println(ANSI_YELLOW+"\n"+"I WAS CALLED To FIND WHILE"+"\n"+ANSI_RESET);

            boolean needItr = true;
            boolean isVAR1th = false;
            boolean isASSIGN2nd = false;
            boolean isVAR3th = false;
            for (Token tokenBufPArs : tokens) {
                tokenBufPArs.getCurrentLineTokens(strings.indexOf(line));
                tokenBufPArs.sortingTokensPos(strings.indexOf(line));
                tokensLine.add(tokenBufPArs);
            }

            // while shit
               /* System.out.println(
                        ANSI_RED +
                                tokensLine.get(i).getStringNum()+ '\t' + tokensLine.get(i).getPos() +
                                +'\t' +
                                "I FOUND WHILE VAR+ASSIGN_OP+VAR" +
                                ANSI_RESET);*/

            while (needItr) {
                for (int i = 0; i < tokensLine.size(); i++) {
                    if (tokensLine.get(i).getType().equals("VAR") && tokensLine.get(i).getPos() == 0) {
                        isVAR1th = true;
                    }
                    if (tokensLine.get(i).getType().equals("ASSIGN_OP") && tokensLine.get(i).getPos() == 1) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isASSIGN2nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("VAR") && tokensLine.get(i).getPos() == 2) {
                        isVAR3th = true;
                    }
                    if (isVAR1th == true &&
                            isASSIGN2nd == true &&
                            isVAR3th == true
                    ) {
                        needItr = false;
                        break;
                    }
                    System.out.println('\n' + line + '\n' + "isVAR1th: " + isVAR1th + '\n' +
                            "isASSIGN2nd: " + isASSIGN2nd + '\n' +
                            "isVAR3th: " + isVAR3th + '\n' +
                            "needItr: " + needItr + '\n' +
                            i + '\n' +
                            tokensLine.size()
                    );
                    if (i == tokensLine.size()-1) {
                        needItr = false;
                        break;
                    }
                }
            }
            if (isVAR1th == true &&
                    isASSIGN2nd == true &&
                    isVAR3th == true) {
                System.out.println(
                                ANSI_RED +
                                '\t' +
                                "I FOUND WHILE VAR+ASSIGN_OP+VAR" +
                                ANSI_RESET);
                System.out.println(
                        ANSI_RED +
                                '\t' +
                                tokensLine +
                                ANSI_RESET);
                tokensLine.clear();
                System.out.println(
                        ANSI_RED +
                                '\t' +
                                tokensLine +
                                ANSI_RESET);

            }
            else {

                Error error = new Error(null, line);
                error.setType(error.recognizeErrorType(line));
                System.out.println("I CAN OUTPUT ERRORS");
                System.out.println(
                        ANSI_RED +
                                "Error in the " +
                                strings.indexOf(line) +
                                "th line" +
                                '\n' +
                                '\t' +
                                ANSI_RESET +
                                ANSI_RED_BACKGROUND +
                                error.getType() +
                                ANSI_RESET +
                                '\n' +
                                '\t' +
                                "provided line: " +
                                ANSI_RED +
                                error.getValue()
                                + ANSI_RESET);
                tokensLine.clear();
            }
            tokensLine.clear();
        }

    }




    class Token {

        private String type;
        private String value;
        private int stringNum;
        private int pos;

        public Token(String type, String value, int stringNum, int pos) {
            this.type = type;
            this.value = value;
            this.stringNum = stringNum;
            this.pos = pos;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getStringNum() {
            return stringNum;
        }

        public void setStringNum(int stringNum) {
            this.stringNum = stringNum;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public String getValue() {
            return value;
        }

        public Token getCurrentLineTokens(int numOfCalledLine) {

            for (Token token : tokens) {
                if (token.getStringNum() == numOfCalledLine) {
                    return token;
                }
            }
            return null;
        }


        public void sortingTokensPos(int numOfCalledLine) {
            ArrayList<Integer> array = new ArrayList<Integer>();
            for (Token token : tokens) {
                if (token.getStringNum() == numOfCalledLine) {
                    // array.add(getCurrentLineTokens(numOfCalledLine).getPos());
                    array.add(token.getPos());
                    //System.out.println(ANSI_RED_BACKGROUND + token.getPos() + ANSI_RESET);//dem
                }
            }
            /* Sorting of arraylist using Collections.sort*/
            Collections.sort(array);
            /* ArrayList after sorting*/
            for (Token token : tokens) {
                if (token.getStringNum() == numOfCalledLine) {
                    token.setPos(array.indexOf(token.getPos()));
                }
            }
        }


        @Override
        public String toString() {
            return "Token{" +
                    "type='" + type + '\'' +
                    ", value='" + value + '\'' +
                    ", stringNum=" + stringNum +
                    ", pos=" + pos +
                    '}';
        }
    }

    static class Error {
        private String type;
        private String value;

        public Error(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }


        public String recognizeErrorType(String str) {

            if (str.matches(".+?[=]{3,}.+?")) {
                return "ASSIGN_OP_AMOUNT_ERROR";
            }
            if (str.matches("^=")) {
                return "ASSIGN_OP_AT_THE_BEGINNING_OF_THE_LINE_ERROR";
            }
            if (str.matches(".+?[=&]")) {
                return "ASSIGN_OP_AT_THE_END_OF_THE_LINE_ERROR";
            }
            return "SOME_ERROR_THAT_I_CAN'T_RECOGNIZE";
        }
    }
}
