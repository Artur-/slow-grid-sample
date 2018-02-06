package com.sample.slowgrid;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinPortlet;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Random;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    @WebFilter(filterName = "LatencyFilter", servletNames = {"MyUIServlet"})
    public static class LatencyFilter implements Filter {

        private static long currentTime;
        private static long numberOfVisibleColls;
        private static long numberOfHiddenColls;
        private static long numberOfRows;
        private static String webBrowser;

        public static void setGridConfiguration(long currentTime, int numberOfVisibleColls, int numberOfHiddenColls, int numberOfRows, String wb) {
            LatencyFilter.currentTime = currentTime;
            LatencyFilter.numberOfVisibleColls = numberOfVisibleColls;
            LatencyFilter.numberOfHiddenColls = numberOfHiddenColls;
            LatencyFilter.numberOfRows = numberOfRows;
            LatencyFilter.webBrowser = wb;
        }

        @Override
        public void init(FilterConfig fc) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
            HttpServletRequest hsr = (HttpServletRequest) sr;
            //hsr.getRequestURL()+ "?" + hsr.getQueryString()
            if (currentTime > 0) {
                String out = MessageFormat.format("{0}|({1,number,#}, {2,number,#}, {3,number,#})|{4,number,#}",
                        webBrowser, numberOfVisibleColls, numberOfHiddenColls, numberOfRows, ((System.nanoTime() - currentTime) / 1000000));
                currentTime = -1;
                System.out.println(out);
            }
            fc.doFilter(sr, sr1);
        }

        @Override
        public void destroy() {
        }

    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

        private static final long serialVersionUID = 1L;

    }

    private static final long serialVersionUID = 1L;

    private static final String vaadinVersion = com.vaadin.shared.Version.getFullVersion();

    private TextField tfColumns;
    private TextField tfHiddenColumns;
    private TextField tfRows;

    private CssLayout gridwrapper;
    private Grid<GridEntry> grid;

    @Override
    protected void init(final VaadinRequest vaadinRequest) {

        final VerticalLayout vertLayout = new VerticalLayout();
        vertLayout.setSizeFull();

        // Adding Vaadin version label
        Label versionLabel = new Label("Grid Test with Vaadin Version: " + vaadinVersion);
        versionLabel.setId("vaadinVersionLabel");
        vertLayout.addComponent(versionLabel);

        tfColumns = new TextField();
        tfColumns.setCaption("column count");
        tfColumns.setId("columncount");
        tfColumns.setValue("10");
        tfHiddenColumns = new TextField();
        tfHiddenColumns.setCaption("hidden column count");
        tfHiddenColumns.setId("hiddencolumncount");
        tfHiddenColumns.setValue("10");
        tfRows = new TextField();
        tfRows.setCaption("row count");
        tfRows.setId("rowcount");
        tfRows.setValue("1000");

        HorizontalLayout horLayout = new HorizontalLayout();
        vertLayout.addComponent(horLayout);

        horLayout.addComponent(tfColumns);
        horLayout.addComponent(tfHiddenColumns);
        horLayout.addComponent(tfRows);

        // Create new Grid with gridEntries
        final Button hideShowButton = new Button("hide Grid");
        hideShowButton.setId("gridButton");
        hideShowButton.addClickListener(event -> {
            if (grid.isVisible()) {
                grid.setVisible(false);
                hideShowButton.setCaption("build Grid");
            } else {
                try {
                    //System.out.println("Time since build grid done:" + (System.nanoTime() - currentTime));
                    System.out.println("Start render");
                    WebBrowser wb = Page.getCurrent().getWebBrowser();
                    LatencyFilter.setGridConfiguration(System.nanoTime(), Integer.parseInt(tfColumns.getValue()), Integer.parseInt(tfHiddenColumns.getValue()), Integer.parseInt(tfRows.getValue()), (wb.isIE()?"IE":(wb.isFirefox()?"FF":"CH")));
                    buildGrid();

                } catch (NumberFormatException e) {
                    Notification.show("only numbers allowed for column counts");
                    return;
                }
                grid.setVisible(true);

                hideShowButton.setCaption("hide Grid");
            }
        });

        horLayout.addComponent(hideShowButton);
        horLayout.setComponentAlignment(hideShowButton, Alignment.BOTTOM_LEFT);

        gridwrapper = new CssLayout();
        gridwrapper.setSizeFull();

        buildGrid();
        tfColumns.setValue("100");

        vertLayout.addComponent(gridwrapper);
        vertLayout.setExpandRatio(gridwrapper, 1);
        vertLayout.setMargin(true);
        vertLayout.setSpacing(true);

        setContent(vertLayout);

        getPage().setTitle("Vaadin Grid Performance Test");
    }

    private void buildGrid() {
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
            gridEntrySet.add(new GridEntry("Content #" + i));//+ ":" + random.nextInt()));
        }

        Collection<GridEntry> gridEntries = gridEntrySet;
        grid = new Grid<>("My Test Grid", gridEntries);

        HeaderRow headerRow = grid.addHeaderRowAt(1);

        for (int i = 0; i < columns; i++) {
            Column<GridEntry, String> col = grid.addColumn(GridEntry::getContent);
            col.setCaption("Col#" + i);
            TextField searchField = new TextField();
            searchField.setHeight("28px");
            searchField.setWidth("100%");
            headerRow.getCell(col).setComponent(searchField);

            if (i != 0) {
                col.setWidth(getRandomColumnWidth());
            }
        }

        for (int i = 0; i < hiddenColumns; i++) {
            Column<GridEntry, String> col = grid.addColumn(GridEntry::getContent).setCaption("hidden #" + i).setHidable(true).setHidden(
                    true);
            col.setCaption("hCol#" + i);
            headerRow.getCell(col).setComponent(new TextField());
        }

        grid.setColumnReorderingAllowed(true);
        grid.setFrozenColumnCount(1);
        grid.setId("testGrid");

        grid.setSizeFull();

        // Activate multi selection mode
        grid.setSelectionMode(SelectionMode.MULTI);

        gridwrapper.addComponent(grid);
    }

    private double getRandomColumnWidth() {
        return 80.0 + Math.random() * 220;
    }
}
