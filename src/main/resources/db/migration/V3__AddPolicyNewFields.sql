-- Migration V3__AddPolicyNewFields.sql
-- Añade los nuevos campos de precio (basePrice, discountPercentage, extraCharges) a la tabla 'policies'.

ALTER TABLE policies
ADD COLUMN base_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
ADD COLUMN discount_percentage INT,
ADD COLUMN extra_charges DECIMAL(10, 2);