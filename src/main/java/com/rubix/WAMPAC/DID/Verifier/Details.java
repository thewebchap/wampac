package com.rubix.WAMPAC.DID.Verifier;


public class Details {

    private String peerid;
    private String role;
    private String ip;
    private String status;

    private String didHash;
    private String walletHash;





    public Details(){}

    public String getPeerid(){return peerid;}
    public String getRole(){return role;}
    public String getIp(){ return ip;}
    public String getStatus(){return status;}

    public String getDidHash(){ return didHash;}
    public String getWalletHash(){return walletHash;}


}