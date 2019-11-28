import javafx.util.Pair;

import java.util.*;

public class Bytecode {
    //public static final int HALT = 0;
    public static final int JMP = 36;
    public static final int JMPC = 40;
    public static final int PUSHI = 70;
    public static final int PUSHVI = 74;
    public static final int POPM = 76;
    public static final int POPA = 77;
    public static final int POPV = 80;
    public static final int PEEKI = 86;
    public static final int POKEI = 90;
    public static final int SWP = 94;
    public static final int ADD = 100;
    public static final int SUB = 104;
    public static final int MUL = 108;
    public static final int DIV = 112;
    public static final int CMPE = 132;
    public static final int CMPLT = 136;
    public static final int CMPGT = 140;
    public static final int PRINTI = 146;
    public static final int LABEL = -1;
    public static final int SHORT = 0;
    public static final int INT = 1;
    public static final int FLOAT = 2;

    private int sc = 0; // current line being compiled
    private int pc = -1; // program counter
    private int fo = -1; // number of local variables in a function

    private ArrayList<String> source = new ArrayList<String>();
    private ArrayList<Integer> mem = new ArrayList<Integer>();
    private Map<String, Integer> Symbol_table = new HashMap<>();

    public Bytecode(ArrayList<String> src) {
        source = src;
    }
    private void decl(String str) {
        Symbol_table.put(str, ++fo);
        pushi(0);
    };
    private void lab(String str) {
        Symbol_table.put(str, pc + 1);
    };
    private void subr(int i, String str) {
        pushi(16);
        pushi(17);
        pushi(1);
        mem.add(0x2C);
        mem.add(0);
        pc+=2;
    };
    private static Integer[] intToByteInt(int val){
        return new Integer[] {
                 (val & 0xFF000000) >> 24,  (val & 0x00FF0000) >> 16,  (val & 0x0000FF00) >> 8,  (val & 0x000000FF)
        };
    }
    private void printi(int num){
        Integer[] bytes = intToByteInt(num);
        mem.add(PUSHI);
        mem.add(bytes[3]);
        mem.add(bytes[2]);
        mem.add(bytes[1]);
        mem.add(bytes[0]);
        mem.add(PRINTI);
        pc += 6;
    };
    private void printv(String str){
        int offset = Symbol_table.get(str);
        pushi(offset);
        mem.add(PUSHVI);
        mem.add(PRINTI);
        pc+=2;
    };
    private void jmp(String str) {
        int offset = 0;
        if (Symbol_table.containsKey(str)) {
            offset = Symbol_table.get(str);
        }
        pushi(offset);
        mem.add(JMP);
        pc++;
    };
    private void jmpc(String str) {
        int offset = 0;
        if (Symbol_table.containsKey(str)) {
            offset = Symbol_table.get(str);
        }
        pushi(offset);
        mem.add(JMPC);
        pc++;
    };
    private void cmpe() {
        mem.add(CMPE);
        pc++;
    };
    private void cmplt() {
        mem.add(CMPLT);
        pc++;
    };
    private void cmpgt() {
        mem.add(CMPGT);
        pc++;
    };
    private void pushv(String str){
        int offset = Symbol_table.get(str);
        pushi(offset);
        mem.add(PUSHVI);
        pc++;
    };
    private void pushi(int i){
        Integer[] bytes = intToByteInt(i);
        mem.add(PUSHI);
        mem.add(bytes[3]);
        mem.add(bytes[2]);
        mem.add(bytes[1]);
        mem.add(bytes[0]);
        pc+=5;
    };
    private void popm(int i) {
        pushi(i);
        mem.add(POPM);
    };
    private void popa() {
        pushi(0);
        mem.add(POPA);
        pc++;
    };
    private void popv(String str) {
        int offset = Symbol_table.get(str);
        pushi(offset);
        mem.add(POPV);
        pc++;
    };
    private void peek(String str, int i){
        int offset = Symbol_table.get(str);
        pushi(offset);
        pushi(i);
        mem.add(PEEKI);
        pc++;
    };
    private void poke(int i , String str) {
        int offset = Symbol_table.get(str);
        pushi(offset);
        pushi(i);
        mem.add(POKEI);
        pc++;
    };
    private void swp(){
        mem.add(SWP);
        pc++;
    };
    private void add(){
        mem.add(ADD);
        pc++;
    };
    private void sub() {
        mem.add(SUB);
        pc++;
    };
    private void mul() {
        mem.add(MUL);
        pc++;
    };
    private void div() {
        mem.add(DIV);
        pc++;
    };
    private void parse(String stmt){
        if (stmt.isEmpty() || stmt.substring(0, 2).equals("//")) {
            return;
        }
        String[] tokens = stmt.split(" ");
        String operation = tokens[0];
        switch (operation) {
            case("subr"):
                subr(Integer.parseInt(tokens[1]), tokens[2]);
                break;
            case("decl"):
                decl(tokens[1]);
                break;
            case("pushi"):
                pushi(Integer.parseInt(tokens[1]));
                break;
            case("printi"):
                printi(Integer.parseInt(tokens[1]));
                break;
            case("peek"):
                peek(tokens[1], Integer.parseInt(tokens[2]));
                break;
            case("poke"):
                poke(Integer.parseInt(tokens[1]), tokens[2]);
                break;
            case("popv"):
                popv(tokens[1]);
                break;
            case("pushv"):
                pushv(tokens[1]);
                break;
            case("printv"):
                printv(tokens[1]);
                break;
            case("lab"):
                lab(tokens[1]);
                break;
            case("jmp"):
                jmp(tokens[1]);
                break;
            case("jmpc"):
                jmpc(tokens[1]);
                break;
            case("popm"):
                popm(Integer.parseInt(tokens[1]));
                break;
            case("add"):
                add();
                break;
            case("sub"):
                sub();
                break;
            case("mul"):
                mul();
                break;
            case("div"):
                div();
                break;
            case("cmpe"):
                cmpe();
                break;
            case("cmplt"):
                cmplt();
                break;
            case("cmpgt"):
                cmpgt();
                break;
            case("swp"):
                swp();
                break;
            case("ret"):
                popa();
                mem.add(48); //?????
                pc++;
                break;
            default:
                System.out.println(operation + "??");
        }
    };

    //Boolean is_alpha(String);
    ArrayList<Integer> compile() {
        int i = 0;
        label_build();
        System.out.println("After the label is built: mem: " + mem);
        System.out.println("After the label is built: Symbol_table: " + Symbol_table);
        mem = new ArrayList<>();
        for (String str : source) {
            sc = i  + 1;
            parse(str);
        }
        return mem;
    };
    private void label_build() {
        int i = 0;
        for (String str : source) {
            sc = i + 1;
            parse(str);
        }
        mem = null;
        sc = 0;
        pc = -1;
        fo = -1;
    };
}
