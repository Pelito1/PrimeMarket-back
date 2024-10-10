package com.umg.proyecto.services;

import com.umg.proyecto.models.Season;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class SeasonService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Mapeador de filas para convertir las filas de la base de datos en objetos Season
    private final RowMapper<Season> seasonRowMapper = new RowMapper<Season>() {
        @Override
        public Season mapRow(ResultSet rs, int rowNum) throws SQLException {
            Season season = new Season();
            season.setId(rs.getInt("ID"));
            season.setName(rs.getString("NAME"));
            season.setStartDate(rs.getDate("START_DATE"));
            season.setEndDate(rs.getDate("END_DATE"));
            season.setDescription(rs.getString("DESCRIPTION"));
            season.setStatus(rs.getString("STATUS").charAt(0));
            return season;
        }
    };

    // Método para obtener todas las temporadas
    public List<Season> findAll() {
        String sql = "SELECT * FROM SEASON";
        return jdbcTemplate.query(sql, seasonRowMapper);
    }

    // Método para obtener una temporada por ID
    public Season findById(Integer id) {
        String sql = "SELECT * FROM SEASON WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, seasonRowMapper);
    }

    // Método para guardar una nueva temporada
    public void save(Season season) {
        String sql = "INSERT INTO SEASON (ID, NAME, START_DATE, END_DATE, DESCRIPTION, STATUS) " +
                "VALUES (SEASON_SEQ.NEXTVAL, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, season.getName(), season.getStartDate(), season.getEndDate(), season.getDescription(), String.valueOf(season.getStatus()));
    }

    // Método para actualizar una temporada existente
    public void update(Season season) {
        String sql = "UPDATE SEASON SET NAME = ?, START_DATE = ?, END_DATE = ?, DESCRIPTION = ?, STATUS = ? WHERE ID = ?";
        jdbcTemplate.update(sql, season.getName(), season.getStartDate(), season.getEndDate(), season.getDescription(), String.valueOf(season.getStatus()), season.getId());
    }

    // Método para eliminar una temporada por ID
    public void delete(Integer id) {
        String sql = "DELETE FROM SEASON WHERE ID = ?";
        jdbcTemplate.update(sql, id);
    }
}
