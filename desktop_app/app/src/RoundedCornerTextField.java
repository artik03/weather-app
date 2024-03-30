import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class RoundedCornerTextField extends JTextField {
    public RoundedCornerTextField() {
        super();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); //padding
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Apply a custom shape to the background of the text field
        int width = getWidth();
        int height = getHeight();
        RoundRectangle2D shape = new RoundRectangle2D.Double(0, 0, width, height, height, height);
        g2d.setClip(shape);
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, width, height);

        super.paintComponent(g2d);

        g2d.dispose();
    }
}
