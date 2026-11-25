package com.example.ejemplospring.repository;

import com.example.ejemplospring.dto.BeneficioDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JDBC para la tabla `beneficios`.
 * Usa consultas parametrizadas con `?` para prevenir SQL Injection.
 */
@Repository
public class BeneficioJdbcRepository {

    private final JdbcTemplate jdbc;

    // ── RowMapper reutilizable ────────────────────────────────────────────────
    private final RowMapper<BeneficioDTO> rowMapper = (rs, rowNum) -> {
        BeneficioDTO dto = new BeneficioDTO();
        dto.setIdBeneficio(rs.getInt("id_beneficio"));
        dto.setNombre(rs.getString("nombre"));
        dto.setDescripcion(rs.getString("descripcion"));
        dto.setCategoria(rs.getString("categoria"));
        dto.setEstado(rs.getString("estado"));
        dto.setFechaCreacion(rs.getObject("fecha_creacion", LocalDateTime.class));
        return dto;
    };

    public BeneficioJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ── Guardar nuevo beneficio ───────────────────────────────────────────────
    public BeneficioDTO save(BeneficioDTO dto) {
        String sql = """
                INSERT INTO beneficios (nombre, descripcion, categoria, estado)
                VALUES (?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getNombre().trim());
            ps.setString(2, dto.getDescripcion().trim());
            ps.setString(3, dto.getCategoria().trim());
            ps.setString(4, dto.getEstado() != null ? dto.getEstado() : "Activo");
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return findById(key.intValue()).orElseThrow();
    }

    // ── Buscar por ID ─────────────────────────────────────────────────────────
    public Optional<BeneficioDTO> findById(int id) {
        String sql = "SELECT * FROM beneficios WHERE id_beneficio = ?";
        List<BeneficioDTO> results = jdbc.query(sql, rowMapper, id);
        return results.stream().findFirst();
    }

    // ── Listar todos ──────────────────────────────────────────────────────────
    public List<BeneficioDTO> findAll() {
        return jdbc.query("SELECT * FROM beneficios ORDER BY fecha_creacion DESC", rowMapper);
    }

    // ── Verificar nombre duplicado (case-insensitive, sin espacios extra) ─────
    public boolean existsByNombreIgnoreCase(String nombre) {
        String sql = "SELECT COUNT(*) FROM beneficios WHERE LOWER(TRIM(nombre)) = LOWER(TRIM(?))";
        Integer count = jdbc.queryForObject(sql, Integer.class, nombre);
        return count != null && count > 0;
    }

    // ── Verificar que beneficio existe y está Activo ───────────────────────────
    public boolean existsActivoById(int idBeneficio) {
        String sql = "SELECT COUNT(*) FROM beneficios WHERE id_beneficio = ? AND estado = 'Activo'";
        Integer count = jdbc.queryForObject(sql, Integer.class, idBeneficio);
        return count != null && count > 0;
    }
}
