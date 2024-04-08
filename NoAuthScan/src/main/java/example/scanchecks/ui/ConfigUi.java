package example.scanchecks.ui;

import javax.swing.*;


public class ConfigUi extends JToolBar {
    public JCheckBox autoSendRequestCheckBox;
    public JCheckBox configCheckBox;

    public ConfigUi() {
        this.autoSendRequestCheckBox = new JCheckBox("Auto sending");
        this.configCheckBox = new JCheckBox("Enable configuration file");
        // 默认发送
        this.autoSendRequestCheckBox.setSelected(true);
        this.configCheckBox.setSelected(true);
        // 不可悬浮
        this.setFloatable(false);
        this.add(autoSendRequestCheckBox);
        this.add(configCheckBox);
    }

    public Boolean getAutoSendRequest() {
        return this.autoSendRequestCheckBox.isSelected();
    }

    public Boolean getConfigStatus() {
        return this.configCheckBox.isSelected();
    }
}