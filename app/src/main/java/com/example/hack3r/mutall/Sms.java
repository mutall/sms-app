package com.example.hack3r.mutall;

public class Sms {
    private String smsBody;
    private String smsNumber;

    public Sms(String mobile, String smsMessage ){
        this.smsBody = smsMessage;
        this.smsNumber = mobile;
    }
    public String getSmsBody() {
        return smsBody;
    }

    public String getSmsNumber() {
        return smsNumber;
    }

    public void setSmsBody(String smsBody) {
        this.smsBody = smsBody;
    }

    public void setSmsNumber(String smsNumber) {
        this.smsNumber = smsNumber;
    }
}
