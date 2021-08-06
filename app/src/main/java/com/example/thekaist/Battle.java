package com.example.thekaist;

public class Battle {
    private String ask;
    private String accept;
    private String name;
    private String accept_name;

    public Battle(String ask, String accept, String name, String accept_name) {
        this.ask = ask;
        this.accept = accept;
        this.name = name;
        this.accept_name = accept_name;
    }

    public String getAccept_name() {
        return accept_name;
    }

    public void setAccept_name(String accept_name) {
        this.accept_name = accept_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }
}
