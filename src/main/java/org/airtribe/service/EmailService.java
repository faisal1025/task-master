package org.airtribe.service;

import org.airtribe.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendVerificationEmail(User user, String token) {
        // In a real app you'd send an email. For now we log the link so it can be copied during development.
        String link = String.format("http://localhost:9090/api/auth/verify-email?token=%s", token);
        log.info("Sending verification email to {} with link: {}", user.getEmail(), link);
    }
}

