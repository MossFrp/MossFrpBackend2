package org.mossmc.mosscg.MossFrpBackend.Code;

import java.util.Random;

public class CodeEncode {
    public static Random random = new Random();

    public static String encode(String node,Integer frpNumber,Integer port) {
        String output;
        Integer randomPart = random.nextInt(20000)+10000;
        int nodeLength = node.length();
        int portPart = port+randomPart;
        int frpNumberPart = frpNumber+randomPart;
        output = nodeLength+node+randomPart+portPart+frpNumberPart;
        //举例 node = "zz1" ; port = 40000 ; frpNumber = 1000000
        //output = "3zz123333633331023333"
        //3 zz1 23333 63333 1023333
        return output;
    }
}
