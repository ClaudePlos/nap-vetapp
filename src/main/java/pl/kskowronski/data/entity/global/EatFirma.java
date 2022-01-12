package pl.kskowronski.data.entity.global;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EAT_FIRMY")
public class EatFirma {

    @Id
    @Column(name = "FRM_ID")
    private Integer frmId;

    @Column(name = "FRM_NAZWA")
    private String frmNazwa;

    @Column(name = "FRM_KL_ID")
    private Integer frmKlId;

    public EatFirma() {
    }

    public Integer getFrmId() {
        return frmId;
    }

    public void setFrmId(Integer frmId) {
        this.frmId = frmId;
    }

    public String getFrmNazwa() {
        return frmNazwa;
    }

    public void setFrmNazwa(String frmNazwa) {
        this.frmNazwa = frmNazwa;
    }

    public Integer getFrmKlId() {
        return frmKlId;
    }

    public void setFrmKlId(Integer frmKlId) {
        this.frmKlId = frmKlId;
    }
}
