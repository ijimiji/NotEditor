package dist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.util.regex.Pattern;

public class FindReplaceDialog extends JDialog {
    private static boolean sessionActive = false;
    private JTextPane textPane;
    private JPanel parentPanel;
    private String regex = "";
    private int offset = 0;
    private List<Pair<Integer, Integer>> matches = new Vector<Pair<Integer, Integer>>();

    JTextField replaceTextField = new JTextField();
    JTextField findTextField = new JTextField();
    JButton findNext = new JButton("Find Next", null);
    JButton findPrevious = new JButton("Find Previous", null);
    JLabel findLabel = new JLabel("Find Text:");
    JButton replaceAll = new JButton("Replace All", null);
    JButton replace = new JButton("Replace", null);
    JLabel notFound = new JLabel("Text not Found");

    public FindReplaceDialog(Frame parent, boolean modal, JTextPane textPane) {
        super(parent, modal);
        this.textPane = textPane;
        initComponents();
    }

    private void initComponents() {
        if (sessionActive == true) {
            dispose();
            return;
        }
        sessionActive = true;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("Find/Replace");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width / 2.6);
        int height = (int) (screenSize.height / 4.5);
        setSize(width, height);
        setLocation((screenSize.width / 2) - (width / 2), (screenSize.height / 2) - (height / 2));
        setResizable(false);
        setVisible(true);
        setModal(false);
        notFound.setForeground(Color.RED);
        notFound.setVisible(false);
        createDialog();
        add(parentPanel);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                sessionActive = false;
                dispose();
            }
        });
    }

    private void createDialog() {
        JLabel replaceLabel = new JLabel("Replace with:");
        findNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                find(1);
            }
        });
        findPrevious.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                find(-1);
            }
        });
        replace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                replace();
            }
        });
        replaceAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                replaceAll();
            }
        });
        findTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                notFound.setVisible(false);
            }
        });
        replaceTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                notFound.setVisible(false);
            }
        });
        parentPanel = new JPanel();
        GroupLayout layout = new GroupLayout(parentPanel);
        parentPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        int buttonSize = 130;
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(findLabel)
                                .addComponent(replaceLabel))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(findTextField)
                                .addComponent(replaceTextField)))
                .addGroup(layout.createSequentialGroup().addComponent(notFound)
                        .addComponent(findNext, buttonSize, buttonSize, buttonSize)
                        .addComponent(findPrevious, buttonSize, buttonSize, buttonSize))
                .addGroup(layout.createSequentialGroup().addComponent(replace, buttonSize, buttonSize, buttonSize)
                        .addComponent(replaceAll, buttonSize, buttonSize, buttonSize)));
        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup().addComponent(findLabel).addGap(12).addComponent(replaceLabel))
                .addGroup(layout.createSequentialGroup().addComponent(findTextField).addComponent(replaceTextField)))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(notFound)
                        .addComponent(findNext).addComponent(findPrevious))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(replace)
                        .addComponent(replaceAll)));
    }

    private void replaceAll() {
        if (!findTextField.getText().isEmpty()) {
            String oldContent = textPane.getText();
            String newContent = oldContent.replaceAll(findTextField.getText(), replaceTextField.getText());
            textPane.setText(newContent);
        }
    }

    private void replace() {
        String replaceWord = replaceTextField.getText();
        if (textPane.getSelectedText() != null)
            textPane.replaceSelection(replaceWord);
    }

    private void find(int delta) {
        notFound.setVisible(false);
        var newRegex = findTextField.getText();
        var text = textPane.getText();
        if (!newRegex.equals(regex)) {
            this.regex = newRegex;
            this.offset = 0;
        } else {
            offset += delta;
            int size = matches.size();

            if (offset >= matches.size()) {
                offset = offset - size;
            }
            if (offset < 0) {
                offset = size + offset;
            }
        }

        var m = Pattern.compile(newRegex).matcher(text);
        this.matches = new Vector<Pair<Integer, Integer>>();
        while (m.find()) {
            matches.add(new Pair<Integer, Integer>(m.start(), m.end()));
        }

        int size = matches.size();
        if (size == 0) {
            notFound.setVisible(true);
        } else {
            var position = matches.get(offset);
            textPane.select(position.first, position.second);
            textPane.requestFocusInWindow();
        }
    }
}
