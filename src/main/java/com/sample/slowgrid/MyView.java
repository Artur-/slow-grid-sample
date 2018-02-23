
package com.sample.slowgrid;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Version;
import com.vaadin.flow.server.WebBrowser;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;


@Route("")
@Theme(Lumo.class)
public class MyView extends Div {

    // define basic grid details here:
    private static int gridNumCols = 20;
    private static long gridNumHidCols = 15;
    private static long gridRows = 1000;

    @WebFilter(filterName = "LatencyFilter", servletNames = { "MyUIServlet" })
    public static class LatencyFilter implements Filter {

        private static long currentTime;
        private static long numberOfVisibleColls;
        private static long numberOfHiddenColls;
        private static long numberOfRows;
        private static boolean fixedWidth;
        private static boolean complexHeader;
        private static String webBrowser;

        public static void setGridConfiguration(long currentTime, boolean fixedWidth, boolean complexHeader, int numberOfVisibleColls,
                int numberOfHiddenColls, int numberOfRows, String wb) {
            LatencyFilter.currentTime = currentTime;
            LatencyFilter.numberOfVisibleColls = numberOfVisibleColls;
            LatencyFilter.numberOfHiddenColls = numberOfHiddenColls;
            LatencyFilter.numberOfRows = numberOfRows;
            LatencyFilter.webBrowser = wb;
            LatencyFilter.fixedWidth = fixedWidth;
            LatencyFilter.complexHeader = complexHeader;
        }

        @Override
        public void init(FilterConfig fc) throws ServletException {}

        @Override
        public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
            if (currentTime > 0) {
                long timeTakenToRender = ((System.nanoTime() - currentTime) / 1000000);
                String out = MessageFormat.format("{0}|{1}|{2}|({3,number,#}, {4,number,#}, {5,number,#})|{6,number,#}",
                        webBrowser, fixedWidth, complexHeader, numberOfVisibleColls, numberOfHiddenColls, numberOfRows, timeTakenToRender);
                if (timeTakenToRender > 1000) {
                    out = out + "\n !!! Rendering took more than 1 second! !!!";
                }
                currentTime = -1;
                System.out.println(out);
            }
            fc.doFilter(sr, sr1);
        }

        @Override
        public void destroy() {}

    }

    private static final long serialVersionUID = 1L;

    private static final String vaadinVersion = Version.getFullVersion();

    private TextField tfColumns;
    private TextField tfHiddenColumns;
    private TextField tfRows;
    private Checkbox cbFixedWidth;
    private Checkbox cbComplexHeader;

    private Div gridwrapper;
    private Grid<GridEntry> grid;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        final VerticalLayout vertLayout = new VerticalLayout();
        vertLayout.setSizeFull();

        // Adding Vaadin version label
        Label versionLabel = new Label("Grid Test with Vaadin Version: " + vaadinVersion);
        versionLabel.setId("vaadinVersionLabel");
        vertLayout.add(versionLabel);

        Label descriptionLabel = new Label(
                "Default setup uses the slowest combination of variable width columns and complex headers aka one row of filter columns with TextField, DateField or ComboBox.");
        descriptionLabel.setId("vaadinVersionLabel");
        vertLayout.add(descriptionLabel);

        tfColumns = new TextField();
        tfColumns.setLabel("column count");
        tfColumns.setId("columncount");
        tfColumns.setValue(String.valueOf(gridNumCols));
        tfHiddenColumns = new TextField();
        tfHiddenColumns.setLabel("hidden column count");
        tfHiddenColumns.setId("hiddencolumncount");
        tfHiddenColumns.setValue(String.valueOf(gridNumHidCols));
        tfRows = new TextField();
        tfRows.setLabel("row count");
        tfRows.setId("rowcount");
        tfRows.setValue(String.valueOf(gridRows));
        cbFixedWidth = new Checkbox();
        cbFixedWidth.setId("cbfixedwidth");
        cbFixedWidth.setLabel("fixed column widths");
        cbComplexHeader = new Checkbox();
        cbComplexHeader.setId("cbcomplexheader");
        cbComplexHeader.setLabel("complex header");
        // turn complex headers with filters on by default
        cbComplexHeader.setValue(true);

        VerticalLayout vertLayoutInner = new VerticalLayout();
        vertLayoutInner.add(cbFixedWidth);
        vertLayoutInner.add(cbComplexHeader);

        HorizontalLayout horLayout = new HorizontalLayout();
        vertLayout.add(horLayout);

        horLayout.add(tfColumns);
        horLayout.add(tfHiddenColumns);
        horLayout.add(tfRows);
        horLayout.add(vertLayoutInner);
        //        horLayout.setComponentAlignment(vertLayoutInner, Alignment.TOP_LEFT);

        // Create new Grid with gridEntries
        final Button hideShowButton = new Button("hide Grid");
        hideShowButton.setId("gridButton");
        hideShowButton.addClickListener(event -> {
            if (grid.isVisible()) {
                grid.setVisible(false);
                hideShowButton.setText("build Grid");
            } else {
                try {
                    //System.out.println("Time since build grid done:" + (System.nanoTime() - currentTime));
                    System.out.println("Start render");
                    WebBrowser wb = getUI().get().getSession().getBrowser();
                    LatencyFilter.setGridConfiguration(System.nanoTime(), cbFixedWidth.getValue(), cbComplexHeader.getValue(), Integer
                            .parseInt(tfColumns.getValue()), Integer.parseInt(tfHiddenColumns.getValue()), Integer.parseInt(tfRows
                                    .getValue()), (wb.isIE() ? "IE" : (wb.isFirefox() ? "FF" : "CH")));
                    buildGrid();

                } catch (NumberFormatException e) {
                    Notification.show("only numbers allowed for column counts", 1000, Position.MIDDLE);
                    return;
                }
                grid.setVisible(true);

                hideShowButton.setText("hide Grid");
                //                hideShowButton.setTitle("Click hide/show Grid button to render the grid and measure times");
            }
        });

        horLayout.add(hideShowButton);
        //        horLayout.setComponentAlignment(hideShowButton, Alignment.TOP_LEFT);

        gridwrapper = new Div();
        gridwrapper.setSizeFull();

        buildGrid();

        vertLayout.add(gridwrapper);
        vertLayout.setFlexGrow(1, gridwrapper);
        vertLayout.setMargin(true);
        vertLayout.setSpacing(true);

        add(vertLayout);

        getUI().get().getPage().setTitle("Vaadin Grid Performance Test");
    }

    private Component createHeaderComponent(int i) {
        Component searchField = null;
        if (i % 3 == 1) {
            searchField = new DatePicker();
            //            searchField.setHeight("28px");
            //            searchField.setWidth("100%");
        } else if (i % 3 == 2) {
            searchField = new ComboBox<String>("", Arrays.asList("val1", "val2", "val3", "val4"));
            //            searchField.setHeight("28px");
            //            searchField.setWidth("100%");
        } else {
            searchField = new TextField();
            //            searchField.setHeight("28px");
            //            searchField.setWidth("100%");
        }
        return searchField;
    }

    private void buildGrid() {
        boolean fixedWidth = cbFixedWidth.getValue();
        boolean complexHeader = cbComplexHeader.getValue();
        String tfValue = tfColumns.getValue();
        String tfHiddenValue = tfHiddenColumns.getValue();
        String tfrowValue = tfRows.getValue();

        int columns = 0;
        int hiddenColumns = 0;
        int rows = 0;

        columns = Integer.parseInt(tfValue);
        hiddenColumns = Integer.parseInt(tfHiddenValue);
        rows = Integer.parseInt(tfrowValue);

        Set<GridEntry> gridEntrySet = new LinkedHashSet<>();
        Random random = new Random();

        for (int i = 1; i < rows + 1; i++) {
            gridEntrySet.add(new GridEntry("Content Longer #" + i + ":" + random.nextInt()));
        }

        Collection<GridEntry> gridEntries = gridEntrySet;
        grid = new Grid<>(GridEntry.class);
        grid.setItems(gridEntries);

        //        HeaderRow headerRow = null;

        //        if (complexHeader) {
        //            headerRow = grid.addHeaderRowAt(1);
        //        }

        for (int i = 0; i < columns; i++) {
            Column<GridEntry> col = grid.addColumn(GridEntry::getContent);
            //            col.set
            col.setHeader("Col#" + i);
            //            if (complexHeader) {
            //                assert headerRow != null : "HeaderRow NullCheck";
            //                headerRow.getCell(col).setComponent(createHeaderComponent(i));
            //            }

            if (i != 0 && fixedWidth) {
                col.setWidth(getRandomColumnWidth());
            }
        }

        for (int i = 0; i < hiddenColumns; i++) {
            Column<GridEntry> col = grid.addColumn(GridEntry::getContent).setHeader("hidden #" + i);
            //            col.setHidable(true)
            col.setVisible(false);
            col.setHeader("hCol#" + i);
            //            if (complexHeader) {
            //                assert headerRow != null : "HeaderRow NullCheck";
            //                headerRow.getCell(col).setComponent(createHeaderComponent(i));
            //            }
        }

        grid.setColumnReorderingAllowed(true);
        grid.getColumns().get(0).setFrozen(true);
        grid.setId("testGrid");

        grid.setSizeFull();

        // Activate multi selection mode
        grid.setSelectionMode(SelectionMode.MULTI);
        //        grid.addColumnVisibilityChangeListener(new ColumnVisibilityChangeListener() {
        //
        //            private static final long serialVersionUID = 1L;
        //
        //            public void columnVisibilityChanged(ColumnVisibilityChangeEvent event) {
        //                System.out.println("Visibility change");
        //
        //            }
        //        });
        //        grid.addItemClickListener(new ItemClickListener<GridEntry>() {
        //
        //            private static final long serialVersionUID = 1L;
        //
        //            public void itemClick(ItemClick<GridEntry> event) {
        //                System.out.println("Clicked!!");
        //
        //            }
        //
        //        });


        gridwrapper.add(grid);
    }

    //test runs must be equal
    private Random rr = new Random(7);

    private String getRandomColumnWidth() {
        return (80.0 + rr.nextDouble() * 220) + "px";
    }
}
