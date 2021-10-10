import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class MainWindow extends JFrame
{
    public static int PIXEL_READ_SIZE = 50;

    private final JPanel rootPanel;

    private JTextPane blocksInputField;

    private JTextPane equationInputField;

    private JTextPane circleInputField;

    private JTextField errorInfoText;

    public MainWindow(final String title) throws HeadlessException
    {
        super(title);
        this.setLayout(new GridLayout(2, 1, 10, 10));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.rootPanel = new JPanel();
        this.rootPanel.setLayout(new GridLayout(4, 3, 10, 10));

        initBlocksSettingUIElements();
        initEquationUIElements();
        initCircleUIElements();

        this.add(this.rootPanel);

        initErrorUIElements();

        this.pack();
        this.setVisible(true);
    }

    private void initBlocksSettingUIElements()
    {
        JTextPane blocksInfoText = new JTextPane();
        blocksInfoText.setEditable(false);
        blocksInfoText.setText("Anzahl Blöcke (Y x Y) für die Anzeige:");

        this.blocksInputField = new JTextPane();
        this.blocksInputField.setText("50");

        JPanel filler = new JPanel();

        this.rootPanel.add(blocksInfoText);
        this.rootPanel.add(this.blocksInputField);
        this.rootPanel.add(filler);
    }

    private void initEquationUIElements()
    {
        JTextPane equationInfoText = new JTextPane();
        equationInfoText.setEditable(false);
        equationInfoText.setText("Rasterisierung nach Gleichung:");

        this.equationInputField = new JTextPane();
        this.equationInputField.setPreferredSize(new Dimension(200, 20));
        this.equationInputField.setText("x+2");

        JButton equationEvaluateButton = new JButton("Evaluate");
        equationEvaluateButton.addActionListener((event) -> {
            try
            {
                MainWindow.PIXEL_READ_SIZE = Integer.parseInt(this.blocksInputField.getText());

                final Expression exp = new ExpressionBuilder(equationInputField.getText()).variable("x").build();

                final ArrayList<Map.Entry<Float, Float>> values = new ArrayList<>();

                for (float i = PIXEL_READ_SIZE / -2f; i < PIXEL_READ_SIZE / 2f; i += 1f)
                {
                    values.add(new Correspondence(2 * i / PIXEL_READ_SIZE,
                            2 * (float) exp.setVariable("x", i).evaluate() / PIXEL_READ_SIZE));
                }

                final float[] pixels = new RenderWindow(values).render();
                new PixelDisplayWindow(pixels, this.getBounds());
                this.errorInfoText.setText("No Error");
            }
            catch (Exception e)
            {
                this.errorInfoText.setText(e.getMessage());
            }
        });

        this.rootPanel.add(equationInfoText);
        this.rootPanel.add(this.equationInputField);
        this.rootPanel.add(equationEvaluateButton);
    }

    private void initCircleUIElements()
    {
        JTextPane circleInfoText = new JTextPane();
        circleInfoText.setEditable(false);
        circleInfoText.setText("Rasterisierung eines Kreises (Radius):");

        this.circleInputField = new JTextPane();
        this.circleInputField.setText("20");

        JButton circleEvaluateButton = new JButton("Evaluate");
        circleEvaluateButton.addActionListener((event) -> {
            try
            {
                MainWindow.PIXEL_READ_SIZE = Integer.parseInt(this.blocksInputField.getText());

                float[][] values = new float[PIXEL_READ_SIZE][PIXEL_READ_SIZE];

                int offset = PIXEL_READ_SIZE / 2 - 1;

                int r = Integer.parseInt(circleInputField.getText());
                int d = -r;
                int x = r;
                int y = 0;

                while (y <= x)
                {
                    values[offset + x][offset + y] = 1f;
                    values[offset + y][offset + x] = 1f;
                    values[offset + y][offset - x] = 1f;
                    values[offset + x][offset - y] = 1f;
                    values[offset - x][offset - y] = 1f;
                    values[offset - y][offset - x] = 1f;
                    values[offset - y][offset + x] = 1f;
                    values[offset - x][offset + y] = 1f;

                    d += 2 * y + 1;
                    y++;

                    if (d > 0)
                    {
                        x--;
                        d -= 2 * x;
                    }
                }

                float[] pixels = new float[PIXEL_READ_SIZE * PIXEL_READ_SIZE];

                for (int i = 0; i < PIXEL_READ_SIZE; ++i)
                {
                    System.arraycopy(values[i], 0, pixels, i * PIXEL_READ_SIZE, PIXEL_READ_SIZE);
                }

                new PixelDisplayWindow(pixels, this.getBounds());
                this.errorInfoText.setText("No Error");
            }
            catch (Exception e)
            {
                this.errorInfoText.setText(e.getMessage());
            }
        });

        this.rootPanel.add(circleInfoText);
        this.rootPanel.add(this.circleInputField);
        this.rootPanel.add(circleEvaluateButton);
    }

    private void initErrorUIElements()
    {
        this.errorInfoText = new JTextField();
        this.errorInfoText.setEditable(false);
        this.errorInfoText.setPreferredSize(new Dimension(400, 20));
        this.errorInfoText.setText("No Error");

        this.add(this.errorInfoText);
    }

    static class Correspondence implements Map.Entry<Float, Float>
    {
        private final Float key;
        private final Float value;

        public Correspondence(Float key, Float value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public Float getKey()
        {
            return this.key;
        }

        @Override
        public Float getValue()
        {
            return this.value;
        }

        @Override
        public Float setValue(Float value)
        {
            return null;
        }
    }
}
