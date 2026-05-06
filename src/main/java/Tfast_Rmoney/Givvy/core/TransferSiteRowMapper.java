package Tfast_Rmoney.Givvy.core;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransferSiteRowMapper implements RowMapper<TransferSite> {
    @Override
    public TransferSite mapRow(ResultSet rs, int rowNum) throws SQLException {
        TransferSite site = new TransferSite();
        site.setId(rs.getInt("id"));
        site.setName(rs.getString("name"));
        site.setAddressOne(rs.getString("address_one"));
        site.setAddressTwo(rs.getString("address_two"));
        site.setCity(rs.getString("city"));
        site.setState(rs.getString("state"));
        site.setZip(rs.getString("zip"));
        site.setImageUrl(rs.getString("image_url"));
        site.setDescription(rs.getString("description"));
        return site;
    }
}