package com.insurance_platform_springboot.service;

import com.insurance_platform_springboot.dtos.request.RoleRequestDTO;
import com.insurance_platform_springboot.dtos.response.RoleResponseDTO;
import com.insurance_platform_springboot.dtos.update.RoleUpdateDTO;
import com.insurance_platform_springboot.exception.ConflictException;
import com.insurance_platform_springboot.exception.ResourceNotFoundException;
import com.insurance_platform_springboot.mapper.RoleMapper;
import com.insurance_platform_springboot.model.auth.Role;
import com.insurance_platform_springboot.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de roles en el sistema.
 *
 * <p>Centraliza toda la lógica de negocio relacionada con roles,
 * incluyendo operaciones CRUD y validaciones específicas del dominio.</p>
 *
 * <p>Dependencias inyectadas:</p>
 * <ul>
 *     <li>{@link RoleRepository} - Acceso a datos de roles</li>
 *     <li>{@link RoleMapper} - Conversión entre entidades y DTOs</li>
 * </ul>
 *
 * <p>Validaciones de negocio:</p>
 * <ul>
 *     <li>No se puede eliminar un rol que tiene usuarios asociados</li>
 * </ul>
 *
 * @author Sistema de Seguros
 * @version 1.0
 * @see Role
 * @see RoleRepository
 * @see RoleMapper
 */
@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    /**
     * Crea un nuevo rol en el sistema.
     *
     * <p>Transforma el DTO de entrada en una entidad Role,
     * la persiste en la base de datos y retorna la respuesta.</p>
     *
     * @param dto {@link RoleRequestDTO} con los datos del rol (name y description)
     * @return {@link RoleResponseDTO} con los datos del rol creado (id y name)
     */
    @Transactional
    public RoleResponseDTO create(RoleRequestDTO dto) {
        Role role = roleMapper.toEntity(dto);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponse(savedRole);
    }

    /**
     * Obtiene todos los roles del sistema.
     *
     * <p>Recupera la lista completa de roles de la base de datos
     * y los convierte a DTOs de respuesta.</p>
     *
     * @return {@link List} de {@link RoleResponseDTO} con todos los roles
     */
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> getAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un rol específico por su ID.
     *
     * <p>Busca un rol en la base de datos por su identificador único.
     * Si no existe, lanza una excepción ResourceNotFoundException.</p>
     *
     * @param id Identificador único del rol
     * @return {@link RoleResponseDTO} con los datos del rol
     * @throws ResourceNotFoundException si el rol no existe en la BD (HTTP 404)
     */
    @Transactional(readOnly = true)
    public RoleResponseDTO getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rol no encontrado con id: " + id));
        return roleMapper.toResponse(role);
    }

    /**
     * Actualiza parcialmente un rol existente.
     *
     * <p>Busca el rol por ID, actualiza solo los campos no-nulos del DTO
     * (gracias a MapStruct @MappingTarget), persiste los cambios
     * y retorna el resultado.</p>
     *
     * @param id Identificador del rol a actualizar
     * @param dto {@link RoleUpdateDTO} con los campos a actualizar
     * @return {@link RoleResponseDTO} con los datos del rol actualizado
     * @throws ResourceNotFoundException si el rol no existe (HTTP 404)
     */
    @Transactional
    public RoleResponseDTO updateRole(Long id, RoleUpdateDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rol no encontrado con id: " + id));

        roleMapper.updateEntity(dto, role);
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toResponse(updatedRole);
    }

    /**
     * Elimina un rol del sistema.
     *
     * <p>Antes de eliminar, válida que el rol no tenga usuarios asociados.
     * Si tiene usuarios, lanza ConflictException para proteger la integridad referencial.</p>
     *
     * @param id Identificador del rol a eliminar
     * @throws ResourceNotFoundException si el rol no existe (HTTP 404)
     * @throws ConflictException si el rol tiene usuarios asociados (HTTP 409)
     */
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rol no encontrado con id: " + id));

        if (!role.getUsers().isEmpty()) {
            throw new ConflictException(
                    "No puedes eliminar un rol que tiene usuarios asociados");
        }
        roleRepository.delete(role);
    }
}
