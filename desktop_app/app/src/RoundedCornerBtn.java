import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class RoundedCornerBtn extends JButton {
	
	private Color hoverBackgroundColor = new Color(0xbe97c6);
    private Color pressedBackgroundColor = new Color(0xefbcd5);
	
	public RoundedCornerBtn(String text) {
        super(text);
        super.setContentAreaFilled(false);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); //padding
    }
	
	 @Override
	    public boolean isFocusable() {
	        return false; // Prevent the button from gaining focus
	    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Apply a custom shape to the background of the text field
        int width = getWidth();
        int height = getHeight();
        RoundRectangle2D shape = new RoundRectangle2D.Double(0, 0, width, height, height, height);
        g2d.setClip(shape);
        if (getModel().isPressed()) {
        	g2d.setColor(pressedBackgroundColor);
        	this.setForeground(new Color(0x4b5267));
        } else if (getModel().isRollover()) {
        	g2d.setColor(hoverBackgroundColor);
        	this.setForeground(new Color(0x4b5267));
        } else {
        	g2d.setColor(getBackground());
        	this.setForeground(new Color(0xefbcd5));
        }
        
        g2d.fillRect(0, 0, width, height);

        super.paintComponent(g2d);

        g2d.dispose();
    }
    
    @Override
    public void setContentAreaFilled(boolean b) {
    }

    public Color getHoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    public void setHoverBackgroundColor(Color hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }

    public Color getPressedBackgroundColor() {
        return pressedBackgroundColor;
    }

    public void setPressedBackgroundColor(Color pressedBackgroundColor) {
        this.pressedBackgroundColor = pressedBackgroundColor;
    }
}