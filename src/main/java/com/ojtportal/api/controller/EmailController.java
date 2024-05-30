package com.ojtportal.api.controller;

import com.ojtportal.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/auth/send")
    public String sendEmail(String toEmail, String subject, String body) {
        return emailService.sendSimpleMail(toEmail, subject, body);
    }
}
