package com.huateng.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xmlVo")
public class XmlVo {

    @XStreamAlias("username")
    private String name;

    private int    age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
