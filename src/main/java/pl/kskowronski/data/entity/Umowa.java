package pl.kskowronski.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "KNT_UMOWY")
public class Umowa {

    @Id
    @Column(name = "UM_ID")
    Long umId;

    @Column(name = "UM_NUMER")
    String umNumer;

    public Umowa() {
    }

    public Long getUmId() {
        return umId;
    }

    public String getUmNumer() {
        return umNumer;
    }
}
