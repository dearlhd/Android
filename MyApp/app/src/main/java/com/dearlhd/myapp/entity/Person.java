package com.dearlhd.myapp.entity;

/**
 * Created by dearlhd on 2016/6/26.
 */
public class Person {
    private String name;
    private String info;
    private int aIcon;

    public Person () {

    }

    public Person (String nn, String ii, int aa) {
        name = nn;
        info = ii;
        aIcon = aa;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getaIcon() {
        return aIcon;
    }

    public void setaIcon(int aIcon) {
        this.aIcon = aIcon;
    }
}
