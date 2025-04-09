import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ButtonPanelBuilder {
    private final Map<String, JButton> buttons = new HashMap<>();
    private final JTextField searchField = new JTextField(15);
    private final JTextField homeLatField = new JTextField(10);
    private final JTextField homeLonField = new JTextField(10);
    private final JComboBox<String> searchCriteriaComboBox = new JComboBox<>(new String[]{"Title", "Company", "Skills"});
    private final JComboBox<String> columnDropdown = new JComboBox<>(new String[]{"Job ID", "Title", "Company", "Salary", "Deadline"});

    class CircleButton extends JButton {

        public CircleButton() {
            setToolTipText("Help");
            setPreferredSize(new Dimension(40, 40));
            setMinimumSize(new Dimension(40, 40));
            setMaximumSize(new Dimension(40, 40));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            int diameter = Math.min(getWidth(), getHeight());

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw circle background
            g2.setColor(new Color(100, 100, 255));
            g2.fillOval(0, 0, diameter, diameter);

            // Draw border
            g2.setColor(new Color(70, 70, 200));
            g2.drawOval(0, 0, diameter - 1, diameter - 1);

            // Draw "i" symbol manually
            g2.setFont(new Font("Dialog", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();
            String text = "i";
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();

            int x = (diameter - textWidth) / 2;
            int y = (diameter + textHeight) / 2 - 3;

            g2.setColor(Color.WHITE);
            g2.drawString(text, x, y);

            g2.dispose();
        }

        @Override
        public boolean contains(int x, int y) {
            int radius = getWidth() / 2;
            int centerX = radius;
            int centerY = getHeight() / 2;
            return ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)) <= radius * radius;
        }
    }

    public JPanel buildButtonPanel() {
        // Initialize and style buttons
        createStyledButton("Set Home Location", new Color(64, 120, 205), 150, 20);
        createStyledButton("Add Job", new Color(8, 145, 15), 110, 20);
        createStyledButton("Remove Job", new Color(193, 20, 6), 110, 20);
        createStyledButton("Calculate Distance", new Color(33, 150, 243), 110, 20);
        createStyledButton("Sort", new Color(69, 112, 204), 110, 20);
        createStyledButton("Search", new Color(50, 100, 186), 110, 20);
        createStyledButton("Load CSV", new Color(245, 120, 0),  110, 20);
        createStyledButton("Save CSV", new Color(0, 200, 0), 110, 20);
        createStyledButton("Display Graph", new Color(165, 78, 193), 200, 40);
        JButton helpButton = new CircleButton();
        buttons.put("Help", helpButton);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Row 1
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        row1.add(getButton("Add Job"));
        row1.add(getButton("Remove Job"));
        panel.add(row1);

        // Row 2
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        row2.add(new JLabel("Home Latitude:"));
        row2.add(homeLatField);
        row2.add(new JLabel("Home Longitude:"));
        row2.add(homeLonField);
        row2.add(getButton("Set Home Location"));
        panel.add(row2);

        // Row 3
        JPanel row3 = new JPanel(new BorderLayout());
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        sortPanel.add(getButton("Sort"));
        sortPanel.add(columnDropdown);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        searchPanel.add(searchField);
        searchPanel.add(getButton("Search"));
        searchPanel.add(searchCriteriaComboBox);
        row3.add(sortPanel, BorderLayout.WEST);
        row3.add(searchPanel, BorderLayout.EAST);
        panel.add(row3);

        // Row 4
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        row4.add(getButton("Display Graph"));
        panel.add(row4);

        // Row 5
        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        row5.add(getButton("Load CSV"));
        row5.add(getButton("Save CSV"));
        row5.add(getButton("Help"));
        panel.add(row5);

        return panel;
    }

    private void createStyledButton(String name, Color color, int width, int height) {
        JButton button = new JButton(name);
        button.setPreferredSize(new Dimension(width, height));
        button.setOpaque(true);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Dialog", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(new javax.swing.border.LineBorder(color.darker(), 1, true));
        addHoverEffect(button, color);
        buttons.put(name, button);
    }

    private void addHoverEffect(JButton button, Color originalColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.brighter());
                button.setBorder(new javax.swing.border.LineBorder(originalColor.darker(), 2, true));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
                button.setBorder(new javax.swing.border.LineBorder(originalColor.darker(), 1, true));
            }
        });
    }

    public JButton getButton(String name) {
        return buttons.get(name);
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JTextField getHomeLatField() {
        return homeLatField;
    }

    public JTextField getHomeLonField() {
        return homeLonField;
    }

    public JComboBox<String> getSearchCriteriaComboBox() {
        return searchCriteriaComboBox;
    }

    public JComboBox<String> getColumnDropdown() {
        return columnDropdown;
    }
}
