package com.srv.setebit.dropshipping.infrastructure.notification;

import com.srv.setebit.dropshipping.application.notification.EmailSenderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import jakarta.mail.internet.MimeMessage;
import java.io.File;

@Component
@Profile("!test")
public class GmailEmailSenderAdapter implements EmailSenderPort {

  private final JavaMailSender mailSender;
  private final String from;
  private final String fixedRecipient;
  private final String baseUrl;
  private final String logoPath;

  public GmailEmailSenderAdapter(
      JavaMailSender mailSender,
      @Value("${MAIL_FROM}") String from,
      @Value("${MAIL_FIXED_RECIPIENT}") String fixedRecipient,
      @Value("${APP_BASE_URL:http://localhost:8080}") String baseUrl,
      @Value("${APP_EMAIL_LOGO_PATH:d:\\Dropshipping\\dropshipping\\frontend\\public\\assets\\logo-email.jpeg}") String logoPath) {
    this.mailSender = mailSender;
    this.from = from;
    this.fixedRecipient = fixedRecipient;
    this.baseUrl = baseUrl;
    this.logoPath = logoPath;
  }

  @Override
  public void sendTemporaryPassword(String toEmail, String toName, String tempPassword, String resetLink) {
    String subject = "Sua senha temporária";
    String link = (resetLink != null ? resetLink : baseUrl + "/login?email=" + toEmail);
    String html = buildHtml(toName, tempPassword, link);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(from);
      helper.setTo(fixedRecipient);
      helper.setSubject(subject);
      helper.setText(html, true);

      File logoFile = new File(logoPath);
      if (logoFile.exists()) {
        FileSystemResource res = new FileSystemResource(logoFile);
        helper.addInline("logoCid", res);
      }

      mailSender.send(message);
    } catch (Exception e) {
      // silencioso por enquanto; em produção, enviar para observabilidade
    }
  }

  private String buildHtml(String name, String tempPassword, String link) {
    String displayName = (name != null ? name : "");
    int year = java.time.Year.now().getValue();
    return """
        <html>
        <head>
          <meta charset="UTF-8">
          <style>
            body { font-family: Arial, sans-serif; background:#f7f7f9; margin:0; padding:24px; }
            .card { max-width:600px; margin:0 auto; background:#fff; border-radius:12px; box-shadow:0 6px 20px rgba(0,0,0,0.08); overflow:hidden; }
            .header { background:#0b5ed7; color:#fff; padding:20px; display:flex; align-items:center; gap:12px; }
            .header img { height:42px; width:auto; border-radius:6px; }
            .content { padding:24px; color:#333; }
            .title { font-size:20px; margin:0 0 8px 0; }
            .subtitle { color:#666; margin:0 0 16px 0; }
            .password { background:#f0f7ff; border:1px solid #cfe2ff; color:#0b5ed7; font-weight:bold; padding:10px 14px; border-radius:8px; display:inline-block; letter-spacing:0.5px; }
            .cta { margin-top:20px; }
            .button { background:#0b5ed7; color:#fff; text-decoration:none; padding:12px 18px; border-radius:8px; display:inline-block; }
            .footer { font-size:12px; color:#888; padding:16px 24px; }
          </style>
        </head>
        <body>
          <div class="card">
            <div class="header">
              <img src="cid:logoCid" alt="Logo">
              <div>DropSeller • Segurança de Acesso</div>
            </div>
            <div class="content">
              <h1 class="title">Olá %s!</h1>
              <p class="subtitle">Aqui está sua senha temporária. Use-a para entrar e, em seguida, crie uma nova senha.</p>
              <div class="password">%s</div>
              <div class="cta">
                <a class="button" href="%s" target="_blank" rel="noopener">Clique aqui para resolver</a>
              </div>
              <p style="margin-top:16px;color:#666;">Se não foi você quem solicitou, ignore este email.</p>
            </div>
            <div class="footer">
              © %d DropSeller. Todos os direitos reservados.
            </div>
          </div>
        </body>
        </html>
        """
        .formatted(displayName, tempPassword, link, year);
  }
}
