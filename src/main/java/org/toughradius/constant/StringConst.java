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
