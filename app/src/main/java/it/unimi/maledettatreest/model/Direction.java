package it.unimi.maledettatreest.model;

public class Direction {

    public static final String DID = "did";
    public static final String REVERSE_DID = "reverseDid";
    public static final String REVERSE_SNAME = "reverseSname";
    public static final String SNAME = "sname";


    private final String lname, did, sname, reverseDid, reverseSname;

    public Direction(String lname, String did, String sname, String reverseDid, String reverseSname) {
        this.lname = lname;
        this.did = did;
        this.sname = sname;
        this.reverseDid = reverseDid;
        this.reverseSname = reverseSname;
    }

    public String getDid() {
        return did;
    }

    public String getLname() {
        return lname;
    }

    public String getReverseDid() {
        return reverseDid;
    }

    public String getReverseSname() {
        return reverseSname;
    }

    public String getSname() {
        return sname;
    }
}
