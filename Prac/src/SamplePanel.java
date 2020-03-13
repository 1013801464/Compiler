import java.awt.Color;

import javax.swing.*;

public class SamplePanel extends JPanel {

    public SamplePanel() {
        setBackground(Color.RED);
        add(new JLabel("haha"));
    }

    public static void main(String[] args) {
        new SampleFrame();
    }
}