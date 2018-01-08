package ui;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Paranoid
 * @description
 * @date 2017/10/17
 * @company 美衫
 **/
public class ImageGUI extends JComponent {

    private BufferedImage image;

    public ImageGUI() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (image == null) {
            g2d.setPaint(Color.BLACK);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        } else {
            g2d.drawImage(image, 0, 0, this.getWidth() , this.getHeight(), null);
        }
    }

    public void createWin(String title) {
        JDialog ui = new JDialog();
        ui.setTitle(title);
        ui.getContentPane().setLayout(new BorderLayout());
        ui.getContentPane().add(this, BorderLayout.CENTER);
        ui.setSize(new Dimension(330, 240));
        ui.setVisible(true);
    }

    public void createWin(String title, Dimension size) {
        JDialog ui = new JDialog();
        ui.setTitle(title);
        ui.getContentPane().setLayout(new BorderLayout());
        ui.getContentPane().add(this, BorderLayout.CENTER);
        ui.setSize(size);
        ui.setVisible(true);

    }


    public void imShow(BufferedImage image) {
        this.image = image;
        this.repaint();
    }

}
