package com.example.bluetoothscan;

public class holder {
    private String Animate;
    private String Time;
    private Boolean state;
    private int btnresid;

    public int getBtnresid() {
        return btnresid;
    }

    public void setBtnresid(int btnresid) {
        this.btnresid = btnresid;
    }

    public holder(String animate, String time, Boolean a){
        Animate = animate;
        Time = time;
        state = a;
    }
    public holder(String animate, String time, int btnresid) {
        Animate = animate;
        Time = time;
        this.btnresid = btnresid;
        state = false;
    }
    public holder(){
        Animate = "";
        Time = "";
        btnresid = R.drawable.ic_action_name;
        state = false;
    }
    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getAnimate() {
        return Animate;
    }

    public String getTime() {
        return Time;
    }

    public void setAnimate(String animate) {
        Animate = animate;
    }

    public void setTime(String time) {
        Time = time;
    }
}
