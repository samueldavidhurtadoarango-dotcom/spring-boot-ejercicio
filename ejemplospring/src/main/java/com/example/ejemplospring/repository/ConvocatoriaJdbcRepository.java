package com.example.ejemplospring.repository;

import com.example.ejemplospring.dto.ConvocatoriaDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JDBC para la tabla `convocatorias`.
 * Incluye un INNER JOIN con `beneficios` para traer el nombre del beneficio padre.
 */
@Repository
public class ConvocatoriaJdbcRepository {

    private final JdbcTemplate jdbc;

    // ── RowMapper con INNER JOIN ──────────────────────────────────────────────
    private final RowMapper<ConvocatoriaDTO> rowMapper = (rs, rowNum) -> {
        ConvocatoriaDTO dto = new ConvocatoriaDTO();
        dto.setIdConvocatoria(rs.getInt("id_convocatoria"));
        dto.setIdBeneficio(rs.getInt("id_beneficio"));
        dto.setNombreBeneficio(rs.getString("nombre_beneficio"));
        dto.setNombre(rs.getString("nombre_convocatoria"));
        dto.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        dto.setFechaCierre(rs.getDate("fecha_cierre").toLocalDate());
        dto.setCupos(rs.getInt("cupos"));
        dto.setValorPago(rs.getBigDecimal("valor_pago"));
        dto.setEstado(rs.getString("estado_convocatoria"));
        dto.setFechaCreacion(rs.getObject("fecha_creacion", LocalDateTime.class));
        return dto;
    };

    // ── SQL base con INNER JOIN ───────────────────────────────────────────────
    private static final String BASE_SELECT = """
            SELECT
                c.id_convocatoria,
                c.id_beneficio,
                b.nombre          AS nombre_beneficio,
                c.nombre          AS nombre_convocatoria,
                c.fecha_inicio,
                c.fecha_cierre,
                c.cupos,
                c.valor_pago,
                c.estado          AS estado_convocatoria,
                c.fecha_creacion
            FROM convocatorias c
            INNER JOIN beneficios b ON c.id_beneficio = b.id_beneficio
            """;

    public ConvocatoriaJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ── Guardar convocatoria ──────────────────────────────────────────────────
    public ConvocatoriaDTO save(ConvocatoriaDTO dto) {
        String sql = """
                INSERT INTO convocatorias
                    (id_beneficio, nombre, fecha_inicio, fecha_cierre, cupos, valor_pago, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, dto.getIdBeneficio());
            ps.setString(2, dto.getNombre().trim());
            ps.setDate(3, Date.valueOf(dto.getFechaInicio()));
            ps.setDate(4, Date.valueOf(dto.getFechaCierre()));
            ps.setInt(5, dto.getCupos());
            ps.setBigDecimal(6, dto.getValorPago());
            ps.setString(7, dto.getEstado() != null ? dto.getEstado() : "Inactiva");
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return findById(key.intValue()).orElseThrow();
    }

    // ── Buscar por ID (con JOIN) ──────────────────────────────────────────────
    public Optional<ConvocatoriaDTO> findById(int id) {
        String sql = BASE_SELECT + "WHERE c.id_convocatoria = ?";
        List<ConvocatoriaDTO> results = jdbc.query(sql, rowMapper, id);
        return results.stream().findFirst();
    }

    // ── Listar todos (con JOIN) ───────────────────────────────────────────────
    public List<ConvocatoriaDTO> findAll() {
        return jdbc.query(BASE_SELECT + "ORDER BY c.fecha_creacion DESC", rowMapper);
    }

    // ── Filtrar por estado obligatorio y categoría opcional (JOIN incluido) ───
    public List<ConvocatoriaDTO> findByEstadoAndCategoria(String estado, String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            String sql = BASE_SELECT +
                    "WHERE c.estado = ? AND b.categoria = ? ORDER BY c.fecha_creacion DESC";
            return jdbc.query(sql, rowMapper, estado, categoria);
        } else {
            String sql = BASE_SELECT + "WHERE c.estado = ? ORDER BY c.fecha_creacion DESC";
            return jdbc.query(sql, rowMapper, estado);
        }
    }
}
