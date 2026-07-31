package com.insurance_platform_springboot.security;

import com.insurance_platform_springboot.model.auth.User;
import com.insurance_platform_springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Implementación personalizada de UserDetailsService para cargar usuarios desde la base de datos.
 * Spring Security utiliza este servicio durante el proceso de autenticación.
 * Ahora devuelve una instancia de {@link CustomUserDetails} para incluir el ID del usuario.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Busca un usuario en la base de datos por su email (utilizado como nombre de usuario).
     * Construye y devuelve un {@link CustomUserDetails} que Spring Security usa internamente.
     *
     * @param email Identificador único de acceso.
     * @return CustomUserDetails objeto que contiene el ID, email, password y autoridades del usuario.
     * @throws UsernameNotFoundException Si no se encuentra el usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email));

        // Mapeo del rol único a la colección de autoridades de Spring Security
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getName())
        );

        // Devolvemos nuestra implementación CustomUserDetails
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities
        );
    }
}
