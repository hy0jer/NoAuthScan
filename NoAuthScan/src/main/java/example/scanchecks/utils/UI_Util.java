package example.scanchecks.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class UI_Util {
    public static JPanel set_panel() {
        JPanel panel1 = new JPanel();
        panel1.setBorder(new EmptyBorder(6, 0, 6, 0));
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        panel1.setAlignmentX(0.0f);
        return panel1;
    }
}
