package org.mossmc.mosscg.MossFrpBackend.Card;

import java.util.Random;

public class CardRandom {
    //参数示例：amount=#rd#500-1500
    public static Random random = new Random();
    public static int getCardRandom(String amount) {
        String[] spilt = amount.replace("#rd#","").split("-");
        int max = Integer.parseInt(spilt[1]);
        int min = Integer.parseInt(spilt[0]);
        return random.nextInt(max-min)+min;
    }
}
