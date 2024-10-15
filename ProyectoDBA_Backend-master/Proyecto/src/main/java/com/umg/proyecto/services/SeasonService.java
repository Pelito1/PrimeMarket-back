package com.umg.proyecto.services;

import com.umg.proyecto.models.Season;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Obtener todas las temporadas activas con fecha válida
    public List<Season> findActiveSeasons() {
        String sql = "SELECT * FROM SEASON WHERE STATUS = '1' AND END_DATE >= SYSDATE";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Season season = new Season();
            season.setId(rs.getInt("ID"));
            season.setName(rs.getString("NAME"));
            season.setDescription(rs.getString("DESCRIPTION"));
            season.setStartDate(rs.getDate("START_DATE"));
            season.setEndDate(rs.getDate("END_DATE"));
            season.setImage(rs.getString("IMAGE"));
            season.setStatus(rs.getString("STATUS").charAt(0));
            return season;
        });
    }

    // Actualizar estado de una temporada
    public void updateStatus(Integer id, char status) {
        String sql = "UPDATE SEASON SET STATUS = ? WHERE ID = ?";
        jdbcTemplate.update(sql, String.valueOf(status), id);
    }

    public void save(Season season) {
        String sql = "INSERT INTO SEASON (ID, NAME, START_DATE, END_DATE, DESCRIPTION, STATUS, IMAGE) " +
                "VALUES (SEASON_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, season.getName(), season.getStartDate(), season.getEndDate(),
                season.getDescription(), String.valueOf(season.getStatus()), season.getImage());
    }
    public void update(Season season) {
        String sql = "UPDATE SEASON SET NAME = ?, START_DATE = ?, END_DATE = ?, DESCRIPTION = ?, " +
                "STATUS = ?, IMAGE = ? WHERE ID = ?";
        jdbcTemplate.update(sql, season.getName(), season.getStartDate(), season.getEndDate(),
                season.getDescription(), String.valueOf(season.getStatus()), season.getImage(), season.getId());
    }
    public void delete(Integer id) {
        String sql = "DELETE FROM SEASON WHERE ID = ?";
        jdbcTemplate.update(sql, id);
    }
    public List<Season> findAllSeasons() {
        String sql = "SELECT * FROM SEASON";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Season season = new Season();
            season.setId(rs.getInt("ID"));
            season.setName(rs.getString("NAME"));
            season.setDescription(rs.getString("DESCRIPTION"));
            season.setStartDate(rs.getDate("START_DATE"));
            season.setEndDate(rs.getDate("END_DATE"));
            season.setImage(rs.getString("IMAGE"));
            season.setStatus(rs.getString("STATUS").charAt(0));
            return season;
        });
    }

    public Season findById(Integer id) {
        String sql = "SELECT * FROM SEASON WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
            Season season = new Season();
            season.setId(rs.getInt("ID"));
            season.setName(rs.getString("NAME"));
            season.setDescription(rs.getString("DESCRIPTION"));
            season.setStartDate(rs.getDate("START_DATE"));
            season.setEndDate(rs.getDate("END_DATE"));
            season.setImage(rs.getString("IMAGE"));
            season.setStatus(rs.getString("STATUS").charAt(0));
            return season;
        });
    }


}
