import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.swing.*;
import javax.swing.text.*;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatClientProperties;
import java.util.Random;

public class DirectionFinder {

    private static class LTPanel extends JPanel {
        public JTextField name;
        public JTextField latitude;
        public JTextField longitude;
        private SDialog search = null;
        private Random rand = new Random();

        public LTPanel (String labelName) {
            setLayout(new GridLayout(2, 2, 10, 10));
            JPanel extra = new JPanel(new BorderLayout());
            JLabel panelName = new JLabel(labelName + " Location", SwingConstants.CENTER);
            ImageIcon ico = new ImageIcon("res/Pin.png");
            JButton fetchLocation = new JButton(ico);
            fetchLocation.setToolTipText("Fetch Location");
            fetchLocation.addActionListener(e -> {
                boolean online = isOnline();
                if (search == null && online) {
                    search = new SDialog();
                }
            });
            extra.add(panelName, BorderLayout.LINE_END);
            extra.add(fetchLocation, BorderLayout.LINE_START);
            name = new JTextField(10);
            name.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Location Name");
            latitude = new JTextField(10);
            latitude.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Latitude");
            longitude = new JTextField(10);
            longitude.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Longitude");
            add(extra);
            add(name);
            add(latitude);
            add(longitude);
        }

        private class SDialog extends JDialog {
            public SDialog() {
                super();
                setTitle("Search");
                JPanel top = new JPanel(new BorderLayout());
                JPanel buttons = new JPanel(new GridLayout(1, 2));
                ULButton ulButton = new ULButton();
                ImageIcon ico = new ImageIcon("res/Search.png");
                JButton sButton = new JButton(ico);
                sButton.setToolTipText("Search for Location");
                JTextField sBar = new JTextField();
                buttons.add(ulButton);
                buttons.add(sButton);
                top.add(buttons, BorderLayout.LINE_END);
                top.add(sBar, BorderLayout.CENTER);
                add(top, BorderLayout.NORTH);
                JPanel bottom = new JPanel(new BorderLayout());
                JLabel error = new JLabel();
                error.setForeground(Color.red);
                JButton locate = new JButton("Locate");
                locate.setFont(locate.getFont().deriveFont(25.0f));
                bottom.add(locate, BorderLayout.LINE_END);
                bottom.add(error, BorderLayout.CENTER);
                add(bottom, BorderLayout.SOUTH);
                DefaultListModel<Location> locationsModel = new DefaultListModel<>();
                JList<Location> locationsList = new JList<>(locationsModel);
                locationsList.setCellRenderer((jList, location, index, isSelected, cellHasFocus) -> {
                    JPanel panel = new JPanel(new GridLayout(2, 1));
                    JLabel name = new JLabel(location.getName());
                    name.setFont(name.getFont().deriveFont(15.0f));
                    JLabel info = new JLabel(location.toString());
                    info.setFont(info.getFont().deriveFont(10.0f));
                    info.setForeground(Color.lightGray);

                    panel.add(name);
                    panel.add(info);

                    panel.setBackground(isSelected ? jList.getSelectionBackground() : jList.getBackground());
                    name.setBackground(isSelected ? jList.getSelectionBackground() : jList.getBackground());
                    info.setBackground(isSelected ? jList.getSelectionBackground() : jList.getBackground());

                    return panel;
                });
                JScrollPane scrollPane = new JScrollPane(locationsList);
                add(scrollPane, BorderLayout.CENTER);
                locate.addActionListener(e -> {
                    if (!locationsList.isSelectionEmpty()) {
                        Location location = locationsList.getSelectedValue();
                        name.setText(location.getName());
                        latitude.setText(location.getLatitudeString());
                        longitude.setText(location.getLongitudeString());
                        dispose();
                    } else {
                        error.setText("Add and Choose items in List");
                    }
                });
                sButton.addActionListener(e -> {
                    if (!sBar.getText().matches("\\s*")) {
                        double la = 90 * rand.nextDouble();
                        la = rand.nextBoolean() ? la : la * -1;
                        double lo = 180 * rand.nextDouble();
                        la = rand.nextBoolean() ? la : la * -1;
                        Location location = new Location(la, lo);
                        location.setName(sBar.getText());
                        locationsModel.addElement(location);
                        sBar.setText("");
                    } else {
                        error.setText("Put text in search Bar");
                    }
                });
                setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        dispose();
                    }
                });
                setSize(400, 400);
                setLocation(LTPanel.this.getX() + 400, LTPanel.this.getY() + 410);
                setVisible(true);
            }

            private class ULButton extends JButton {
                public ULButton() {
                    ImageIcon ico = new ImageIcon("res/YourLocation.png");
                    super(ico);
                    setToolTipText("Your Location");
                    addActionListener(e -> {
                        name.setText("Hi User");
                        dispose();
                    });
                }
            }

            @Override
            public void dispose() {
                search = null;
                super.dispose();
            }
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

            JPanel rightPanel = new JPanel(new BorderLayout());

            JTextPane bearingText = new JTextPane();
            bearingText.setFont(bearingText.getFont().deriveFont(25.0f));
            bearingText.setText("Enter Coordinates for Bearing");
            bearingText.setEditable(false);
            bearingText.setFocusable(false);
            bearingText.setOpaque(false);
            bearingText.setBorder(null);

            StyledDocument doc = bearingText.getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), center, false);

            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.add(bearingText, BorderLayout.CENTER);

            JLabel bearingResult = new JLabel("", SwingConstants.CENTER);
            bearingResult.setFont(bearingResult.getFont().deriveFont(25.0f));

            JCompass compass = new JCompass();
            compass.setVisible(false);

            rightPanel.add(wrapper, BorderLayout.NORTH);
            rightPanel.add(bearingResult, BorderLayout.SOUTH);
            rightPanel.add(compass, BorderLayout.CENTER);

            containerPanel.add(leftPanel);
            containerPanel.add(rightPanel);

            reset.addActionListener(e -> {
                bearingText.setText("Enter Coordinates for Bearing");
                bearingResult.setText("");
                startInput.reset();
                endInput.reset();
                scrollPane.setVisible(false);
                errorModel.clear();
                compass.setVisible(false);
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
                    Location start = Location.parseLocation(
                            startInput.latitude.getText(), startInput.longitude.getText());
                    Location end = Location.parseLocation(
                            endInput.latitude.getText(), endInput.longitude.getText());
                    if (sNameNotBlank && eNameNotBlank) {
                        start.setName(startInput.name.getText().trim());
                        end.setName(endInput.name.getText().trim());
                    } else {
                        scrollPane.setVisible(true);
                    }
                    double bearing = Bearing.calculate(start, end);
                    bearingText.setText((sNameNotBlank && eNameNotBlank) ? String.format("%s to %s",
                            start.getName(), end.getName()) : "");
                    bearingResult.setText(Bearing.format(bearing));
                    compass.setBearing(bearing);
                    compass.repaint();
                    compass.setVisible(true);
                } else {
                    scrollPane.setVisible(true);
                }
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
                if (!sNameNotBlank || !eNameNotBlank) {
                    scrollPane.setVisible(true);
                    if (!sNameNotBlank) {
                        errorModel.addElement("Warning: Add Start Location Name");
                    }
                    if (!eNameNotBlank) {
                        errorModel.addElement("Warning: Add End Location Name");
                    }
                }
            });

            frame.add(containerPanel);
        });
    }

    public static boolean isOnline() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
