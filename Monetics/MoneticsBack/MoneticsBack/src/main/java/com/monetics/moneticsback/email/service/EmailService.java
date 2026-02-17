package com.monetics.moneticsback.email.service;

import com.monetics.moneticsback.email.content.EmailDetails;

public interface EmailService {
    void sendSimpleMail(EmailDetails details);
}
