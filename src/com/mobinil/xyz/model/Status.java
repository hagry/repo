package com.mobinil.xyz.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Status implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String arabicDescription;

    public Status() {
    }

    public Status(ResultSet rs) {
        try {
            this.id = rs.getLong("ID");
            this.name = rs.getString("ARABICDESCRIPTION");
            this.description = rs.getString("DESCRIPTION");
            this.arabicDescription = rs.getString("NAME");
        } catch (SQLException ex) {
            Logger.getLogger(Status.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setArabicDescription(String arabicDescription) {
        this.arabicDescription = arabicDescription;
    }

    public String getArabicDescription() {
        return this.arabicDescription;
    }
}
