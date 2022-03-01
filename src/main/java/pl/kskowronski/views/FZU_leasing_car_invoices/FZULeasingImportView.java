package pl.kskowronski.views.FZU_leasing_car_invoices;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.util.SharedUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import pl.kskowronski.data.entity.global.EatFirma;
import pl.kskowronski.data.service.global.EatFirmaService;
import pl.kskowronski.data.service.nz.NzService;
import pl.kskowronski.views.MainLayout;
import pl.kskowronski.views.components.PeriodLayout;

import javax.annotation.security.PermitAll;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@PageTitle("FZU")
@Route(value = "fzu", layout = MainLayout.class)
@PermitAll
public class FZULeasingImportView extends VerticalLayout {

    private EatFirmaService eatFirmaService;
    private NzService nzService;

    private Grid<String[]> grid = new Grid<>();

    private TextField fieldNrDoc = new TextField("Nr dok. Egeria");
    private ComboBox<String> cmbClients = new ComboBox<>();

    public FZULeasingImportView(EatFirmaService eatFirmaService, NzService nzService) {
        this.eatFirmaService = eatFirmaService;
        this.nzService = nzService;
        setSpacing(false);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        var frmList  = getSelectFrm();
        var period = new PeriodLayout(0);
        var foreignInvoiceNumber = new TextField("");

        var buffer = new MemoryBuffer();
        var upload = new Upload(buffer);
        upload.addSucceededListener( e -> {
            try {
                readExcel(buffer.getInputStream());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InvalidFormatException invalidFormatException) {
                invalidFormatException.printStackTrace();
            }
        });

        cmbClients.setItems("ALD","TM_FLOTA","PCM","LeasePlan");


        var buttonInsertInvoiceToEgeria = new Button("Wgraj do Egerii");
        buttonInsertInvoiceToEgeria.addClickListener( e -> {
            var items = grid.getDataProvider();
//            int egeriaDokId = nzService.changePositionOnFzuInvoice(items, fieldNrDoc.getValue(), cmbClients.getValue() );
            int egeriaDokId;
            egeriaDokId = nzService.addLeaseFeeInvoiceToEgeria(items, frmList.getValue().getFrmNazwa(), period.getPeriod(), foreignInvoiceNumber.getValue(), cmbClients.getValue());
            Notification not = createSubmitSuccess("Dodano dokument w Egerii id: " + egeriaDokId);
            not.open();
        });


        add( new HorizontalLayout(new Label("Firma: ")
                        , frmList
                        , period
                        , new Label("Numer obcy:")
                        , foreignInvoiceNumber
                        , new Label("Klient: ")
                        , cmbClients
                )
                , grid
                , upload
                , buttonInsertInvoiceToEgeria
        );

//        add(new Label("Należy podać numer dokumentu z Egerii, wskazać firmę i wskazać dokument excel w celu podmiany pozycji")
//                , new HorizontalLayout(fieldNrDoc, cmbClients)
//                , grid
//                , upload
//                , buttonInsertInvoiceToEgeria
//        );

    }

    public void readExcel(InputStream resourceAsStream) throws IOException, InvalidFormatException {

        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(resourceAsStream);

        // Retrieving the number of sheets in the Workbook
        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

        /*
           =============================================================
           Iterating over all the sheets in the workbook (Multiple ways)
           =============================================================
        */

        // 1. You can obtain a sheetIterator and iterate over it
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        System.out.println("Retrieving Sheets using Iterator");
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            System.out.println("=> " + sheet.getSheetName());
        }

        // 2. Or you can use a for-each loop
        System.out.println("Retrieving Sheets using for-each loop");
        for(Sheet sheet: workbook) {
            System.out.println("=> " + sheet.getSheetName());
        }

        // 3. Or you can use a Java 8 forEach with lambda
        System.out.println("Retrieving Sheets using Java 8 forEach with lambda");
        workbook.forEach(sheet -> {
            System.out.println("=> " + sheet.getSheetName());
        });

        /*
           ==================================================================
           Iterating over all the rows and columns in a Sheet (Multiple ways)
           ==================================================================
        */

        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheetAt(0);

        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();

        // 2. Or you can use a for-each loop to iterate over the rows and columns
        System.out.println("\n\nIterating over Rows and Columns using for-each loop\n");

        List<String[]> data = new ArrayList<>();
        int numCell = 0;
        for (Row row: sheet) {
            if (row.getRowNum() == 0 ) {
                for(Cell cell: row) {
                    numCell = row.getPhysicalNumberOfCells();
                    String cellValue = dataFormatter.formatCellValue(cell);
                    grid.addColumn( r -> r[cell.getColumnIndex()]).setHeader(SharedUtil.camelCaseToHumanFriendly(cellValue));
                }
            } else {
                String [] item = new String[numCell];
                for(Cell cell: row) {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    item[cell.getColumnIndex()] = cellValue;
                }
                data.add(item);
            }

        }

        //String[] item = new String[] { i.substring(0,i.length()-1) };
        grid.setItems(data);

        // Closing the workbook
        workbook.close();
    }

// -----------------------------------------------------------------------

    private ComboBox<EatFirma> getSelectFrm() {
        ComboBox<EatFirma> comboFrm = new ComboBox<>();
        comboFrm.setItems(eatFirmaService.findAll());
        comboFrm.setItemLabelGenerator(EatFirma::getFrmNazwa);
        return comboFrm;
    }


    private Notification createSubmitSuccess( String text) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.MIDDLE);

        Icon icon = VaadinIcon.CHECK_CIRCLE.create();
        Div info = new Div(new Text(text));

        Button closeBtn = new Button(
                VaadinIcon.CLOSE_SMALL.create(),
                clickEvent -> notification.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);


        HorizontalLayout layout = new HorizontalLayout(
                icon, info, closeBtn);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);

        return notification;
    }





}
