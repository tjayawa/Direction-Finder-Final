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

            JTabbedPane tabs = new JTabbedPane();

            JPanel textPanel = new JPanel(new BorderLayout());
            JPanel inputPanel = new JPanel(new GridLayout(3, 1, 0, 20));
            LTPanel startInput = new LTPanel("Start");
            LTPanel endInput = new LTPanel("End");
            DefaultListModel<String> errorModel = new DefaultListModel<>();
            JList<String> errorList = new JList<>(errorModel);
            errorList.setSelectionModel(new DefaultListSelectionModel() {
                @Override
                public void setSelectionInterval(int index0, int index1) {

                }

                @Override
                public void addSelectionInterval(int index0, int index1) {

                }
            });
            JScrollPane scrollPane = new JScrollPane(errorList);
            scrollPane.setVisible(false);

            errorList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    Component c = super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);

                    if (value != null && value.toString().startsWith("Error")) {
                        c.setForeground(Color.decode("#822b24"));
                    } else if (value != null && value.toString().startsWith("Warning")){
                        c.setForeground(Color.decode("#cfa730"));
                    } else {
                        c.setForeground(list.getForeground());
                    }

                    return c;
                }
            });

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

            JPanel mapPanel = new JPanel();
            mapPanel.setBackground(Color.decode("#F5F5DC"));

            leftPanel.add(tabs, BorderLayout.CENTER);
            tabs.addTab("Text", textPanel);
            tabs.addTab("Map", mapPanel);

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
                scrollPane.setVisible(false);
                errorModel.clear();
            });

            calculate.addActionListener(e -> {
                boolean sIsLatitude = Location.isLatitude(startInput.latitude.getText());
                boolean sIsLongitude = Location.isLongitude(startInput.longitude.getText());
                boolean eIsLatitude = Location.isLatitude(endInput.latitude.getText());
                boolean eIsLongitude = Location.isLongitude(endInput.longitude.getText());
                boolean sNameNotBlank = !startInput.name.getText().isBlank();
                boolean eNameNotBlank = !endInput.name.getText().isBlank();
                scrollPane.setVisible(false);
                errorModel.clear();
                if (sIsLatitude && sIsLongitude && eIsLatitude && eIsLongitude) {
                    Location start = Location.parseLocation(String.format("(%s, %s)",
                            startInput.latitude.getText(), startInput.longitude.getText()));
                    Location end = Location.parseLocation(String.format("(%s, %s)",
                            endInput.latitude.getText(), endInput.longitude.getText()));
                    if (sNameNotBlank && eNameNotBlank) {
                        start.setName(startInput.name.getText().trim());
                        end.setName(endInput.name.getText().trim());
                    } else {
                        if (!sNameNotBlank) {
                            errorModel.addElement("Warning: Add Start Location Name");
                        }
                        if (!eNameNotBlank) {
                            errorModel.addElement("Warning: Add End Location Name");
                        }
                        scrollPane.setVisible(true);
                    }
                    bearingText.setText(Bearing.format(start, end));
                } else {
                    if (!sIsLatitude) {
                        errorModel.addElement("Error: Enter Latitude for Start Location");
                    }
                    if (!sIsLongitude) {
                        errorModel.addElement("Error: Enter Longitude for Start Location");
                    }
                    if (!eIsLatitude) {
                        errorModel.addElement("Error: Enter Latitude for End Location");
                    }
                    if (!eIsLongitude) {
                        errorModel.addElement("Error: Enter Longitude for End Location");
                    }
                    if (!sNameNotBlank) {
                        errorModel.addElement("Warning: Add Start Location Name");
                    }
                    if (!eNameNotBlank) {
                        errorModel.addElement("Warning: Add End Location Name");
                    }
                    scrollPane.setVisible(true);
                }
            });

            frame.add(containerPanel);
        });
    }
}
