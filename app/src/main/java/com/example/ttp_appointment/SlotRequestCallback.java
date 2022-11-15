package com.example.ttp_appointment;

public interface SlotRequestCallback {
    void processSuccess(String s);
    void processFailure(Exception e);
}
