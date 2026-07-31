package com.insurance_platform_springboot.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Componente responsable de la generación, firma y validación de tokens JWT.
 * Utiliza el algoritmo HS512 y una clave secreta configurada en las propiedades de la aplicación.
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    /**
     * Genera un nuevo token JWT a partir de los detalles del usuario autenticado.
     * @param userDetails Información del usuario autenticado.
     * @return Token JWT firmado.
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (subject) contenido en un token JWT.
     * @param token Token JWT.
     * @return Nombre de usuario.
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Válida si un token es correcto y pertenece al usuario indicado.
     * @param token Token JWT a validar.
     * @param userDetails Detalles del usuario para contrastar.
     * @return True si el token es válido, false en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Verifica si el token ha superado su fecha de expiración.
     */
    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    /**
     * Parsea los claims (información) del token verificado.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene la clave de firma a partir del secreto configurado.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
