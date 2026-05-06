package Tfast_Rmoney.Givvy.core;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemRowMapper implements RowMapper<Item> {

    @Override
    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {

        Item item = new Item();

        item.setItemId(rs.getString("itemId"));
        item.setDonorId(rs.getString("donorId"));
        item.setTitle(rs.getString("title"));
        item.setDescription(rs.getString("description"));
        item.setImageUrl(rs.getString("imageUrl"));
        item.setStatus(rs.getString("status"));

        // safer for nulls
        if (rs.getTimestamp("postedAt") != null) {
            item.setPostedAt(rs.getTimestamp("postedAt").toLocalDateTime());
        }

        return item;
    }
}

/*
use this sql code to replace the old item table 

DROP TABLE IF EXISTS items;

CREATE TABLE items (
    itemId VARCHAR(100) PRIMARY KEY,
    donorId VARCHAR(100) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    imageUrl VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'available',
    postedAt DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (donorId) REFERENCES users(userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


*/