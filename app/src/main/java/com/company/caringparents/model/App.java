package com.company.caringparents.model;

public class App {

    private Long id;
    private String name;
    private Long last_time_used;
    private Long total_time;
    private byte[] icon;

    public App(String name, Long last_time_used, Long total_time, byte[] icon) {
        this.name = name;
        this.last_time_used = last_time_used;
        this.total_time = total_time;
        this.icon = icon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLast_time_used() {
        return last_time_used;
    }

    public void setLast_time_used(Long last_time_used) {
        this.last_time_used = last_time_used;
    }

    public Long getTotal_time() {
        return total_time;
    }

    public void setTotal_time(Long total_time) {
        this.total_time = total_time;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
}
