package com.company.jersey01.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Jeff Risberg
 * @since 10/26/17
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "charities")
public class Charity extends AbstractDatedEntity {
    @Column(name = "name")
    protected String name;

    @Column(name = "ein")
    protected String ein;

    @Column(name = "website")
    protected String website;

    /**
     * Default constructor
     */
    public Charity() {
    }

    /**
     * Constructor
     */
    public Charity(Integer id, String name, String ein, String website) {
        this.id = id;
        this.name = name;
        this.ein = ein;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEin() {
        return ein;
    }

    public void setEin(String ein) {
        this.ein = ein;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String toString() {
        return "Charity[" + name + "/" + ein + "/" + website + "]";
    }
}
