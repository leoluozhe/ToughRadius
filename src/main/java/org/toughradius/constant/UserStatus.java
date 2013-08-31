package org.toughradius.constant;

import java.util.ArrayList;
import java.util.List;


public class UserStatus {

    public final static IntConst Prepar = new IntConst(100,"Î´¼¤»î×´Ì¬");
    public final static IntConst Normal = new IntConst(101,"Õý³£×´Ì¬");
    public final static IntConst Pause = new IntConst(102,"Í£»ú×´Ì¬");
    public final static IntConst Expire = new IntConst(104,"µ½ÆÚ×´Ì¬");
    
    public final static List<IntConst> UserStatusList = new ArrayList<IntConst>();
    
    static 
    {
        UserStatusList.add(Prepar);
        UserStatusList.add(Normal);
        UserStatusList.add(Pause);
        UserStatusList.add(Expire);
    }
    
}
