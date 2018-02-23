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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.ColumnVisibilityChangeEvent;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.ColumnVisibilityChangeListener;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.components.grid.ItemClickListener;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
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
        private static boolean fixedWidth;
        private static boolean complexHeader;
        private static String webBrowser;

        public static void setGridConfiguration(long currentTime, boolean fixedWidth, boolean complexHeader, int numberOfVisibleColls, int numberOfHiddenColls, int numberOfRows, String wb) {
            LatencyFilter.currentTime = currentTime;
            LatencyFilter.numberOfVisibleColls = numberOfVisibleColls;
            LatencyFilter.numberOfHiddenColls = numberOfHiddenColls;
            LatencyFilter.numberOfRows = numberOfRows;
            LatencyFilter.webBrowser = wb;
            LatencyFilter.fixedWidth = fixedWidth;
            LatencyFilter.complexHeader = complexHeader;
        }

        @Override
        public void init(FilterConfig fc) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
            HttpServletRequest hsr = (HttpServletRequest) sr;
            //hsr.getRequestURL()+ "?" + hsr.getQueryString()
            if (currentTime > 0) {
                String out = MessageFormat.format("{0}|{1}|{2}|({3,number,#}, {4,number,#}, {5,number,#})|{6,number,#}",
                        webBrowser, fixedWidth, complexHeader, numberOfVisibleColls, numberOfHiddenColls, numberOfRows, ((System.nanoTime() - currentTime) / 1000000));
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
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = true)
    public static class MyUIServlet extends VaadinServlet {

        private static final long serialVersionUID = 1L;

    }

    private static final long serialVersionUID = 1L;

    private static final String vaadinVersion = com.vaadin.shared.Version.getFullVersion();

    private TextField tfColumns;
    private TextField tfHiddenColumns;
    private TextField tfRows;
    private CheckBox cbFixedWidth;
    private CheckBox cbComplexHeader;

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
        cbFixedWidth = new CheckBox();
        cbFixedWidth.setId("cbfixedwidth");
        cbFixedWidth.setCaption("fixed column widths");
        cbComplexHeader = new CheckBox();
        cbComplexHeader.setId("cbcomplexheader");
        cbComplexHeader.setCaption("complex header");
        
        VerticalLayout vertLayoutInner = new VerticalLayout();
        vertLayoutInner.addComponent(cbFixedWidth);
        vertLayoutInner.addComponent(cbComplexHeader);

        HorizontalLayout horLayout = new HorizontalLayout();
        vertLayout.addComponent(horLayout);

        horLayout.addComponent(tfColumns);
        horLayout.addComponent(tfHiddenColumns);
        horLayout.addComponent(tfRows);
        horLayout.addComponent(vertLayoutInner);
        horLayout.setComponentAlignment(vertLayoutInner, Alignment.TOP_LEFT);
        
        

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
                    LatencyFilter.setGridConfiguration(System.nanoTime(), cbFixedWidth.getValue(), cbComplexHeader.getValue(), Integer.parseInt(tfColumns.getValue()), Integer.parseInt(tfHiddenColumns.getValue()), Integer.parseInt(tfRows.getValue()), (wb.isIE() ? "IE" : (wb.isFirefox() ? "FF" : "CH")));
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
        horLayout.setComponentAlignment(hideShowButton, Alignment.TOP_LEFT);

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

    private Component createHeaderComponent(int i) {
        Component searchField = null;
        if (true || i % 3 == 4) {
            searchField = new DateTimeField();
            searchField.setHeight("28px");
            searchField.setWidth("100%");
        } else if (true || i % 3 == 2) {
            searchField = new ComboBox("", Arrays.asList("val1", "val2", "val3", "val4"));
            searchField.setHeight("28px");
            searchField.setWidth("100%");
        } else {
            searchField = new TextField();
            searchField.setHeight("28px");
            searchField.setWidth("100%");
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
        grid = new Grid<>("My Test Grid", gridEntries);

        HeaderRow headerRow = null;
        if (complexHeader) {
            headerRow = grid.addHeaderRowAt(1);
        }

        for (int i = 0; i < columns; i++) {
            Column<GridEntry, String> col = grid.addColumn(GridEntry::getContent).setHidable(true);
            col.setCaption("Col#" + i);
            if (complexHeader) {
                headerRow.getCell(col).setComponent(createHeaderComponent(i));
            }

            if (i != 0 && fixedWidth) {
                col.setWidth(getRandomColumnWidth());
            }
        }

        for (int i = 0; i < hiddenColumns; i++) {
            Column<GridEntry, String> col = grid.addColumn(GridEntry::getContent).setCaption("hidden #" + i).setHidable(true).setHidden(
                    true);
            col.setCaption("hCol#" + i);
            if (complexHeader) {
                headerRow.getCell(col).setComponent(createHeaderComponent(i));
            }
        }
        
        grid.setColumnReorderingAllowed(true);
        grid.setFrozenColumnCount(1);
        grid.setId("testGrid");

        grid.setSizeFull();

        // Activate multi selection mode
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addColumnVisibilityChangeListener(new ColumnVisibilityChangeListener() {
			
			public void columnVisibilityChanged(ColumnVisibilityChangeEvent event) {
				System.out.println("Visibility change");
				
			}
		});
        grid.addItemClickListener(new ItemClickListener<GridEntry>() {

			public void itemClick(ItemClick<GridEntry> event) {
				System.out.println("Clicked!!");
				
			}
        	
        });
      

        gridwrapper.addComponent(grid);
    }
    //test runs must be equal
    private Random rr = new Random(7);

    private double getRandomColumnWidth() {
        return 80.0 + rr.nextDouble() * 220;
    }
}
