package Tfast_Rmoney.Givvy.core;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TransferSiteDAO {

    private JdbcTemplate jdbcTemplate;

    public TransferSiteDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Saves a new transfer site.
    // MySQL creates the id automatically because id is AUTO_INCREMENT.
    public int save(TransferSite site) {
        String sql = """
            INSERT INTO transfer_sites
            (name, address_one, address_two, city, state, zip, image_url, description)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        return jdbcTemplate.update(
            sql,
            site.getName(),
            site.getAddressOne(),
            site.getAddressTwo(),
            site.getCity(),
            site.getState(),
            site.getZip(),
            site.getImageUrl(),
            site.getDescription()
        );
    }

    // Returns all safe transfer sites.
    // Your friend's appointment scheduling can use this to let users pick a location.
    public List<TransferSite> findAll() {
        String sql = "SELECT * FROM transfer_sites";
        RowMapper<TransferSite> rowMapper = new TransferSiteRowMapper();

        return jdbcTemplate.query(sql, rowMapper);
    }

    // Returns one transfer site by id.
    // Appointment scheduling can store this id as location_id.
    public TransferSite findById(int id) {
        String sql = "SELECT * FROM transfer_sites WHERE id = ?";
        RowMapper<TransferSite> rowMapper = new TransferSiteRowMapper();

        TransferSite result = null;

        try {
            result = jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (Exception ex) {
            // If no site is found, return null.
        }

        return result;
    }

    // Optional, but useful if an admin needs to remove a bad location.
    public int delete(int id) {
        String sql = "DELETE FROM transfer_sites WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}