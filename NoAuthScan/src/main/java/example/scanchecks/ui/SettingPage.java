package example.scanchecks.ui;


import example.scanchecks.utils.UI_Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingPage {

    public JTextField textField;

    public SettingPage() {
        this.textField = textField_builder();
    }

    private JTextField textField_builder() {
        JTextField textField = new JTextField(200);
        textField.setMaximumSize(textField.getPreferredSize());
        String path = System.getProperty("user.dir") + "/" + "auth_config.yaml";
        textField.setText(path);
        return textField;
    }

    public static Component configTab_builder(ConfigUi config, JTextField textField) {
        JPanel mainpanel = new JPanel();
        mainpanel.setBorder(new EmptyBorder(8, 20, 8, 20));
        mainpanel.setAlignmentX(0.0f);
        mainpanel.setLayout(new BoxLayout(mainpanel, 1));

        JLabel label1 = new JLabel("Base Setting");
        label1.setFont(new Font("Arial", 1, 17));
        label1.setForeground(new Color(232, 128, 61, 228));
        label1.setAlignmentX(0.0f);

        JPanel panel1 = UI_Util.set_panel();
        JPanel panel3 = UI_Util.set_panel();

        JLabel label2 = new JLabel("Config Path Setting");
        label2.setFont(new Font("Arial", 1, 17));
        label2.setForeground(new Color(232, 128, 61, 228));
        label2.setAlignmentX(0.0f);

        JLabel label = new JLabel("config file path: ");

        panel1.add(config);
        panel3.add(label);
        panel3.add(textField);

        mainpanel.add(label1);
        mainpanel.add(panel1);
        mainpanel.add(label2);
        mainpanel.add(panel3);

        return mainpanel;
    }
}
