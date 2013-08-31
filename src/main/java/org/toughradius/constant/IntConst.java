/*
 * 版权所有 (C) 2001-2009 深圳市凌亚科技有限公司。保留所有权利。
 * 版本：
 * 修改记录：
 *		1、2009-3-10，wangjuntao创建。 
 */
package org.toughradius.constant;
final public class IntConst
{
    private int value = -1;
    private String desc = "未知";
    
    public IntConst(int status,String desc)
    {
        this.value = status;
        this.desc = desc;
    }
    public int value()
    {
        return value;
    }
    public String desc()
    {
        return desc;
    }
    
    public String toString()
    {
        return String.valueOf(value);
    }

}
