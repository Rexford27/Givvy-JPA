package Tfast_Rmoney.Givvy.core;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import Tfast_Rmoney.Givvy.interfaces.dtos.OfferResponse;

@Repository
public class OfferDAO {



    private JdbcTemplate jdbcTemplate;

    public OfferDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Get item_id from interest via offer_id
    public String getItemIdByOfferId(String offerId) {
        String sql = """
            SELECT z.item_id FROM offers o
            JOIN interest z ON o.interest_id = z.id
            WHERE o.offer_id = ?
        """;
        try {
            return jdbcTemplate.queryForObject(sql, String.class, offerId);
        } catch (Exception e) {
            return null;
        }
    }

    // Get donor_id from item via item_id
    public String getDonorIdByItemId(String itemId) {
        String sql = "SELECT donorId FROM items WHERE itemId = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, itemId);
        } catch (Exception e) {
            return null;
        }
    }

    // Get recipient_id by offer_id
    public String getRecipientIdByOfferId(String offerId) {
        String sql = "SELECT recipient_id FROM offers WHERE offer_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, offerId);
        } catch (Exception e) {
            return null;
        }
    }

    // Get interest_id by offer_id
    public Integer getInterestIdByOfferId(String offerId) {
        String sql = "SELECT interest_id FROM offers WHERE offer_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, offerId);
        } catch (Exception e) {
            return null;
        }
    }

    // Get accepted offer for specific item and recipient
    public Offer getAcceptedOfferForItemAndRecipient(String itemId, String recipientId) {
        String sql = """
            SELECT o.* FROM offers o
            JOIN interest z ON o.interest_id = z.id
            WHERE z.item_id = ? AND o.recipient_id = ? AND o.status = 1
            LIMIT 1
        """;
        RowMapper<Offer> rowMapper = new OfferRowMapper();
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, itemId, recipientId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Offer> getOffersForRecipient(String userId){
        String sql = "SELECT * FROM offers WHERE recipient_id = ?";
        RowMapper<Offer> rowMapper = new OfferRowMapper();
        List<Offer> offers = null;
        try {
            offers = jdbcTemplate.query(sql, rowMapper, userId);
        } catch (Exception e) {
            // Handle exception
            offers = new ArrayList<>();
        }
        return offers;
    }

    public List<Offer> getOffersForDonor(String userId){
        String sql = "SELECT * FROM offers WHERE donor_id = ?";
        RowMapper<Offer> rowMapper = new OfferRowMapper();
        List<Offer> offers = null;
        try {
            offers = jdbcTemplate.query(sql, rowMapper, userId);
        } catch (Exception e) {
            // Handle exception
            offers = new ArrayList<>();
        }
        return offers;
    }

    public int saveOffer(Offer offer) {
        String sql = "INSERT INTO offers (interest_id, recipient_id, donor_id, status) VALUES (?, ?, ?, ?)";
        try{
            jdbcTemplate.update(sql, offer.getInterestId(), offer.getRecipientId(), offer.getDonorId(), offer.getStatus());
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }


    public int deleteOffer(String offerId) {
        String sql = "DELETE FROM offers WHERE offer_id = ?";
        try{
            jdbcTemplate.update(sql, offerId);
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    public int updateOffer(OfferResponse offerRes) {
        if(offerRes.isAccepted()){
            // Get item_id from offer
            String itemId = getItemIdByOfferId(offerRes.getOfferId());
            
            // Update offer status to accepted (status = 1)
            String sql = "UPDATE offers SET status = 1 WHERE offer_id = ?";
            try{
                jdbcTemplate.update(sql, offerRes.getOfferId());
                
                // Also update item status to 'pending'
                if (itemId != null) {
                    String itemSql = "UPDATE items SET status = ? WHERE itemId = ?";
                    jdbcTemplate.update(itemSql, "pending", itemId);
                }
            } catch (Exception e) {
                return -1;
            }
        }
        else {
            String sql = "UPDATE offers SET status = 2 WHERE offer_id = ?";
            try{
                jdbcTemplate.update(sql, offerRes.getOfferId());
            } catch (Exception e) {
                return -1;
            }
        }
        return 0;
    }

    // Delete accepted offer for deselection
    public int deleteAcceptedOffer(String itemId, String recipientId) {
        String sql = """
            DELETE FROM offers WHERE offer_id IN (
                SELECT offer_id FROM (
                    SELECT o.offer_id FROM offers o
                    JOIN interest z ON o.interest_id = z.id
                    WHERE z.item_id = ? AND o.recipient_id = ? AND o.status = 1
                ) as temp
            )
        """;
        try {
            return jdbcTemplate.update(sql, itemId, recipientId);
        } catch (Exception e) {
            return -1;
        }
    }
}
