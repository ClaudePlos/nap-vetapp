package pl.kskowronski.data.service.nz;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class NzService {

    @PersistenceContext
    private EntityManager em;


    public int addHighwayFeeInvoiceToEgeria(DataProvider<String[],?> items, String frmName, String period, String foreignInvoiceNumber) {

        int dokId = addNewDocumentHeader( frmName, period, foreignInvoiceNumber, "AUTOPAY MOBILITY SP. Z O.O.");
        addPositionsToEgeriaInvoice(dokId, frmName, items);

        return dokId;
    }


    private int addNewDocumentHeader(String frmName, String period,  String foreignInvoiceNumber, String pInvoiceCompanyName) {
        Session session = em.unwrap( Session.class );
        Integer docId = null;
        try {
            docId = session.doReturningWork(
                    connection -> {
                        try (CallableStatement function = connection
                                .prepareCall(
                                        "{call ? := naprzod.nap_nz_tools.generuj_fzp_naglowek(?,?,?,?)}")) {
                            function.registerOutParameter(1, Types.INTEGER );
                            function.setString(2 , frmName );
                            function.setString(3 , period );
                            function.setString(4 , foreignInvoiceNumber);
                            function.setString(5 , pInvoiceCompanyName );
                            function.execute();
                            return function.getInt(1);
                        }
                    });
        } catch (JDBCException ex){
            Notification.show(ex.getSQLException().getMessage(),5000, Notification.Position.MIDDLE);
        }
        return docId;
    }

    private String addPositionsToEgeriaInvoice(int docId, String frmName,  DataProvider<String[],?> items) {

        var rows = ((ListDataProvider) items).getItems();
        rows.stream().forEach( row -> {
            //var no = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(0);
            //var date = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(3);
            var serviceNumber = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(4);
            var vehicle = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(5);

            var grossAmount = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(6);
            var netAmount = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(7);
            var vatAmount = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(8);

            var sk = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(11);

            String response = addPositionToEgeriaInvoice(docId, frmName, serviceNumber, grossAmount, netAmount, vatAmount, vehicle, sk);
            //System.out.println(docId + frmName + serviceNumber + grossAmount + netAmount + vatAmount + vehicle + sk);
        });

        return "OK";
    }


    private String addPositionToEgeriaInvoice(int egeriaDokId, String frmName, String serviceNumber
            , String grossAmount, String netAmount, String vatAmount, String vehicle, String sk) {
        Session session = em.unwrap( Session.class );
        String response = null;
        try {
            response = session.doReturningWork(
                    connection -> {
                        try (CallableStatement function = connection
                                .prepareCall(
                                        "{call ? := naprzod.nap_nz_tools.generuj_fzp_pozycje(?,?,?,?,?,?,?,?,?,?,?,?,?)}")) {
                            function.registerOutParameter(1, Types.VARCHAR );
                            function.setString(2 , frmName );
                            function.setInt(3 , egeriaDokId );
                            function.setString(4 , serviceNumber); //numer karty
                            function.setString(5 , "PRZEJAZD AUTOSTRA");  //nazwa produktu
                            function.setString(6 , "1" ); // ilosc
                            function.setString(7 , netAmount ); // cena_jednostokowa
                            function.setString(8 , netAmount ); // wartosc netto
                            function.setString(9 , vatAmount.equals("0") ? "0" : "23" ); // stawka vat
                            function.setString(10 , vatAmount ); // wartosc vat
                            function.setString(11 , grossAmount ); // wart brutto
                            function.setString(12 , vehicle ); // dod info nr rej.
                            function.setString(13 , sk ); // centrum kosztow
                            function.setString(14 , "0" ); // stan licznika
                            function.execute();
                            return function.getString(1);
                        }
                    });
        } catch (JDBCException ex){
            Notification.show(ex.getSQLException().getMessage(),5000, Notification.Position.MIDDLE);
        }
        return response;
    }





    // Obsługa dokumentu FZU

    public int addLeaseFeeInvoiceToEgeria(DataProvider<String[], ?> items, String frmName, String period, String foreignInvoiceNumber, String clientKod) {
        int dokId = 0;
//        int dokId = addNewFZUDocumentHeader( frmName, period, foreignInvoiceNumber, clientKod);
        addPositionsToFZUEgeriaInvoice(dokId, frmName, items);

        return dokId;
    }


    private int addNewFZUDocumentHeader(String frmName, String period,  String foreignInvoiceNumber, String pInvoiceCompanyName) {
        Session session = em.unwrap( Session.class );
        Integer docId = null;
        try {
            docId = session.doReturningWork(
                    connection -> {
                        try (CallableStatement function = connection
                                .prepareCall(
                                        "{call ? := naprzod.nap_nz_tools.generuj_fzu_naglowek(?,?,?,?)}")) {
                            function.registerOutParameter(1, Types.INTEGER );
                            function.setString(2 , frmName );
                            function.setString(3 , period );
                            function.setString(4 , foreignInvoiceNumber);
                            function.setString(5 , pInvoiceCompanyName );
                            function.execute();
                            return function.getInt(1);
                        }
                    });
        } catch (JDBCException ex){
            Notification.show(ex.getSQLException().getMessage(),5000, Notification.Position.MIDDLE);
        }
        return docId;
    }


    private String addPositionsToFZUEgeriaInvoice(int docId, String frmName,  DataProvider<String[],?> items) {

        var rows = ((ListDataProvider) items).getItems();
        rows.stream().forEach( row -> {
            var docNumber =Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(2);
            //var date = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(3);
            var productGroup = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(13);  // grupa produktow
            var vehicleNumber = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(9);  // nr_rejestracyjny
            var description = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(12);

            var grossAmount = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(21);
            var netAmount = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(19);
            var vatAmount = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(20);

            var client = Arrays.stream(((String[]) row)).collect(Collectors.toList()).get(1);

            String response = addPositionToFZUEgeriaInvoice(docId, frmName, productGroup, grossAmount, netAmount, vatAmount, vehicleNumber, description );
            System.out.println(docId + ";" + frmName + ";" + productGroup + ";" + grossAmount + ";" + netAmount + ";" + vatAmount + ";" + vehicleNumber + ";" + docNumber + ";" + client + ";" + description);
        });

        return "OK";
    }


    private String addPositionToFZUEgeriaInvoice(int egeriaDokId, String frmName, String productGroup
            , String grossAmount, String netAmount, String vatAmount, String vehicle, String description) {
        Session session = em.unwrap( Session.class );
        String response = null;
        try {
            response = session.doReturningWork(
                    connection -> {
                        try (CallableStatement function = connection
                                .prepareCall(
                                        "{call ? := naprzod.nap_nz_tools.generuj_fzu_pozycje(?,?,?,?,?,?,?,?,?,?,?,?,?)}")) {
                            function.registerOutParameter(1, Types.VARCHAR );
                            function.setString(2 , frmName );
                            function.setInt(3 , egeriaDokId );
                            function.setString(4 , productGroup);
                            function.setString(5 , description);  //nazwa produktu
                            function.setString(6 , "1" ); // ilosc
                            function.setString(7 , netAmount ); // cena_jednostokowa
                            function.setString(8 , netAmount ); // wartosc netto
                            function.setString(9 , vatAmount.equals("0") ? "0" : "23" ); // stawka vat
                            function.setString(10 , vatAmount ); // wartosc vat
                            function.setString(11 , grossAmount ); // wart brutto
                            function.setString(12 , vehicle ); // dod info nr rej.
                            function.setString(13 , "-" ); // centrum kosztow
                            function.setString(14 , "0" ); // stan licznika
                            function.execute();
                            return function.getString(1);
                        }
                    });
        } catch (JDBCException ex){
            Notification.show(ex.getSQLException().getMessage(),5000, Notification.Position.MIDDLE);
        }
        return response;
    }


}
