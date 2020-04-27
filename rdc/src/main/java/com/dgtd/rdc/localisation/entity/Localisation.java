package com.dgtd.rdc.localisation.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Localisation implements Serializable {
    private static final long serialVersionUID = 1L;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String niv1;
    private String niv2;
    private String niv3;
    private String niv4;
    private String niv5;
    private String niv6;
    private String niv7;
    private String niv8;
    private String filtre1;
    private String filtre2;
    private String filtre3;
    private String filtre4;
    private String filtre5;
    private short typeEntite;


    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "niv_1", nullable = true, length = 50)
    public String getNiv1() {
        return niv1;
    }

    public void setNiv1(String niv1) {
        this.niv1 = niv1;
    }

    @Basic
    @Column(name = "niv_2", nullable = true, length = 50)
    public String getNiv2() {
        return niv2;
    }

    public void setNiv2(String niv2) {
        this.niv2 = niv2;
    }

    @Basic
    @Column(name = "niv_3", nullable = true, length = 50)
    public String getNiv3() {
        return niv3;
    }

    public void setNiv3(String niv3) {
        this.niv3 = niv3;
    }

    @Basic
    @Column(name = "niv_4", nullable = true, length = 50)
    public String getNiv4() {
        return niv4;
    }

    public void setNiv4(String niv4) {
        this.niv4 = niv4;
    }

    @Basic
    @Column(name = "niv_5", nullable = true, length = 50)
    public String getNiv5() {
        return niv5;
    }

    public void setNiv5(String niv5) {
        this.niv5 = niv5;
    }

    @Basic
    @Column(name = "niv_6", nullable = true, length = 50)
    public String getNiv6() {
        return niv6;
    }

    public void setNiv6(String niv6) {
        this.niv6 = niv6;
    }

    @Basic
    @Column(name = "niv_7", nullable = true, length = 50)
    public String getNiv7() {
        return niv7;
    }

    public void setNiv7(String niv7) {
        this.niv7 = niv7;
    }

    @Basic
    @Column(name = "niv_8", nullable = true, length = 50)
    public String getNiv8() {
        return niv8;
    }

    public void setNiv8(String niv8) {
        this.niv8 = niv8;
    }

    @Basic
    @Column(name = "filtre_1", nullable = true, length = 50)
    public String getFiltre1() {
        return filtre1;
    }

    public void setFiltre1(String filtre1) {
        this.filtre1 = filtre1;
    }

    @Basic
    @Column(name = "filtre_2", nullable = true, length = 50)
    public String getFiltre2() {
        return filtre2;
    }

    public void setFiltre2(String filtre2) {
        this.filtre2 = filtre2;
    }

    @Basic
    @Column(name = "filtre_3", nullable = true, length = 50)
    public String getFiltre3() {
        return filtre3;
    }

    public void setFiltre3(String filtre3) {
        this.filtre3 = filtre3;
    }

    @Basic
    @Column(name = "filtre_4", nullable = true, length = 50)
    public String getFiltre4() {
        return filtre4;
    }

    public void setFiltre4(String filtre4) {
        this.filtre4 = filtre4;
    }

    @Basic
    @Column(name = "filtre_5", nullable = true, length = 50)
    public String getFiltre5() {
        return filtre5;
    }

    public void setFiltre5(String filtre5) {
        this.filtre5 = filtre5;
    }

    @Basic
    @Column(name = "type_entite", nullable = false)
    public short getTypeEntite() {
        return typeEntite;
    }

    public void setTypeEntite(short typeEntite) {
        this.typeEntite = typeEntite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Localisation that = (Localisation) o;
        return id == that.id &&
                typeEntite == that.typeEntite &&
                Objects.equals(niv1, that.niv1) &&
                Objects.equals(niv2, that.niv2) &&
                Objects.equals(niv3, that.niv3) &&
                Objects.equals(niv4, that.niv4) &&
                Objects.equals(niv5, that.niv5) &&
                Objects.equals(niv6, that.niv6) &&
                Objects.equals(niv7, that.niv7) &&
                Objects.equals(niv8, that.niv8) &&
                Objects.equals(filtre1, that.filtre1) &&
                Objects.equals(filtre2, that.filtre2) &&
                Objects.equals(filtre3, that.filtre3) &&
                Objects.equals(filtre4, that.filtre4) &&
                Objects.equals(filtre5, that.filtre5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, niv1, niv2, niv3, niv4, niv5, niv6, niv7, niv8, filtre1, filtre2, filtre3, filtre4, filtre5, typeEntite);
    }


}
