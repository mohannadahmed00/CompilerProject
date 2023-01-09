public class Stack {
    private String u,v;
    private int rule;
    Stack(String u,int rule,String v){
        this.u=u;
        this.rule=rule;
        this.v=v;
    }

    public String getU() {
        return u;
    }

    public int getRule() {
        return rule;
    }

    public String getV() {
        return v;
    }
}
