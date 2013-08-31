/*
 * 版权所有 (C) 2001-2009 深圳市凌亚科技有限公司。保留所有权利。
 * 版本：
 * 修改记录：
 *		1、2009-3-10，wangjuntao创建。 
 */
package org.toughradius.constant;

final public class StringConst
{
    private String value;
    private String desc;
    
    public StringConst(String value,String desc)
    {
        this.value = value;
        this.desc = desc;
    }
    public String value()
    {
        return value;
    }
    public String desc()
    {
        return desc;
    }
    
    public String toString()
    {
        return value;
    }
}
