package Tfast_Rmoney.Givvy.core;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OfferRowMapper implements RowMapper<Offer> {
    @Override
    public Offer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Offer offer = new Offer();
        offer.setOfferId(rs.getObject("offer_id", Integer.class));
        offer.setInterestId(rs.getObject("interest_id", Integer.class));
        offer.setRecipientId(rs.getString("recipient_id"));
        offer.setDonorId(rs.getString("donor_id"));
        offer.setStatus(rs.getObject("status", Integer.class));
        return offer;
    }
}