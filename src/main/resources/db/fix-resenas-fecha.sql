-- Ejecutar en PostgreSQL si fallan las reseñas de vehículos
ALTER TABLE resenas ADD COLUMN IF NOT EXISTS vehicle_id bigint;
ALTER TABLE resenas RENAME COLUMN vehiculo_id TO vehicle_id; -- solo si existe vehiculo_id

DELETE FROM resena_tags WHERE resena_id IN (SELECT id FROM resenas WHERE vehicle_id IS NULL);
DELETE FROM resenas WHERE vehicle_id IS NULL;
ALTER TABLE resenas ALTER COLUMN vehicle_id SET NOT NULL;

ALTER TABLE resenas ADD COLUMN IF NOT EXISTS fecha timestamp;
UPDATE resenas SET fecha = NOW() WHERE fecha IS NULL;

ALTER TABLE resenas_usuario ADD COLUMN IF NOT EXISTS fecha timestamp;
UPDATE resenas_usuario SET fecha = NOW() WHERE fecha IS NULL;
