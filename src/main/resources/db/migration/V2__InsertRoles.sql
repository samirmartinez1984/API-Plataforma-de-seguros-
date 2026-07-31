-- Insertar roles predefinidos del sistema (Tabla corregida a 'roles')
INSERT INTO roles (name, description, created_at) VALUES
('ROLE_ADMIN', 'Administrador del sistema', NOW()),
('ROLE_USER', 'Usuario estándar', NOW()),
('ROLE_SUPERVISOR', 'Supervisor de reclamos', NOW());