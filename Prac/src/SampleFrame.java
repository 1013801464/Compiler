import javax.swing.*;
import java.awt.*;

public class SampleFrame  extends JFrame{
    public SampleFrame() throws HeadlessException {
        super("title");
        setBounds(100,100,300,300);
        add(new SamplePanel(), BorderLayout.CENTER);
        this.setVisible(true);
    }
}