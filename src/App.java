import java.awt.Font;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.swing.*;
import javax.swing.JScrollPane;
import java.awt.event.*;
import javax.swing.text.*;
import dist.TextLineNumber;
import dist.Theme;
import dist.SyntaxColorizer;
import dist.FindReplaceDialog;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.*;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class App extends JFrame implements ActionListener {
    public DefaultStyledDocument doc;
    public JTextPane txt;
    String filePath = "";
    private SyntaxColorizer syntax;

    private JMenu createFindMenu(JTextPane txt) {
        JMenu find = new JMenu("Find");
        JMenuItem replace = new JMenuItem("Find and replace");
        find.add(replace);
        replace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                new FindReplaceDialog(null, false, txt);
            }
        });
        return find;
    }

    private JMenu createBuildMenu() {
        JMenu build = new JMenu("Build");
        JMenuItem run = new JMenuItem("Run");
        build.add(run);

        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String compileLog = "";
                String exeLog = "";
                try {
                    String buffer;
                    Runtime r = Runtime.getRuntime();

                    Process compile = r.exec("g++ " + filePath + " -o exe");
                    compile.waitFor();
                    BufferedReader compileOutBuffer = new BufferedReader(
                            new InputStreamReader(compile.getErrorStream()));

                    while ((buffer = compileOutBuffer.readLine()) != null) {
                        compileLog += buffer;
                    }

                    Process run = r.exec("./exe");
                    run.waitFor();
                    BufferedReader exeLogBuffer = new BufferedReader(new InputStreamReader(run.getInputStream()));
                    while ((buffer = exeLogBuffer.readLine()) != null) {
                        exeLog += buffer;
                    }

                    if (compileLog.equals("")) {
                        JOptionPane.showMessageDialog(null, "Output\n" + exeLog);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error\n" + compileLog);
                    }
                } catch (Exception e) {
                    System.out.println(compileLog);
                    System.out.println(exeLog);

                }
            }
        });
        return build;
    }

    private JMenu createFileMenu(JTextPane txt) {
        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem exit = new JMenuItem("Exit");
        file.add(open);
        file.add(save);
        file.addSeparator();
        file.add(exit);

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.showOpenDialog(txt);
                    File selectedFile = fileChooser.getSelectedFile();
                    String content = Files.readString(selectedFile.toPath(), StandardCharsets.UTF_8);
                    filePath = selectedFile.toString();
                    txt.setText(content);
                    syntax.updateTextStyles();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    String content = txt.getText();
                    if (filePath == "") {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.showOpenDialog(txt);
                        File selectedFile = fileChooser.getSelectedFile();
                        filePath = selectedFile.toString();
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                    writer.write(content);
                    writer.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        return file;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    public App() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        var theme = new Theme();
        var doc = new DefaultStyledDocument();
        JTextPane txt = new JTextPane(doc);
        JMenuBar menubar = new JMenuBar();
        var fileMenu = createFileMenu(txt);
        var findMenu = createFindMenu(txt);
        var buildMenu = createBuildMenu();
        menubar.add(fileMenu);
        menubar.add(findMenu);
        menubar.add(buildMenu);

        txt.setText("hello\nhella\nhello");
        txt.setBackground(theme.black);
        txt.setForeground(theme.white);
        txt.setCaretColor(theme.white);
        txt.setFont(new Font(theme.fontFamily, 0, theme.fontSize));
        this.syntax = new SyntaxColorizer(txt, theme);
        doc.setDocumentFilter(syntax);
        syntax.updateTextStyles();
        JScrollPane scrollPane = new JScrollPane(txt);
        TextLineNumber tln = new TextLineNumber(txt);
        tln.setCurrentLineForeground(theme.white);
        tln.setForeground(theme.gray);
        tln.setBackground(theme.black);
        scrollPane.setRowHeaderView(tln);
        add(scrollPane);
        setJMenuBar(menubar);
        setVisible(true);
    }

    public static void main(String args[]) {
        new App();
    }
}
