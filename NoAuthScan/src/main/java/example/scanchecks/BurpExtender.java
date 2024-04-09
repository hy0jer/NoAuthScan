package example.scanchecks;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import example.scanchecks.ui.ConfigUi;
import example.scanchecks.ui.Menu;
import example.scanchecks.ui.SettingPage;
import example.scanchecks.model.View_model;

import javax.swing.*;
import java.awt.*;

import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;


public class BurpExtender implements BurpExtension {
    private MontoyaApi api;

    @Override
    public void initialize(MontoyaApi api) {
        Logging logging = api.logging();
        logging.logToOutput("""
                ===================================
                NoAuthScan v1.6 load success!
                Author: hy0jer
                ===================================""");

        logging.logToOutput("config_yaml is location in   "+System.getProperty("user.dir")+"/"+"auth_config.yaml");
        this.api = api;
        View_model tableModel = new View_model();
        ConfigUi config = new ConfigUi();
        Component test = constructLoggerTab(tableModel, config);
        api.extension().setName("NoAuthScan");
        api.userInterface().registerSuiteTab("NoAuthScan", test);
        api.userInterface().registerContextMenuItemsProvider(new Menu(api, tableModel, config));
        api.scanner().registerScanCheck(new ScanCheck(api, tableModel, config));
    }

    private Component constructLoggerTab(View_model tableModel, ConfigUi config) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane downSplitPane = new JSplitPane();
        downSplitPane.setResizeWeight(0.5D);

        JTabbedPane tabs = new JTabbedPane();
        JTabbedPane tabx = new JTabbedPane();

        UserInterface userInterface = api.userInterface();

        HttpRequestEditor requestViewer = userInterface.createHttpRequestEditor(READ_ONLY);
        HttpResponseEditor responseViewer = userInterface.createHttpResponseEditor(READ_ONLY);

        tabs.addTab("Request", requestViewer.uiComponent());
        tabx.addTab("Response", responseViewer.uiComponent());

        downSplitPane.add(tabs, "left");
        downSplitPane.add(tabx, "right");

        splitPane.setRightComponent(downSplitPane);

        JTable table = new JTable(tableModel) {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                View_model.ListTree listTree = tableModel.get(rowIndex);
                requestViewer.setRequest(listTree.responseReceived.request());
                responseViewer.setResponse(listTree.responseReceived.response());
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };

        JSplitPane upSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        upSplitPane.setEnabled(false);
        upSplitPane.add(config, "left");

        JScrollPane scrollPane = new JScrollPane(table);
        upSplitPane.add(scrollPane, "right");
        splitPane.setLeftComponent(upSplitPane);
        JTabbedPane jtabbedPane = new JTabbedPane();


        SettingPage ui = new SettingPage();
        Component pane = SettingPage.configTab_builder(config, ui.textField);

        jtabbedPane.addTab("Details", splitPane);
        jtabbedPane.addTab("Setting", pane);

        JSplitPane top = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        top.setTopComponent(jtabbedPane);
        api.userInterface().applyThemeToComponent(top);
        return top;
    }
}
