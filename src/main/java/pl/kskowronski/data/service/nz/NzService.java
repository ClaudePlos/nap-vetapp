package pl.kskowronski.data.service.nz;

import com.vaadin.flow.component.notification.Notification;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class NzService {

    @PersistenceContext
    private EntityManager em;


    public String addHighwayFeeInvoiceToEgeria(Stream<String[]> items) {





        return "OK";
    }


    private int addNewDocumentHeader(String pSpolka, String pMiesiac, String nrFaktury, String pKontrahent) {
        Session session = em.unwrap( Session.class );
        Integer docId = null;
        try {
            docId = session.doReturningWork(
                    connection -> {
                        try (CallableStatement function = connection
                                .prepareCall(
                                        "{call ? := naprzod.nap_nz_tools.generuj_fzp_naglowek(?,?,?,?)}")) {
                            function.registerOutParameter(1, Types.INTEGER );
                            function.setString(2 , pSpolka );
                            function.setString(3 , pMiesiac );
                            function.setString(4 , nrFaktury);
                            function.setString(5 , pKontrahent );
                            function.execute();
                            return function.getInt(1);
                        }
                    });
        } catch (JDBCException ex){
            Notification.show(ex.getSQLException().getMessage(),5000, Notification.Position.MIDDLE);
        }
        return 0;
    }



}
