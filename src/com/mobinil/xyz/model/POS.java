package com.mobinil.xyz.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class POS implements Serializable {

    private String id;

    public POS() {
    }

    public POS(ResultSet rs) {
        try {
            this.id = rs.getString("ID");
        } catch (SQLException ex) {
            Logger.getLogger(POS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
