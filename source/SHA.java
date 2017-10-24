package org.androidtown.materialpractice;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ahsxj on 2017-07-27.
 */

public class SHA {

    /**
     * setSHA() : SHA-256을 하기위한 스태틱 메소드
     */

    static public String setSHA(String str)
    {
        String SHA = "";
        try
        {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for(int i = 0 ; i < byteData.length ; i++)
            {
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            }
            SHA = sb.toString();
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            SHA = null;
        }
        return SHA;
    }

}
