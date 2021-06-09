package com.rubix.WAMPAC.NMS.NLSS;

import java.io.IOException;

public class Interact
{
    public String secretstring="", y1string ="",y2string="",y3string="",y4string="",y5string="",y6string="",bits,strs;
    public StringBuilder pvt, cnd1,cnd2,cnd3,cnd4,cnd5,cnd6;
    public int cand1[][],cand2[][],cand3[][],cand4[][],cand5[][],cand6[][],secret[][];

    public Interact(String s){
        bits = s ;
    }



    public void sharecreate()
    {
        bits=bits.replaceAll("\\s+","");
        System.out.println(bits.length());
        int i,j;
        secret= new int[bits.length()][8];
        cand1 = new int[bits.length()][8];
        cand2 = new int[bits.length()][8];
        cand3 = new int[bits.length()][8];
        cand4 = new int[bits.length()][8];
//        cand5 = new int[bits.length()][8];
//        cand6 = new int[bits.length()][8];

        SecretShare share;
        pvt = new StringBuilder();
        cnd1 = new StringBuilder();
        cnd2 = new StringBuilder();
        cnd3 = new StringBuilder();
        cnd4 = new StringBuilder();
//        cnd5 = new StringBuilder();
//        cnd6 = new StringBuilder();
        for(i=0;i<bits.length();i++)
        {
            if(bits.charAt(i)=='0')
            {
                share = new SecretShare(0);
                share.starts();
                for(j=0;j<8;j++)
                {
                    secret[i][j]=share.S0[j];
                    cand1[i][j]=share.Y1[j];
                    cand2[i][j]=share.Y2[j];
                    cand3[i][j]=share.Y3[j];
                    cand4[i][j]=share.Y4[j];
//                    cand5[i][j]=share.Y5[j];
//                    cand6[i][j]=share.Y6[j];

                    pvt.append(share.S0[j]);
                    cnd1.append(share.Y1[j]);
                    cnd2.append(share.Y2[j]);
                    cnd3.append(share.Y3[j]);
                    cnd4.append(share.Y4[j]);
//                    cnd5.append(share.Y5[j]);
//                    cnd6.append(share.Y6[j]);
                }
            }
            if(bits.charAt(i)=='1')
            {
                share = new SecretShare(1);
                share.starts();
                for(j=0;j<8;j++)
                {
                    secret[i][j]=share.S0[j];
                    cand1[i][j]=share.Y1[j];
                    cand2[i][j]=share.Y2[j];
                    cand3[i][j]=share.Y3[j];
                    cand4[i][j]=share.Y4[j];
//                    cand5[i][j]=share.Y5[j];
//                    cand6[i][j]=share.Y6[j];

                    pvt.append(share.S0[j]);
                    cnd1.append(share.Y1[j]);
                    cnd2.append(share.Y2[j]);
                    cnd3.append(share.Y3[j]);
                    cnd4.append(share.Y4[j]);
//                    cnd5.append(share.Y5[j]);
//                    cnd6.append(share.Y6[j]);
                }
            }

        }

        secretstring = pvt.toString();
        y1string = cnd1.toString();
        y2string = cnd2.toString();
        y3string = cnd3.toString();
        y4string = cnd4.toString();
//        y5string = cnd5.toString();
//        y6string = cnd6.toString();


    }
    public boolean checkshare() throws IOException {
        int i,j,sum1,sum2,sum3,sum4,sum5,sum6;
        boolean verified = true;

        for(i=0;i<secret.length;i++){
            sum1=0;sum2=0;sum3=0;sum4=0;sum5=0;sum6=0;
            for(j=0;j<secret[i].length;j++) {
                sum1 += secret[i][j] * cand1[i][j];
                sum2 += secret[i][j] * cand2[i][j];
                sum3 += secret[i][j] * cand3[i][j];
                sum4 += secret[i][j] * cand4[i][j];
//                sum5 += secret[i][j] * cand5[i][j];
//                sum6 += secret[i][j] * cand6[i][j];
            }
            sum1%=2;
            sum2%=2;
            sum3%=2;
            sum4%=2;
//            sum5%=2;
//            sum6%=2;



            if(sum1!=(bits.charAt(i)-48)|sum2!=(bits.charAt(i)-48)|sum3!=(bits.charAt(i)-48)|sum4!=(bits.charAt(i)-48)/*|sum5!=(bits.charAt(i)-48)|sum6!=(bits.charAt(i)-48)*/)
                verified = false;


        }
        if(verified==true)
        {
            System.out.println("Verified :Correct");
//            Functions.writeToFile("pvtshare.txt",secretstring,false);
//            Functions.writeToFile("candshare1.txt",y1string,false);
//            Functions.writeToFile("candshare2.txt",y2string,false);
//            Functions.writeToFile("candshare3.txt",y3string,false);
//            Functions.writeToFile("candshare4.txt",y4string,false);
//            Functions.writeToFile("candshare5.txt",y5string,false);
//            Functions.writeToFile("candshare6.txt",y6string,false);

//            System.out.println(secretstring.length());
//            System.out.println(y1string.length());
            return true;
        }
        else
            System.out.println("Verified :Wrong");
            return false;

    }

    public static String getback(String s1,String s2) throws IOException {
        int i,j,temp,temp1,sum;
        if(s1.length()!=s2.length()||s1.length()<1){
            System.out.println("Shares corrupted");
            return "Shares corrupted";
        }

        StringBuilder tempo = new StringBuilder(),result = new StringBuilder();

        char nextChar;
        for(i=0;i<s1.length();i+=8){
            sum=0;
            for(j=i;j<i+8;j++){
                temp = s1.charAt(j)-'0';
                temp1 = s2.charAt(j)-'0';
                sum+=temp*temp1;
            }
            sum%=2;
            tempo.append(sum);
        }

        for(i = 0; i < tempo.length(); i +=8)
        {
            nextChar = (char)Integer.parseInt(tempo.substring(i, i+8), 2);
            result.append(nextChar);
        }
        return result.toString();
    }



}


