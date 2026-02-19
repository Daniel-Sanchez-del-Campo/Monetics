package com.monetics.moneticsback.service;

import com.monetics.moneticsback.email.content.EmailDetails;
import com.monetics.moneticsback.email.service.EmailService;
import com.monetics.moneticsback.model.PasswordResetToken;
import com.monetics.moneticsback.model.Usuario;
import com.monetics.moneticsback.repository.PasswordResetTokenRepository;
import com.monetics.moneticsback.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository tokenRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void solicitarReset(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        // No revelamos si el email existe o no (seguridad)
        if (usuario == null) {
            return;
        }

        // Eliminar tokens previos del usuario
        tokenRepository.deleteByUsuario(usuario);

        // Generar token UUID con expiración de 30 minutos
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                usuario,
                LocalDateTime.now().plusMinutes(30)
        );
        tokenRepository.save(resetToken);

        // Enviar email con enlace de reset
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(usuario.getEmail());
        emailDetails.setSubject("Monetics - Recuperar contraseña");
        emailDetails.setMsgBody(
                "Hola " + usuario.getNombre() + ",\n\n" +
                "Has solicitado recuperar tu contraseña en Monetics.\n\n" +
                "Haz clic en el siguiente enlace para establecer una nueva contraseña:\n" +
                resetLink + "\n\n" +
                "Este enlace expira en 30 minutos.\n\n" +
                "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                "Saludos,\nEquipo Monetics"
        );

        emailService.sendSimpleMail(emailDetails);
    }

    @Transactional
    public boolean resetearPassword(String token, String nuevaPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElse(null);

        if (resetToken == null || resetToken.isExpirado()) {
            return false;
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        // Eliminar token usado
        tokenRepository.delete(resetToken);

        return true;
    }
}
