package com.soracel.library_system.service;

public interface EmailService {

    void sendEmail(String recipient, String subject, String body);
}
