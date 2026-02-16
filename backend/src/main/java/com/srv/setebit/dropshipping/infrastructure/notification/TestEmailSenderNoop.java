package com.srv.setebit.dropshipping.infrastructure.notification;

import com.srv.setebit.dropshipping.application.notification.EmailSenderPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestEmailSenderNoop implements EmailSenderPort {

    @Override
    public void sendTemporaryPassword(String toEmail, String toName, String tempPassword, String resetLink) {
    }
}
