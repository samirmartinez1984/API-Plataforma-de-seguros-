-- Migration V1__Init.sql
-- Inicializa las tablas principales para la Insurance Platform.
-- Este script está pensado para MySQL (BIGINT AUTO_INCREMENT, CURRENT_TIMESTAMP).
-- Contiene tablas: roles, users, catalog_products, policies, partners, claims

-- Using MySQL-compatible types and names (BIGINT AUTO_INCREMENT, CURRENT_TIMESTAMP)

-- Tabla 'roles': guarda los roles disponibles en el sistema (ADMIN, USER, ...)
CREATE TABLE roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla 'users': usuarios del sistema. Relacionada con 'roles'.
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150),
  username VARCHAR(100) UNIQUE,
  email VARCHAR(150) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  registered_at TIMESTAMP NULL,
  role_id BIGINT NOT NULL,
  enabled BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- Tabla 'catalog_products': productos/servicios ofertados.
CREATE TABLE catalog_products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name_product VARCHAR(150) NOT NULL,
  description TEXT,
  coverage TEXT,
  exclusions TEXT,
  base_price DECIMAL(10,2) NOT NULL,
  type VARCHAR(50) NOT NULL,
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla 'policies': pólizas contratadas por usuarios sobre productos del catálogo.
CREATE TABLE policies (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  status VARCHAR(50) NOT NULL,
  final_price DECIMAL(10,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_policies_customer FOREIGN KEY (customer_id) REFERENCES users (id),
  CONSTRAINT fk_policies_product FOREIGN KEY (product_id) REFERENCES catalog_products (id)
);

-- Tabla 'partners': socios/proveedores (talleres, clínicas, laboratorios...)
CREATE TABLE partners (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  partner_name VARCHAR(150) NOT NULL,
  type VARCHAR(50) NOT NULL,
  address VARCHAR(255),
  phone VARCHAR(50),
  email VARCHAR(150),
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla 'claims': reclamos asociados a pólizas.
CREATE TABLE claims (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  policy_id BIGINT NOT NULL,
  customer_id BIGINT NOT NULL,
  supervisor_id BIGINT NOT NULL,
  partner_id BIGINT,
  description TEXT,
  reported_at TIMESTAMP NOT NULL,
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_claims_policy FOREIGN KEY (policy_id) REFERENCES policies (id),
  CONSTRAINT fk_claims_customer FOREIGN KEY (customer_id) REFERENCES users (id),
  CONSTRAINT fk_claims_supervisor FOREIGN KEY (supervisor_id) REFERENCES users (id),
  CONSTRAINT fk_claims_partner FOREIGN KEY (partner_id) REFERENCES partners (id)
 );
