package Proyecto.Gestor_Productos.Models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Token de un solo uso para confirmar el email de un {AppUser} recién
 * registrado (tabla {@code email_verification_tokens}).
 * <p>
 * El valor guardado en {@code tokenHash} es un hash (no el token en texto
 * plano) para que, si la base de datos se ve comprometida, el token real
 * enviado por correo no pueda deducirse ni reutilizarse directamente.
 */
@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    /** Hash (no el valor original) del token enviado por correo. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    /** Momento a partir del cual este token deja de ser válido. */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** Marca si el token ya fue consumido, para impedir que se use más de una vez. */
    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public EmailVerificationToken() {
    }

    /** Asigna automáticamente la fecha de creación justo antes del primer guardado,
     *  sin depender de que el código que lo crea recuerde hacerlo. */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailVerificationToken that = (EmailVerificationToken) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
