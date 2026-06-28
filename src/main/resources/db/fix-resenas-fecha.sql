-- Ejecutar una sola vez si Hibernate no pudo crear la columna fecha
ALTER TABLE resenas ADD COLUMN IF NOT EXISTS fecha timestamp;
UPDATE resenas SET fecha = NOW() WHERE fecha IS NULL;

ALTER TABLE resenas_usuario ADD COLUMN IF NOT EXISTS fecha timestamp;
UPDATE resenas_usuario SET fecha = NOW() WHERE fecha IS NULL;
