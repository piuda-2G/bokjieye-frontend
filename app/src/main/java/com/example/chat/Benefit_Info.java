package com.example.chat;

//한사용자에 대한 정보를  담는 부분
public class Benefit_Info {

    String B_id;
    String B_name;
    String B_contents;
    String B_who;
    String B_howto;

    public String getB_id() {
        return B_id;
    }

    public String getB_name() {
        return B_name;
    }

    public void setB_name(String b_name) {
        B_name = b_name;
    }

    public String getB_contents() {
        return B_contents;
    }

    public void setB_contents(String b_contents) {
        B_contents = b_contents;
    }

    public String getB_who() {
        return B_who;
    }

    public void setB_who(String b_who) {
        B_who = b_who;
    }

    public String getB_howto() {
        return B_howto;
    }

    public void setB_howto(String b_howto) {
        B_howto = b_howto;
    }

    //생성자
    public Benefit_Info(String b_id, String b_name, String b_contents, String b_who, String b_howto) {
        B_id = b_id;
        B_name = b_name;
        B_contents = b_contents;
        B_who = b_who;
        B_howto = b_howto;
    }
    public Benefit_Info(String b_id, String b_name, String b_contents) {
        B_id = b_id;
        B_name = b_name;
        B_contents = b_contents;
    }
}