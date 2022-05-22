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
        lexemes.put("SIGN_LESS", Pattern.compile("[<]"));
        lexemes.put("SIGN_GREATER", Pattern.compile("[>]"));
        lexemes.put("SEMICOLON", Pattern.compile(";"));
        lexemes.put("PLUS_SIGN", Pattern.compile("[+]"));
        lexemes.put("MINUS_SIGN", Pattern.compile("[-]"));
        lexemes.put("MULTIPLY_SIGN", Pattern.compile("[*]"));
        lexemes.put("DIVIDE_SIGN", Pattern.compile("[/]"));
        lexemes.put("LINKED_LIST_TRIGGER", Pattern.compile("LinkedList"));
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
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
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


        ArrayList<LinkedListCollection> myLists = new ArrayList<>();

        for (String string : strings) {
            Regexp object = new Regexp();
            if (checkErrors(string)) {
                //LinkedListCollection obj = new LinkedListCollection(null,null);

                if (string.startsWith("LinkedList")) {


                    LinkedListCollection obj = new LinkedListCollection(string.substring(string.indexOf("<") + 1, string.indexOf(">")),
                            string.substring(string.indexOf(">") + 1, string.lastIndexOf("=")).trim(),new LinkedList<>());
                    myLists.add(obj);

                } else {

                    if ((string.contains("."))&&(string.substring(string.indexOf(".")).matches("\\.add\\(\\w+\\)")||
                    string.substring(string.indexOf(".")).matches("\\.remove\\(\\w+\\)")||
                    string.substring(string.indexOf(".")).matches("\\.clear\\(\\)")))
                    {

                        for (int i = 0; i < myLists.size(); i++){
                            if (myLists.get(i).getCollectionName().equals(string.substring(0,string.indexOf(".")))) {
                                myLists.get(i).checkLLKeyWords(myLists.get(i).getCollectionName(), string, myLists.get(i).getObjectList());
                            }
                        }

                    }
                    else {
                        object.lexer(string, strings.indexOf(string));
                        object.parser(string);
                    }
                }
            } else {
                Error someError = new Error(null, null);
                someError.printError(string);
            }
        }

        for (Token token : tokens) {
            System.out.println(token);
        }

        for (Token token : tokens) {
            System.out.print(token.getValue() + " ");

        }
        System.out.println(myLists);

    }


    public void lexer(String line, int stringNum) {
        int i=0;
        for (String lexemName : lexemes.keySet()) {
            {
                Matcher m = lexemes.get(lexemName).matcher(line);

                while (m.find()) {

                    Map<Integer, String> unsortedMap = new HashMap<>();
                    unsortedMap.put(i, line.substring(m.start(), m.end()));      //line.indexOf(line.substring(m.start(), m.end()))
                    i++;


                    Map<Integer, String> sortedMap = new TreeMap<Integer, String>(unsortedMap);
                    sortedMap.putAll(unsortedMap);
                    System.out.println(sortedMap);
                    for (Integer lex : sortedMap.keySet()) {

                        if (
                           (lexemName.equals("VAR") &&(
                                sortedMap.get(lex).equals("for") |
                                sortedMap.get(lex).equals("if") |
                                sortedMap.get(lex).equals("do") |
                                sortedMap.get(lex).equals("while")
                                )
                            )
                        )
                         {
                            System.out.println("KEYWORD FOUND");
                        } else {
                            tokens.add(new Token(lexemName, sortedMap.get(lex), stringNum, m.start()));
                        }
                    }
                }
            }
        }
    }

    public void stupidSortingList(List<Token> tokensLine){
        for (int i =0; i<tokensLine.size(); i++) {
            if (tokensLine.get(i).getPos() != i&&tokensLine.get(i).getPos()<= tokensLine.size()) {
                swap(tokensLine,tokensLine.get(i).getPos(),i);
                stupidSortingList(tokensLine);
            }
        }
    }
    public void printExprType(List<Token> tokensLine,String exprType) {
        stupidSortingList(tokensLine);

        if (exprType.equals("IF") || exprType.equals("WHILE")) {
            System.out.println(
                    ANSI_RED +
                            '\t' +
                            "I FOUND " + ANSI_GREEN + ANSI_BLACK_BACKGROUND + exprType +
                            ANSI_RESET + ANSI_PURPLE + " AND ITS TYPE IS " +
                            ANSI_CYAN + tokensLine.get(0).getType() +
                            "+" + tokensLine.get(1).getType() + "+" +
                            tokensLine.get(2).getType() +
                            ANSI_RESET);

            System.out.println(
                    ANSI_RED +
                            '\t' +
                            tokensLine +
                            ANSI_RESET);

        }
        if (exprType.equals("FOR")){
            System.out.println(
                    ANSI_RED +
                            '\t' +
                            "I FOUND " + ANSI_GREEN + ANSI_BLACK_BACKGROUND + exprType +
                            ANSI_RESET + ANSI_PURPLE + " AND ITS TYPE IS " +'\n'+'\t'+'\t'+

                            ANSI_RED +ANSI_BLACK_BACKGROUND +"counter_initialization's type: " + ANSI_RESET+
                                ANSI_CYAN +
                                tokensLine.get(0).getType() + "+" +
                                tokensLine.get(1).getType() + "+" +
                                tokensLine.get(2).getType() + "+" +
                                tokensLine.get(3).getType() + " " +
                            ANSI_RESET+tokensLine.get(0).getValue()+tokensLine.get(1).getValue()+tokensLine.get(2).getValue()+tokensLine.get(3).getValue()+
                            '\n'+'\t'+'\t'+
                            ANSI_RED +ANSI_BLACK_BACKGROUND+"condition_block's type: " +ANSI_RESET+
                                ANSI_CYAN +
                                tokensLine.get(4).getType() + "+" +
                                tokensLine.get(5).getType() + "+" +
                                tokensLine.get(6).getType() + "+" +
                                tokensLine.get(7).getType() + " " +
                            ANSI_RESET+tokensLine.get(4).getValue()+tokensLine.get(5).getValue()+tokensLine.get(6).getValue()+tokensLine.get(7).getValue()+
                            '\n'+'\t'+'\t'+
                            ANSI_RED+ANSI_BLACK_BACKGROUND +"action_block's type: " +ANSI_RESET+
                                ANSI_CYAN +
                                tokensLine.get(8).getType() + "+" +
                                tokensLine.get(9).getType() + "+" +
                                tokensLine.get(10).getType() + " " +
                            ANSI_RESET+tokensLine.get(8).getValue()+tokensLine.get(9).getValue()+tokensLine.get(10).getValue()
                            );

            System.out.println(ANSI_GREEN +
                            '\t' +'\t' +"THERE ARE PROVIDED TOKENS: "+'\n'+
                    tokensLine+
                            ANSI_RESET
                    );
        }
    }



    public void parser(String line) {
        System.out.println("line from method:" + '\t' + line);

        if (line.matches("^while .+?")) {
            List<Token> tokensLine = new LinkedList<>();

            System.out.println("im stupid recognizer but im found while");
            System.out.println(ANSI_YELLOW+"\n"+"I WAS CALLED TO FIND WHILE"+"\n"+ANSI_RESET);

            boolean needItr = true;
            boolean isVAR1th = false;
            boolean isDIGIT1th = false;
            boolean isASSIGN2nd = false;
            boolean isSIGN_LESS2nd = false;
            boolean isSIGN_GREATER2nd = false;
            boolean isVAR3th = false;
            boolean isDIGIT3th = false;
            for (Token tokenBufPArs : tokens) {
                //tokenBufPArs.getCurrentLineTokens(strings.indexOf(line));
                tokenBufPArs.sortingTokensPos(strings.indexOf(line));
                if (tokenBufPArs.getStringNum()==strings.indexOf(line))
                    tokensLine.add(tokenBufPArs);
            }


            while (needItr) {
                for (int i = 0; i < tokensLine.size(); i++) {
                    if (tokensLine.get(i).getType().equals("VAR") && tokensLine.get(i).getPos() == 0) {
                        isVAR1th = true;
                    }
                    if (tokensLine.get(i).getType().equals("DIGIT") && tokensLine.get(i).getPos() == 0) {
                        isDIGIT1th = true;
                    }
                    if (tokensLine.get(i).getType().equals("ASSIGN_OP") && tokensLine.get(i).getPos() == 1) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isASSIGN2nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("SIGN_LESS") && tokensLine.get(i).getPos() == 1) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isSIGN_LESS2nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("SIGN_GREATER") && tokensLine.get(i).getPos() == 1) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isSIGN_GREATER2nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("VAR") && tokensLine.get(i).getPos() == 2) {
                        isVAR3th = true;
                    }
                    if (tokensLine.get(i).getType().equals("DIGIT") && tokensLine.get(i).getPos() == 2) {
                        isDIGIT3th = true;
                    }
                    if ((isVAR1th == true ||isDIGIT1th == true)&&
                            (isASSIGN2nd == true||isSIGN_LESS2nd==true||isSIGN_GREATER2nd==true )&&
                            (isVAR3th == true||isDIGIT3th == true)
                    ) {
                        needItr = false;
                        break;
                    }
                    System.out.println('\n' + line + '\n' + "isVAR1th: " + isVAR1th + '\n' +
                            "isDIGIT1th: " + isDIGIT1th + '\n' +
                            "isASSIGN2nd: " + isASSIGN2nd + '\n' +
                            "isVAR3th: " + isVAR3th + '\n' +
                            "isDIGIT3th: " + isDIGIT3th + '\n' +
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

            if ((isVAR1th == true ||isDIGIT1th == true)&&
                    (isASSIGN2nd == true||isSIGN_LESS2nd==true||isSIGN_GREATER2nd==true )&&
                    (isVAR3th == true||isDIGIT3th == true)
            ){
                printExprType(tokensLine,"WHILE");
            }
            else {
                Error someError = new Error(null, null);
                someError.printError(strings.get(strings.indexOf(line)));

            }
            tokensLine.clear();
        }

        if (line.matches("^if .+?")) {
            List<Token> tokensLine = new LinkedList<>();

            System.out.println("im stupid recognizer but im found IF");
            System.out.println(ANSI_YELLOW+"\n"+"I WAS CALLED TO FIND IF"+"\n"+ANSI_RESET);

            boolean needItr = true;
            boolean isVAR1th = false;
            boolean isDIGIT1th = false;
            boolean isASSIGN2nd = false;
            boolean isSIGN_LESS2nd = false;
            boolean isSIGN_GREATER2nd = false;
            boolean isVAR3th = false;
            boolean isDIGIT3th = false;
            for (Token tokenBufPArs : tokens) {
                //tokenBufPArs.getCurrentLineTokens(strings.indexOf(line));
                tokenBufPArs.sortingTokensPos(strings.indexOf(line));
                if (tokenBufPArs.getStringNum()==strings.indexOf(line))
                    tokensLine.add(tokenBufPArs);
            }

            while (needItr) {
                for (int i = 0; i < tokensLine.size(); i++) {
                    if (tokensLine.get(i).getType().equals("VAR") && tokensLine.get(i).getPos() == 0) {
                        isVAR1th = true;
                    }
                    if (tokensLine.get(i).getType().equals("DIGIT") && tokensLine.get(i).getPos() == 0) {
                        isDIGIT1th = true;
                    }
                    if (tokensLine.get(i).getType().equals("ASSIGN_OP") && tokensLine.get(i).getPos() == 1) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isASSIGN2nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("SIGN_LESS") && tokensLine.get(i).getPos() == 1) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isSIGN_LESS2nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("SIGN_GREATER") && tokensLine.get(i).getPos() == 1) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isSIGN_GREATER2nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("VAR") && tokensLine.get(i).getPos() == 2) {
                        isVAR3th = true;
                    }
                    if (tokensLine.get(i).getType().equals("DIGIT") && tokensLine.get(i).getPos() == 2) {
                        isDIGIT3th = true;
                    }
                    if ((isVAR1th == true ||isDIGIT1th == true)&&
                            (isASSIGN2nd == true||isSIGN_LESS2nd==true||isSIGN_GREATER2nd==true )&&
                            (isVAR3th == true||isDIGIT3th == true)
                    ) {
                        needItr = false;
                        break;
                    }
                    System.out.println('\n' + line + '\n' + "isVAR1th: " + isVAR1th + '\n' +
                            "isDIGIT1th: " + isDIGIT1th + '\n' +
                            "isASSIGN2nd: " + isASSIGN2nd + '\n' +
                            "isVAR3th: " + isVAR3th + '\n' +
                            "isDIGIT3th: " + isDIGIT3th + '\n' +
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

            if ((isVAR1th == true ||isDIGIT1th == true)&&
                    (isASSIGN2nd == true||isSIGN_LESS2nd==true||isSIGN_GREATER2nd==true )&&
                    (isVAR3th == true||isDIGIT3th == true)
            ){
                printExprType(tokensLine, "IF");
            }
            else {
                Error someError = new Error(null, null);
                someError.printError(strings.get(strings.indexOf(line)));
            }
            tokensLine.clear();
        }




        if (line.matches("^for .+?")) {
            List<Token> tokensLine = new LinkedList<>();

            System.out.println("im stupid recognizer but im found FOR");
            System.out.println(ANSI_YELLOW+"\n"+"I WAS CALLED TO FIND FOR"+"\n"+ANSI_RESET);

            boolean needItr = true;
            boolean isVAR1th = false;
            boolean isASSIGN2nd = false;
            boolean isSEMICOLON4th = false;
            boolean isDIGIT3th = false;
            boolean counter_initialization=false;
            boolean isVAR5th = false;
            boolean isDIGIT5th = false;
            boolean isASSIGN6nd = false;
            boolean isSIGN_LESS6nd = false;
            boolean isSIGN_GREATER6nd = false;
            boolean isVAR7th = false;
            boolean isDIGIT7th = false;
            boolean isSEMICOLON8th = false;
            boolean condition_block = false;
            boolean isVAR9th =false;
            boolean isDIGIT9th =false;
            boolean isASSIGN10nd =false;
            boolean isPLUS10nd =false;
            boolean isMINUS10nd =false;
            boolean isMULTIPLY10nd =false;
            boolean isDIVIDE10nd =false;
            boolean isVAR11th =false;
            boolean isDIGIT11th =false;
            boolean action_block=false;

                for (Token tokenBufPArs : tokens) {
                //tokenBufPArs.getCurrentLineTokens(strings.indexOf(line));
                tokenBufPArs.sortingTokensPos(strings.indexOf(line));
                if (tokenBufPArs.getStringNum()==strings.indexOf(line))
                    tokensLine.add(tokenBufPArs);
            }

            while (needItr) {
                for (int i = 0; i < tokensLine.size(); i++) {
                    if (tokensLine.get(i).getType().equals("VAR") && tokensLine.get(i).getPos() == 0) {
                        isVAR1th = true;
                    }

                    if (tokensLine.get(i).getType().equals("ASSIGN_OP") && tokensLine.get(i).getPos() == 1) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isASSIGN2nd = true;
                    }

                    if (tokensLine.get(i).getType().equals("DIGIT") && tokensLine.get(i).getPos() == 2) {
                        isDIGIT3th = true;
                    }
                    if (tokensLine.get(i).getType().equals("SEMICOLON") && tokensLine.get(i).getPos() == 3) {
                        isSEMICOLON4th = true;
                    }

                    if ((isVAR1th == true &&
                            isASSIGN2nd == true&&
                            isDIGIT3th == true&&
                            isSEMICOLON4th==true)
                    ) {
                        counter_initialization=true;
                    }

                    if (tokensLine.get(i).getType().equals("VAR") && tokensLine.get(i).getPos() == 4) {
                        isVAR5th = true;
                    }
                    if (tokensLine.get(i).getType().equals("DIGIT") && tokensLine.get(i).getPos() == 4) {
                        isDIGIT5th = true;
                    }
                    if (tokensLine.get(i).getType().equals("ASSIGN_OP") && tokensLine.get(i).getPos() == 5) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isASSIGN6nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("SIGN_LESS") && tokensLine.get(i).getPos() == 5) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isSIGN_LESS6nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("SIGN_GREATER") && tokensLine.get(i).getPos() == 5) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isSIGN_GREATER6nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("VAR") && tokensLine.get(i).getPos() == 6) {
                        isVAR7th = true;
                    }
                    if (tokensLine.get(i).getType().equals("DIGIT") && tokensLine.get(i).getPos() == 6) {
                        isDIGIT7th = true;
                    }
                    if (tokensLine.get(i).getType().equals("SEMICOLON") && tokensLine.get(i).getPos() == 7) {
                        isSEMICOLON8th = true;
                    }

                    if(
                            (isVAR5th==true || isDIGIT5th==true)&&
                            (isASSIGN6nd==true || isSIGN_LESS6nd==true|| isSIGN_GREATER6nd==true)&&
                            (isVAR7th==true || isDIGIT7th==true)&&
                             isSEMICOLON8th==true
                    ){
                        condition_block=true;
                    }

                    if (tokensLine.get(i).getType().equals("VAR") && tokensLine.get(i).getPos() == 8) {
                        isVAR9th = true;
                    }
                    if (tokensLine.get(i).getType().equals("DIGIT") && tokensLine.get(i).getPos() == 8) {
                        isDIGIT9th = true;
                    }
                    if (tokensLine.get(i).getType().equals("ASSIGN_OP") && tokensLine.get(i).getPos() == 9) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isASSIGN10nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("PLUS_SIGN") && tokensLine.get(i).getPos() == 9) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isPLUS10nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("MINUS_SIGN") && tokensLine.get(i).getPos() == 9) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isMINUS10nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("MULTIPLY_SIGN") && tokensLine.get(i).getPos() == 9) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isMULTIPLY10nd = true;
                    }
                    if (tokensLine.get(i).getType().equals("DIVIDE_SIGN") && tokensLine.get(i).getPos() == 9) {
                        System.out.println("TEST FOR ASSIGN_OP\n"+"Type: "+tokensLine.get(i).getType()+'\n'+"POS: "+ tokensLine.get(i).getPos());
                        isDIVIDE10nd = true;
                    }

                    if (tokensLine.get(i).getType().equals("VAR") && tokensLine.get(i).getPos() == 10) {
                        isVAR11th = true;
                    }
                    if (tokensLine.get(i).getType().equals("DIGIT") && tokensLine.get(i).getPos() == 10) {
                        isDIGIT11th = true;
                    }

                    if(
                    (isVAR9th == true ||isDIGIT9th == true)&&
                    (isASSIGN10nd == true||isPLUS10nd == true||isMINUS10nd == true||isMULTIPLY10nd == true||isDIVIDE10nd == true)&&
                    (isVAR11th == true||isDIGIT11th == true)
                    ){
                        action_block=true;
                    }


                    if (counter_initialization==true&&
                        condition_block==true&&
                        action_block==true
                    )
                     {
                        needItr = false;
                        break;
                    }

                    System.out.println('\n' + line +
                            "needItr: "+ needItr+'\n'+
                            "isVAR1th: "+ isVAR1th+'\n'+
                            "isASSIGN2nd: "+ isASSIGN2nd+'\n'+
                            "isSEMICOLON4th: "+ isSEMICOLON4th+'\n'+
                            "isDIGIT3th: "+ isDIGIT3th+'\n'+ANSI_RED+
                            "counter_initialization: "+ANSI_RESET+ counter_initialization+'\n'+
                            "isVAR5th: "+ isVAR5th+'\n'+
                            "isDIGIT5th: "+ isDIGIT5th+'\n'+
                            "isASSIGN6nd: "+ isASSIGN6nd+'\n'+
                            "isSIGN_LESS6nd: "+ isSIGN_LESS6nd+'\n'+
                            "isSIGN_GREATER6nd: "+ isSIGN_GREATER6nd+'\n'+
                            "isVAR7th: "+ isVAR7th+'\n'+
                            "isDIGIT7th: "+ isDIGIT7th+'\n'+
                            "isSEMICOLON8th: "+ isSEMICOLON8th+'\n'+ ANSI_RED+
                            "condition_block: "+ANSI_RESET+ condition_block+'\n'+
                            "isVAR9th: "+ isVAR9th+'\n'+
                            "isDIGIT9th: "+ isDIGIT9th+'\n'+
                            "isASSIGN10nd: "+ isASSIGN10nd+'\n'+
                            "isPLUS10nd: "+ isPLUS10nd+'\n'+
                            "isMINUS10nd: "+ isMINUS10nd+'\n'+
                            "isMULTIPLY10nd: "+ isMULTIPLY10nd+'\n'+
                            "isDIVIDE10nd: "+ isDIVIDE10nd+'\n'+
                            "isVAR11th: "+ isVAR11th+'\n'+
                            "isDIGIT11th: "+ isDIGIT11th+'\n'+ ANSI_RED+
                            "action_block: "+ANSI_RESET+ action_block+'\n'+

                            i + '\n' +
                            tokensLine.size()
                    );

                    if (i == tokensLine.size()-1) {
                        needItr = false;
                        break;
                    }
                }
            }

            if (counter_initialization==true&&
                    condition_block==true&&
                    action_block==true
            ){
                printExprType(tokensLine, "FOR");
            }
            else {
                Error someError = new Error(null, null);
                someError.printError(strings.get(strings.indexOf(line)));
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

        public void printError(String string){
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
        public String recognizeErrorType(String str) {

            if (str.matches(".+?[=]{3,}.+?")) {
                return "ASSIGN_OP_AMOUNT_ERROR";
            }
            if (str.matches(".+?[;].+?")) {
                char[] ar;
                ar = str.toCharArray();

                int counter=0;
                for (int i=0;i<ar.length;i++){
                    if (ar[i]==';'){
                        counter+=1;
                        System.out.println(ANSI_BLUE_BACKGROUND+counter+ANSI_RESET);
                    }
                    System.out.println(ar[i]);
                }
                if (counter!=2) {
                    return "SEMICOLON_AMOUNT_ERROR";
                }
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
class LinkedListCollection{
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    private LinkedList<String> objectList = new LinkedList<>();
    private String dataType;
    private String collectionName;



    public LinkedListCollection(String dataType, String collectionName, LinkedList<String> objectList) {
        this.dataType = dataType;
        this.collectionName = collectionName;
        this.objectList = objectList;
    }

    public LinkedList<String> getObjectList() {
        return objectList;
    }
    public void setObjectList(LinkedList<String> objectList, String value) {
        this.objectList.add((java.lang.String) value);
    }

    public void removeObjectList(LinkedList<String> objectList, String value) {
        this.objectList.remove((java.lang.String) value);
    }

    public void clearObjectList(LinkedList<String> objectList, String value) {
        this.objectList.clear();
    }
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }


    public void setObjectList(LinkedList<String> objectList) {
        this.objectList = objectList;
    }

    public void createLinkedList(String wholeLine) {
        LinkedListCollection someCol = new LinkedListCollection(wholeLine.substring(wholeLine.indexOf("<") + 1, wholeLine.indexOf(">")),
                wholeLine.substring(wholeLine.indexOf(">") + 1, wholeLine.lastIndexOf("=")).trim(), new LinkedList<>());

        System.out.println(someCol.toString());

        switch (someCol.dataType) {
            case ("String"): {
                System.out.println("STRING LINKED LIST");
                LinkedList<String> llistCode = new LinkedList<String>();
            }
            case ("INTEGER"): {
                System.out.println("Integer LINKED LIST");
                LinkedList<Integer> llistCode = new LinkedList<Integer>();
            }
        }
    }
    public void checkLLKeyWords(String collectionName, String wholeLine,LinkedList<String> objectList){

        String locVariable = wholeLine.substring(0,wholeLine.indexOf("."));
        String locValue=wholeLine.substring(wholeLine.indexOf("(")+1,wholeLine.indexOf(")"));

        if (locVariable.equals(collectionName)&&
        (wholeLine.substring(wholeLine.indexOf(".")).matches("\\.add\\(\\w+\\)"))){

            this.setObjectList(this.getObjectList(),locValue);
            //objectList.add(locValue);
            System.out.println(ANSI_YELLOW+'\t'+"YOURS LINKED_LIST "+
                    ANSI_CYAN+locVariable+
                    ANSI_YELLOW+" AFTER OPERATION ADD("+
                    ANSI_CYAN+locValue+
                    ANSI_YELLOW+")"+
                    '\n'+this.getObjectList()+ANSI_RESET);
        }
        if (locVariable.equals(collectionName)&&
                (wholeLine.substring(wholeLine.indexOf(".")).matches("\\.remove\\(\\w+\\)"))){

            this.removeObjectList(this.getObjectList(),locValue);
            //objectList.add(locValue);
            System.out.println(ANSI_YELLOW+'\t'+"YOURS LINKED_LIST "+
                    ANSI_CYAN+locVariable+
                    ANSI_YELLOW+" AFTER OPERATION REMOVE("+
                    ANSI_CYAN+locValue+
                    ANSI_YELLOW+")"+
                    '\n'+this.getObjectList()+ANSI_RESET);
        }
        if (locVariable.equals(collectionName)&&
                (wholeLine.substring(wholeLine.indexOf(".")).matches("\\.clear\\(\\)"))){

            this.clearObjectList(this.getObjectList(),locValue);
            //objectList.add(locValue);
            System.out.println(ANSI_YELLOW+'\t'+"YOURS LINKED_LIST "+
                    ANSI_CYAN+locVariable+
                    ANSI_YELLOW+" AFTER OPERATION CLEAR("+")"+
                    '\n'+this.getObjectList()+ANSI_RESET);
        }

    }


    @Override
    public String toString() {
        return "LinkedListCollection{" +
                "dataType='" + dataType + '\'' +
                ", collectionName='" + collectionName + '\'' +
                '}';
    }

    public void abstractAdd(LinkedList<String> colFromDSL,LinkedListCollection objectLL){
    }

}
