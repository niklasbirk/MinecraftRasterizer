import javax.swing.*;
import java.awt.*;

public class PixelDisplayWindow extends JFrame
{
    public PixelDisplayWindow(final float[] pixels, final Rectangle mainWindowBounds)
    {
        super("Minecraft Layout");

        this.setLayout(new GridLayout(MainWindow.PIXEL_READ_SIZE, MainWindow.PIXEL_READ_SIZE, 1, 1));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocation(mainWindowBounds.x + mainWindowBounds.width, mainWindowBounds.y);
        this.setResizable(false);

        final int pixelAmount = MainWindow.PIXEL_READ_SIZE * MainWindow.PIXEL_READ_SIZE;

        for (int i = 0; i < pixelAmount; i++)
        {
            final JPanel panel = new JPanel();

            if (pixels[i] < 1f)
            {
                panel.setBackground(new Color(100, 130, 180));
            }
            else
            {
                panel.setBackground(new Color(30, 90, 30));
            }

            this.add(panel);
        }

        this.pack();
        this.setVisible(true);
    }
}
