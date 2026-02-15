package com.srv.setebit.dropshipping.application.notification;

public interface EmailSenderPort {
    void sendTemporaryPassword(String toEmail, String toName, String tempPassword, String resetLink);
}
