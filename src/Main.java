import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static String u, v;
    private static ArrayList<Stack> stack = new ArrayList<>();
    private static ArrayList<Rule> rules = new ArrayList<>();
    private static ArrayList<Integer> errors = new ArrayList<>();
    private static ArrayList<Word> words = new ArrayList<>();


    public static void main(String[] args) {
        //arabic rules
        rules.add(new Rule("S", "NPVP"));
        rules.add(new Rule("S", "VPNP"));
        rules.add(new Rule("S", "VP"));
        rules.add(new Rule("S", "NP"));
        rules.add(new Rule("NP", "n"));
        rules.add(new Rule("NP", "nNP"));
        rules.add(new Rule("NP", "nvNP"));
        rules.add(new Rule("NP", "npNP"));
        rules.add(new Rule("NP", "pnNP"));
        rules.add(new Rule("NP", "pn"));
        rules.add(new Rule("VP", "v"));
        rules.add(new Rule("VP", "vNP"));
        rules.add(new Rule("VP", "vNPVP"));





        words.add(new Word("يلعب", "v"));
        words.add(new Word("يدرس", "v"));
        words.add(new Word("يركب", "v"));
        words.add(new Word("الطالب", "n"));
        words.add(new Word("الرجل", "n"));
        words.add(new Word("رجل", "n"));
        words.add(new Word("الولد", "n"));
        words.add(new Word("صديقه", "n"));
        words.add(new Word("الحديقة", "n"));
        words.add(new Word("مدرسة", "n"));
        words.add(new Word("المدرسة", "n"));
        words.add(new Word("السيارة", "n"));
        words.add(new Word("في", "p"));
        words.add(new Word("مع", "p"));
        words.add(new Word("على", "p"));
        words.add(new Word("جميل", "a"));
        words.add(new Word("صغير", "a"));



        //example
        /*rules.add(new Rule("S", "A"));
        rules.add(new Rule("A", "T"));
        rules.add(new Rule("A", "A+T"));
        rules.add(new Rule("T", "b"));
        rules.add(new Rule("T", "(A)"));*/


//      input
//      احمد رجل مصري صالح يلعب دور مهم في المجتمع مع
        String sentence = "الولد يلعب الكرة";
        v = sentenceToNonTerm(sentence);
        System.out.println("non-Terminals: " + v);
        u = "";

        while (!u.equals("S")) {
            int rule = searchRule(u);
            if (rule == 0) {
                if (v.equals("")) {
                    System.out.println("this sentence can not configured from rules");
                    break;
                } else {
                    shift();
                }
            } else if (rule > /*1*/ 4 ) {//normal nodes all without (S) nodes
                stack.add(new Stack(u, rule, v));
                u = execute(stack.get(stack.size() - 1).getU(), stack.get(stack.size() - 1).getRule());
            } else if (rule == 1 || rule == 2 || rule == 3 || rule == 4) {  //(S) nodes
                if (v.equals("")) {
                    stack.add(new Stack(u, rule, v));
                    u = execute(stack.get(stack.size() - 1).getU(), stack.get(stack.size() - 1).getRule());
                    if (u.equals("S")) {
                        u = "S";
                    } else {
                        errors.add(stack.get(stack.size() - 1).getRule());
                        stack.remove(stack.size() - 1);
                        v = stack.get(stack.size() - 1).getV();
                        u = execute(stack.get(stack.size() - 1).getU(), stack.get(stack.size() - 1).getRule());
                    }
                } else {
                    shift();
                }
            } else {
                shift();
            }
        }

        if(u.equals("S")){
            String result = "";
            for (int i = stack.size() - 1; i >= 0; i--) {
                if (i == stack.size() - 1) {
                    result = stack.get(i).getU();
                    System.out.println(stack.get(i).getRule() + " S-----> " + result);
                } else {
                    result = executeFinal(result, stack.get(i).getRule());
                    System.out.println(stack.get(i).getRule() + " -----> " + result);
                }
            }
        }

    }


    //arabic shift(right to left)
    public static void shift() {
        u = v.charAt(v.length() - 1) + u;
        v = v.substring(0, v.length() - 1);
    }

    //english shift(lift to right)
    /*public static void shift() {
        u =u + v.charAt(0);
        v = v.substring(1);
    }*/

    public static String execute(String u, int rule) {
        String parent, child;
        parent = rules.get(rule - 1).getFrom();
        child = rules.get(rule - 1).getDir();
        u = u.replace(child, parent);
        return u;
    }

    public static String executeFinal(String u, int rule) {
        String parent, child;
        parent = rules.get(rule - 1).getFrom();
        child = rules.get(rule - 1).getDir();
        u = u.replace(parent, child);
        return u;
    }

    public static int searchRule(String u) {
        String dir;
        int rule = 0;
        for (int i = rules.size() - 1; i >= 0; i--) {
            dir = rules.get(i).getDir();
            if (u.contains(dir)) {
                rule = i + 1;
                if (isError(rule)) {
                    rule = 0;
                } else {
                    break;
                }
            }
        }
        return rule;
    }

    private static String sentenceToNonTerm(String sentence) {
        String[] x = stringToArr(sentence);
        String nonTerm = "";
        for (String s : x) {
            String type;
            if (isExist(s)) {
                for (Word w : words) {
                    if (s.equals(w.getName())) {
                        type = w.getType();
                        nonTerm = type + nonTerm;
                    }
                }
            } else {
                System.out.println("tell me!! what is the type of this word " + s + " ?");
                Scanner in = new Scanner(System.in);
                String newWordType = in.nextLine();
                nonTerm = newWordType + nonTerm;
                words.add(new Word(s, newWordType));
            }
        }
        return nonTerm;
    }

    private static String[] stringToArr(String x) {
        return x.split(" ");
    }

    private static boolean isExist(String word) {
        boolean x = false;
        for (Word s : words) {
            if (word.equals(s.getName())) {
                x = true;
                break;
            }
        }
        return x;
    }

    public static boolean isError(int rule) {
        boolean x = false;
        for (int i : errors) {
            if (rule == i) {
                x = true;
                break;
            }
        }
        return x;
    }

}
