package pl.kskowronski.views.map;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.PermitAll;
import pl.kskowronski.components.leafletmap.LeafletMap;
import pl.kskowronski.views.MainLayout;

@PageTitle("Map")
@Route(value = "map", layout = MainLayout.class)
@PermitAll
public class MapView extends VerticalLayout {

    private LeafletMap map = new LeafletMap();

    public MapView() {
        setSizeFull();
        setPadding(false);
        map.setSizeFull();
        map.setView(55.0, 10.0, 4);
        add(map);
    }
}
