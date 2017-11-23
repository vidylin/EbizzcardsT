package com.gzligo.ebizzcardstranslator.persistence;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZuoJian on 2017/8/3.
 */

public class IdentityBean extends BaseBean{

    private String username;
    private String id_number;
    private int education;
    private String profession;
    private List<String> id_cards;
    private List<String> degree_cers;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public int getEducation() {
        return education;
    }

    public void setEducation(int education) {
        this.education = education;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public List<String> getId_cards() {
        return id_cards;
    }

    public void setId_cards(List<String> id_cards) {
        this.id_cards = id_cards;
    }

    public List<String> getDegree_cers() {
        return degree_cers;
    }

    public void setDegree_cers(List<String> degree_cers) {
        this.degree_cers = degree_cers;
    }
}
