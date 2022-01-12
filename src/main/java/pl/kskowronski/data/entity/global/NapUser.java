package pl.kskowronski.data.entity.global;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "NAP_UZYTKOWNIK")
public class NapUser {

    @Id
    @Column(name = "PRC_ID")
    private Integer prcId;

    @Column(name = "UZ_NAZWA")
    private String username;

    @Column(name = "UZ_HASLO_ZAKODOWANE")
    private String password;

    public NapUser() {
    }

    public Integer getPrcId() {
        return prcId;
    }

    public void setPrcId(Integer prcId) {
        this.prcId = prcId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
