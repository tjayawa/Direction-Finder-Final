import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import javax.swing.text.*;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatClientProperties;

public class DirectionFinder {

    private static class LTPanel extends JPanel {
        public JTextField name;
        public JTextField latitude;
        public JTextField longitude;

        public LTPanel (String labelName) {
            setLayout(new GridLayout(2, 2, 10, 10));
            JLabel panelName = new JLabel(labelName + " Location", SwingConstants.CENTER);
            name = new JTextField(10);
            name.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Location Name");
            latitude = new JTextField(10);
            latitude.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Latitude");
            longitude = new JTextField(10);
            longitude.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Longitude");
            add(panelName);
            add(name);
            add(latitude);
            add(longitude);
        }

        public void reset() {
            name.setText("");
            latitude.setText("");
            longitude.setText("");
        }
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("DirectionFinder");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            JPanel containerPanel = new JPanel(new GridLayout(1, 2, 10, 0));

            JPanel leftPanel = new JPanel(new BorderLayout());

            CardLayout switcher = new CardLayout();
            JPanel currentPanel = new JPanel(switcher);

            JPanel switchButtons = new JPanel(new FlowLayout(FlowLayout.LEADING));
            switchButtons.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            JButton text = new JButton("Text");
            text.setEnabled(false);
            JButton map = new JButton("Map");
            switchButtons.add(text);
            switchButtons.add(map);

            JPanel textPanel = new JPanel(new BorderLayout());
            JPanel inputPanel = new JPanel(new GridLayout(3, 1, 0, 20));
            LTPanel startInput = new LTPanel("Start");
            LTPanel endInput = new LTPanel("End");
            DefaultListModel<String> errorModel = new DefaultListModel<>();
            JList<String> errorList = new JList<>(errorModel);
            JScrollPane scrollPane = new JScrollPane(errorList);
            scrollPane.setVisible(false);
            inputPanel.add(startInput);
            inputPanel.add(endInput);
            inputPanel.add(scrollPane);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
            buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            JButton calculate = new JButton("Calculate");
            JButton reset = new JButton("Reset");
            buttonPanel.add(calculate);
            buttonPanel.add(reset);

            textPanel.add(inputPanel, BorderLayout.CENTER);
            textPanel.add(buttonPanel, BorderLayout.SOUTH);

            leftPanel.add(switchButtons, BorderLayout.NORTH);

            JPanel mapPanel = new JPanel();
            mapPanel.setBackground(Color.decode("#F5F5DC"));

            currentPanel.add(textPanel, "Text");
            currentPanel.add(mapPanel, "Map");

            leftPanel.add(currentPanel, BorderLayout.CENTER);

            text.addActionListener(e -> {
                text.setEnabled(false);
                map.setEnabled(true);
                switcher.show(currentPanel, "Text");
            });

            map.addActionListener(e -> {
                text.setEnabled(true);
                map.setEnabled(false);
                switcher.show(currentPanel, "Map");
            });

            JPanel rightPanel = new JPanel(new GridBagLayout());

            JTextPane bearingText = new JTextPane();
            bearingText.setText("Enter Coordinates for Bearing");
            bearingText.setEditable(false);
            bearingText.setFocusable(false);
            bearingText.setOpaque(false);
            bearingText.setBorder(null);

            StyledDocument doc = bearingText.getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), center, false);

            rightPanel.add(bearingText, new GridBagConstraints());

            rightPanel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    super.componentResized(e);
                    int newSize = Math.max(13, rightPanel.getHeight() / 13);
                    bearingText.setFont(bearingText.getFont().deriveFont((float) newSize));
                }
            });

            containerPanel.add(leftPanel);
            containerPanel.add(rightPanel);

            reset.addActionListener(e -> {
                bearingText.setText("Enter Coordinates for Bearing");
                startInput.reset();
                endInput.reset();
            });

            frame.add(containerPanel);
        });
    }
}
