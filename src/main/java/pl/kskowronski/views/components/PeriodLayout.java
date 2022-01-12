package pl.kskowronski.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import pl.kskowronski.MapperDate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class PeriodLayout extends HorizontalLayout {

    private transient MapperDate mapperDate = new MapperDate();

    private Button butPlus = new Button("+");
    private Button butMinus = new Button("-");
    private TextField textPeriod = new TextField();

    public PeriodLayout( Integer minusMonths) {
        setId("period-layout");
        Date now =  Date.from(LocalDate.now().minus(minusMonths, ChronoUnit.MONTHS).atStartOfDay(ZoneId.systemDefault()).toInstant());
        textPeriod.setClassName("textPeriod");
        textPeriod.setValue(mapperDate.dtYYYYMM.format(now));
        textPeriod.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

        butPlus.setClassName("butPlus");
        butPlus.addClickListener(event ->{
            Long mc = Long.parseLong(textPeriod.getValue().substring(textPeriod.getValue().length()-2));
            if ( mc < 12 ){
                mc++; String mcS = "0" + mc;
                textPeriod.setValue(textPeriod.getValue().substring(0,5) + mcS.substring(mcS.length()-2) );
            } else {
                Long year = Long.parseLong(textPeriod.getValue().substring(0,4));
                year++;
                textPeriod.setValue(year + "-01" );
            }
        });
        butMinus.setClassName("butMinus");
        butMinus.addClickListener(event ->{
            Long mc = Long.parseLong(textPeriod.getValue().substring(textPeriod.getValue().length()-2));
            if ( mc > 1 ){
                mc--; String mcS = "0" + mc;
                textPeriod.setValue(textPeriod.getValue().substring(0,5) + mcS.substring(mcS.length()-2) );
            } else {
                Long year = Long.parseLong(textPeriod.getValue().substring(0,4));
                year--;
                textPeriod.setValue(year + "-12" );
            }
        });
        add(new Label("Okres: "),butMinus, textPeriod, butPlus);

    }

    public String getPeriod(){
        return textPeriod.getValue();
    }

    public LocalDate getFirstDayOfPeriod(){
        return LocalDate.parse(textPeriod.getValue()+"-01", mapperDate.localDateYYYYMMDD);
    }

    public LocalDate getLastDayOfPeriod(){
        LocalDate date = LocalDate.parse(textPeriod.getValue()+"-01", mapperDate.localDateYYYYMMDD);
        return date.withDayOfMonth(date.lengthOfMonth());
    }
}
