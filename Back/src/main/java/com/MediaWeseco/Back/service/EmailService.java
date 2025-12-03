package com.MediaWeseco.Back.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}