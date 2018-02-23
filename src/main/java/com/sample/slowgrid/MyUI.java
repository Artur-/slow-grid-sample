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
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
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

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

	// define basic grid details here:
    private static int gridNumCols = 20;
    private static long gridNumHidCols = 15;
    private static long gridRows = 1000;
	
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
        
        Label descriptionLabel = new Label("Default setup uses the slowest combination of variable width columns and complex headers aka one row of filter columns with TextField, DateField or ComboBox.");
        descriptionLabel.setId("vaadinVersionLabel");
        vertLayout.addComponent(descriptionLabel);

        tfColumns = new TextField();
        tfColumns.setCaption("column count");
        tfColumns.setId("columncount");
        tfColumns.setValue(String.valueOf(gridNumCols));
        tfHiddenColumns = new TextField();
        tfHiddenColumns.setCaption("hidden column count");
        tfHiddenColumns.setId("hiddencolumncount");
        tfHiddenColumns.setValue(String.valueOf(gridNumHidCols));
        tfRows = new TextField();
        tfRows.setCaption("row count");
        tfRows.setId("rowcount");
        tfRows.setValue(String.valueOf(gridRows));
        cbFixedWidth = new CheckBox();
        cbFixedWidth.setId("cbfixedwidth");
        cbFixedWidth.setCaption("fixed column widths");
        cbComplexHeader = new CheckBox();
        cbComplexHeader.setId("cbcomplexheader");
        cbComplexHeader.setCaption("complex header");
        // turn complex headers with filters on by default
        cbComplexHeader.setValue(true);
        
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
                hideShowButton.setDescription("Click hide/show Grid button to render the grid and measure times");
            }
        });

        horLayout.addComponent(hideShowButton);
        horLayout.setComponentAlignment(hideShowButton, Alignment.TOP_LEFT);

        gridwrapper = new CssLayout();
        gridwrapper.setSizeFull();

        buildGrid();

        vertLayout.addComponent(gridwrapper);
        vertLayout.setExpandRatio(gridwrapper, 1);
        vertLayout.setMargin(true);
        vertLayout.setSpacing(true);

        setContent(vertLayout);

        getPage().setTitle("Vaadin Grid Performance Test");
    }

    private Component createHeaderComponent(int i) {
        Component searchField = null;
        if (i % 3 == 1) {
            searchField = new DateTimeField();
            searchField.setHeight("28px");
            searchField.setWidth("100%");
        } else if (i % 3 == 2) {
            searchField = new ComboBox<String>("", Arrays.asList("val1", "val2", "val3", "val4"));
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
            	assert headerRow != null : "HeaderRow NullCheck";
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
            	assert headerRow != null : "HeaderRow NullCheck";
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
			
			private static final long serialVersionUID = 1L;

			public void columnVisibilityChanged(ColumnVisibilityChangeEvent event) {
				System.out.println("Visibility change");
				
			}
		});
        grid.addItemClickListener(new ItemClickListener<GridEntry>() {

			private static final long serialVersionUID = 1L;

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
