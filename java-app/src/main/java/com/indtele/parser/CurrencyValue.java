package com.indtele.parser;

public class CurrencyValue
{
    public String fullString;
    public String approxString;

    public CurrencyValue(String fullString, String approxString)
    {
        this.fullString = fullString;
        this.approxString = approxString;
    }

    public long numericValue()
    {
        throw new RuntimeException("impl pending");
    }
}
