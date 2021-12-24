package com.example.xingliansdk.network.changSang.api;

public class ChangSangBean {

    /**
     * id : 134
     * pid : 4533235888218112
     * data_source : 447
     * pco : 00000000000000860101120000aa00460000009b006c000000000000000000f7fc1621299fac832d20431d86ec98df4620a1e691261a863a27e7273d788ba06787a4ede56fb3ca5ca490893f235015d5e23a66ef433a5f1d5fa44c27b7d2307cd634895c1b1135117444ae4767ec2eb722ed57aa7b68b919e5b14a9ae7b95d8bbc6fae100f6dd0351e99c89fc2cbb37b74ce438abb841b5c9e4dfa7d281e9b3f6d6276575496ff766707b0c1701279022e4ae8378a62acedb09b5c0483801ae3033291de211cc04c96f7e5aebf11031f8dbfd98b34d81075deed72f351ae8b4d2b532a438836890fb901f38b6808da4f0d776be932e98644900bcca1e07be48bd6a29963debe127712792d804a1d35a869f09a79eab8dac7408330cdc7812e945e15929e6e5d25d779c38a913e7954aa77efd255dce5395384d64123c7b9b89a78fc9205e35bf23663b52116af60b009d1bab522da0ce09f967a40498062c53167a4f910064f0aa4dcdf3a24060127e87941816c179040d7f7a4da725eca33fa8e437eee72997ec9bd26e106fc4ac1b30fbb79e92e9053ac20e13170253b6bcfbc50220018eb59c44a34fbbf14e740d459c778e2e87df01654f7c175a47128f373053daac07a8b4ed67d804a5d1f9c88e427640afb3874d688dc77272a1be56bda1418149eb4f6ca2fe9b07b0d337152c6f309a6708e181e58e571891b297fb7d71b4365bd
     * pco_size : 517
     * ts : 1638523231448
     * calcType : 1
     * state : 0
     */

    private int id;
    private long pid;
    private int data_source;
    private String pco;
    private int pco_size;
    private long ts;
    private int calcType;
    private int state;
    private int sys;//高压
    private int dia;//低压
    private long sts;//开始
    private long ets;//结束

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int getData_source() {
        return data_source;
    }

    public void setData_source(int data_source) {
        this.data_source = data_source;
    }

    public String getPco() {
        return pco;
    }

    public void setPco(String pco) {
        this.pco = pco;
    }

    public int getPco_size() {
        return pco_size;
    }

    public void setPco_size(int pco_size) {
        this.pco_size = pco_size;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public int getCalcType() {
        return calcType;
    }

    public void setCalcType(int calcType) {
        this.calcType = calcType;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
