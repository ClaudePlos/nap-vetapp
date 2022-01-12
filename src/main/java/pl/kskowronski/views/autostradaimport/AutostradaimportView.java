package pl.kskowronski.views.autostradaimport;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.PermitAll;

import com.vaadin.flow.shared.util.SharedUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import pl.kskowronski.views.MainLayout;
import pl.kskowronski.views.components.PeriodLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@PageTitle("Autostrada import")
@Route(value = "higway_import", layout = MainLayout.class)
@PermitAll
public class AutostradaimportView extends VerticalLayout {

    Grid<String[]> grid = new Grid<>();

    public AutostradaimportView() {
        setSpacing(false);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        var period = new PeriodLayout(0);

        var buffer = new MemoryBuffer();
        var upload = new Upload(buffer);
        upload.addSucceededListener( e -> {
            //displayCsv(buffer.getInputStream());
            try {
                readExcel(buffer.getInputStream());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InvalidFormatException invalidFormatException) {
                invalidFormatException.printStackTrace();
            }
        });

        var buttonInsertInvoiceToEgeria = new Button("Wgraj do Egerii");
        buttonInsertInvoiceToEgeria.addClickListener( e -> {
                Stream<String[]> items = grid.getGenericDataView().getItems();
        });

        add(period, grid, upload, buttonInsertInvoiceToEgeria);
    }

    private void insertInvoiceToEgeria() {
        Stream<String[]> items = grid.getGenericDataView().getItems();
    }

    private void readFromClasspath(){
            displayCsv(getClass().getClassLoader().getResourceAsStream("contacts.csv"));
    }

    private void displayCsv(InputStream resourceAsStream) {
        var parser = new CSVParserBuilder().withSeparator(';').build();
        var reader = new CSVReaderBuilder(new InputStreamReader(resourceAsStream)).withCSVParser(parser);

        try {
            var entries = reader.build().readAll();

            var headers = entries.get(0);

            for ( int i = 0; i < headers.length; i++) {
                int colIndex = i;
                grid.addColumn( row -> row[colIndex])
                        .setHeader(SharedUtil.camelCaseToHumanFriendly(headers[colIndex]));
            }

            grid.setItems(entries.subList(1, entries.size()));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }


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

        // 1. You can obtain a rowIterator and columnIterator and iterate over them
//        System.out.println("\n\nIterating over Rows and Columns using Iterator\n");
//        Iterator<Row> rowIterator = sheet.rowIterator();
//        while (rowIterator.hasNext()) {
//            Row row = rowIterator.next();
//
//            // Now let's iterate over the columns of the current row
//            Iterator<Cell> cellIterator = row.cellIterator();
//
//
//            while (cellIterator.hasNext()) {
//                Cell cell = cellIterator.next();
//                String cellValue = dataFormatter.formatCellValue(cell);
//                System.out.print(cellValue + "\t");
//            }
//            System.out.println();
//        }



        // 2. Or you can use a for-each loop to iterate over the rows and columns
        System.out.println("\n\nIterating over Rows and Columns using for-each loop\n");

        List<String[]> data = new ArrayList<>();
        for (Row row: sheet) {

            if (row.getRowNum() == 0 ) {
                for(Cell cell: row) {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    grid.addColumn( r -> r[cell.getColumnIndex()]).setHeader(SharedUtil.camelCaseToHumanFriendly(cellValue));
                }
            } else {
                String [] item = new String[row.getPhysicalNumberOfCells()];
                for(Cell cell: row) {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    item[cell.getColumnIndex()] = cellValue;
                }
                data.add(item);
            }

        }

        //String[] item = new String[] { i.substring(0,i.length()-1) };
        grid.setItems(data);

        // 3. Or you can use Java 8 forEach loop with lambda
//        System.out.println("\n\nIterating over Rows and Columns using Java 8 forEach with lambda\n");
//        sheet.forEach(row -> {
//            row.forEach(cell -> {
//                String cellValue = dataFormatter.formatCellValue(cell);
//                System.out.print(cellValue + "\t");
//            });
//            System.out.println();
//        });

        // Closing the workbook
        workbook.close();
    }

}
