package com.insurance_platform_springboot.service;

import com.insurance_platform_springboot.dtos.authDTO.LoginRequestDTO;
import com.insurance_platform_springboot.dtos.authDTO.RegisterRequestDTO;
import com.insurance_platform_springboot.dtos.authDTO.AuthResponseDTO;
import com.insurance_platform_springboot.dtos.response.UserResponseDTO;
import com.insurance_platform_springboot.dtos.update.UserUpdateDTO;
import com.insurance_platform_springboot.exception.ConflictException;
import com.insurance_platform_springboot.exception.ResourceNotFoundException;
import com.insurance_platform_springboot.mapper.UserMapper;
import com.insurance_platform_springboot.model.auth.Role;
import com.insurance_platform_springboot.model.auth.RoleConstants;
import com.insurance_platform_springboot.model.auth.User;
import com.insurance_platform_springboot.repository.RoleRepository;
import com.insurance_platform_springboot.repository.UserRepository;
import com.insurance_platform_springboot.security.JwtTokenProvider;
import com.insurance_platform_springboot.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio principal para la gestión de usuarios en el sistema.
 * Provee funcionalidades para el registro, autenticación (login),
 * y operaciones CRUD básicas sobre los usuarios.
 *
 * <p>Dependencias:</p>
 * <ul>
 *     <li>Repositorios: {@link UserRepository}, {@link RoleRepository}</li>
 *     <li>Seguridad: {@link PasswordEncoder}, {@link AuthenticationManager}, {@link JwtTokenProvider}, {@link UserDetailsServiceImpl}</li>
 *     <li>Mappers: {@link UserMapper}</li>
 *     <li>Notificaciones: {@link NotificationService}</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * <p>Realiza validaciones de unicidad para username y email, asigna el rol de cliente,
     * cifra la contraseña y, tras un registro exitoso, genera un token JWT para auto-login
     * y envía un correo de bienvenida de forma asíncrona.</p>
     *
     * @param dto Objeto RegisterRequestDTO con los datos del nuevo usuario.
     * @return AuthResponseDTO que contiene el token JWT y datos básicos del usuario.
     * @throws ConflictException Si el username o email ya están en uso.
     * @throws ResourceNotFoundException Si el rol por defecto no está configurado.
     */
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        // Validaciones de unicidad
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ConflictException("El username '" + dto.getUsername() + "' ya está en uso");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("El email '" + dto.getEmail() + "' ya está en uso");
        }

        // Obtención del rol de cliente
        Role roleUser = roleRepository.findByName(RoleConstants.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El rol " + RoleConstants.ROLE_USER + " no ha sido configurado en el sistema"));

        // Mapeo y preparación de entidad
        User user = userMapper.registerToEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(roleUser);
        user.setEnabled(true);
        user.setRegisteredAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());

        // Guardar en la base de datos
        User saved = userRepository.save(user);

        // Enviar correo de bienvenida (operación asíncrona que no bloquea la respuesta)
        notificationService.sendWelcomeEmail(saved.getEmail(), saved.getName());

        // Generar token JWT inmediatamente (Auto-login)
        UserDetails userDetails = userDetailsService.loadUserByUsername(saved.getEmail());
        String token = jwtTokenProvider.generateToken(userDetails);

        // Retornar la respuesta de autenticación
        return new AuthResponseDTO(
                token,
                saved.getEmail(),
                saved.getRole().getName());
    }
    
    /**
     * Autentica a un usuario en el sistema utilizando sus credenciales.
     * Si la autenticación es exitosa, genera y devuelve un token JWT.
     *
     * @param dto Objeto LoginRequestDTO con el email y la contraseña del usuario.
     * @return AuthResponseDTO que contiene el token JWT y datos básicos del usuario.
     * @throws ConflictException Si las credenciales son inválidas.
     * @throws ResourceNotFoundException Si el usuario no es encontrado después de la autenticación (caso raro).
     */
    public AuthResponseDTO login(LoginRequestDTO dto) {
        try {
            // 1. Intentar la autenticación con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(), dto.getPassword()));
        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            // 2. Si las credenciales son incorrectas
            throw new ConflictException("Credenciales inválidas");
        }

        // 3. Buscar al usuario en la base de datos tras la autenticación exitosa
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con email: " + dto.getEmail()));

        // 4. Cargar UserDetails para generar el token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtTokenProvider.generateToken(userDetails);

        // 5. Devolver la respuesta de autenticación
        return new AuthResponseDTO(
                token,
                user.getEmail(),
                user.getRole().getName());
    }

    /**
     * Obtiene una lista de todos los usuarios registrados en el sistema.
     *
     * @return Lista de UserResponseDTO con la información pública de los usuarios.
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> listarUsuarios(){
        return userRepository.findAll()
                .stream().map(userMapper::toResponse)
                .toList();
    }

    /**
     * Busca un usuario específico por su identificador único.
     *
     * @param id El ID del usuario a buscar.
     * @return UserResponseDTO con la información pública del usuario encontrado.
     * @throws ResourceNotFoundException Si no se encuentra un usuario con el ID proporcionado.
     */
    @Transactional(readOnly = true)
    public UserResponseDTO buscarPorId(Long id){
        User savedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no existe con ID " + id));
        return userMapper.toResponse(savedUser);
    }

    /**
     * Actualiza la información de un usuario existente.
     * Permite modificar el email y el estado 'enabled'.
     * Válida que el nuevo email no esté ya en uso por otro usuario.
     *
     * @param id El ID del usuario a actualizar.
     * @param dto Objeto UserUpdateDTO con los campos a actualizar.
     * @return UserResponseDTO con la información actualizada del usuario.
     * @throws ResourceNotFoundException Si no se encuentra un usuario con el ID proporcionado.
     * @throws ConflictException Si el nuevo email ya está en uso por otro usuario.
     */
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto){
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no existe con ID " + id));

        // validar el nuevo email
        if (dto.getEmail() != null &&
                !user.getEmail().equals(dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())){
            throw new ConflictException("El email " + dto.getEmail() +
                    " ya esta en uso");
        }
        userMapper.updateEntity(dto, user);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * Elimina un usuario del sistema por su identificador.
     *
     * @param id El ID del usuario a eliminar.
     * @throws ResourceNotFoundException Si no se encuentra un usuario con el ID proporcionado.
     */
    public void delete (Long id){
        User savedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El usuario no existe con el ID " + id));
        userRepository.deleteById(id);
    }
}