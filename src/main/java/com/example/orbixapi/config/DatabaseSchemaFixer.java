package com.example.orbixapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaFixer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        fixResenasVehicleColumn();
        jdbcTemplate.execute("ALTER TABLE resenas ADD COLUMN IF NOT EXISTS fecha timestamp");
        jdbcTemplate.execute("UPDATE resenas SET fecha = NOW() WHERE fecha IS NULL");
        jdbcTemplate.execute("ALTER TABLE resenas_usuario ADD COLUMN IF NOT EXISTS fecha timestamp");
        jdbcTemplate.execute("UPDATE resenas_usuario SET fecha = NOW() WHERE fecha IS NULL");
    }

    private void fixResenasVehicleColumn() {
        if (!tableExists("resenas")) {
            return;
        }

        boolean hasVehicleId = columnExists("resenas", "vehicle_id");
        boolean hasVehiculoId = columnExists("resenas", "vehiculo_id");

        if (!hasVehicleId && hasVehiculoId) {
            jdbcTemplate.execute("ALTER TABLE resenas RENAME COLUMN vehiculo_id TO vehicle_id");
            hasVehicleId = true;
        }

        if (!hasVehicleId) {
            jdbcTemplate.execute("ALTER TABLE resenas ADD COLUMN IF NOT EXISTS vehicle_id bigint");
            jdbcTemplate.execute("DELETE FROM resena_tags WHERE resena_id IN (SELECT id FROM resenas WHERE vehicle_id IS NULL)");
            jdbcTemplate.execute("DELETE FROM resenas WHERE vehicle_id IS NULL");
        }

        jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF EXISTS (
                        SELECT 1 FROM information_schema.columns
                        WHERE table_name = 'resenas' AND column_name = 'vehicle_id' AND is_nullable = 'YES'
                    ) THEN
                        DELETE FROM resena_tags WHERE resena_id IN (SELECT id FROM resenas WHERE vehicle_id IS NULL);
                        DELETE FROM resenas WHERE vehicle_id IS NULL;
                        ALTER TABLE resenas ALTER COLUMN vehicle_id SET NOT NULL;
                    END IF;
                END $$
                """);

        jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1 FROM information_schema.table_constraints
                        WHERE table_name = 'resenas' AND constraint_name = 'fk_resenas_vehicle'
                    ) THEN
                        ALTER TABLE resenas
                            ADD CONSTRAINT fk_resenas_vehicle
                            FOREIGN KEY (vehicle_id) REFERENCES vehicles(id);
                    END IF;
                EXCEPTION
                    WHEN others THEN NULL;
                END $$
                """);

        if (columnExists("resenas", "created_at")) {
            jdbcTemplate.execute("ALTER TABLE resenas ALTER COLUMN created_at DROP NOT NULL");
            jdbcTemplate.execute("ALTER TABLE resenas ALTER COLUMN created_at SET DEFAULT NOW()");
            jdbcTemplate.execute("UPDATE resenas SET created_at = COALESCE(fecha, NOW()) WHERE created_at IS NULL");
        }

        if (columnExists("resenas", "reviewed_id")) {
            jdbcTemplate.execute("ALTER TABLE resenas ALTER COLUMN reviewed_id DROP NOT NULL");
        }
    }

    private boolean tableExists(String tableName) {
        Boolean exists = jdbcTemplate.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1 FROM information_schema.tables
                            WHERE table_schema = 'public' AND table_name = ?
                        )
                        """,
                Boolean.class,
                tableName
        );
        return Boolean.TRUE.equals(exists);
    }

    private boolean columnExists(String tableName, String columnName) {
        Boolean exists = jdbcTemplate.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1 FROM information_schema.columns
                            WHERE table_schema = 'public' AND table_name = ? AND column_name = ?
                        )
                        """,
                Boolean.class,
                tableName,
                columnName
        );
        return Boolean.TRUE.equals(exists);
    }
}
