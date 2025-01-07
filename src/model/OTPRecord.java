package model;

import java.sql.Timestamp;
import java.util.Date;

public class OTPRecord {

    private int id;
    private String email;
    private String otpCode;
    private boolean isUsed;
    private Date createdAt;

    public OTPRecord() {
        this.isUsed = false;
        this.createdAt = new Date();
    }

    public OTPRecord(String email, String otpCode) {
        this();
        this.email = email;
        this.otpCode = otpCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = new Date(createdAt.getTime());
    }
}
