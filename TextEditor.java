package Java.TextEditor;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

public class TextEditor extends JFrame {
    protected static final int SAVE_FILE = 1;
    protected static final int THROW_FILE = 2;
    protected boolean firstSaved = false;
    protected boolean changed = false;
    private File currentFile;    
    private JTextArea editor;
    private JLabel indicatorPosition;
    private JScrollPane scrollPane;


    public TextEditor() {
        super("Test");
        editor = new JTextArea();
        
        
        editor.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                int caretPosition = e.getDot();
                try{
                    int row = editor.getLineOfOffset(caretPosition);
                    int col = caretPosition - editor.getLineStartOffset(row);
                    indicatorPosition.setText("  Ze " + (row + 1) + ", Sp " + (col + 1));
                } catch(BadLocationException b) {
                    dispose();
                }
            }
            
        });
        

        setJMenuBar(createMenuBar());
        
        scrollPane = scrollPane();
        getContentPane().add(scrollPane);
        newFile();
        
        Document d = editor.getDocument();
        d.addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                if(changed == false) {
                    changed = true;
                    setTitle(getTitle() + " *");
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(changed == false) {
                    changed = true;
                    setTitle(getTitle() + " *");
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
            
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(changed == true) {
                    int val = askForSaving();
                    if(val == SAVE_FILE) saveFile();
                    if(val == THROW_FILE) dispose();
                    if(val == 0) {}
                }
            }
        });

        indicatorPosition = new JLabel("  Ze 1, Sp 1");
        add(indicatorPosition, BorderLayout.SOUTH);

        Image img = Toolkit.getDefaultToolkit().getImage("C:\\Users\\patri\\Desktop\\editor.png");
        setIconImage(img);
    
        setSize(500, 500);
        setDefaultCloseOperation(2);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        menuBar = new JMenuBar();

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription("File options");
        menuBar.add(menu);

        //Open file button
        menuItem = new JMenuItem("Open file");//, KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Open file");
        menuItem.addActionListener(l -> {
            if(changed == true) {
                int val = askForSaving();
                if (val == SAVE_FILE) {
                    saveFile();
                }
            }
            openFile();
        });
        menu.add(menuItem);

        //New file button
        menuItem = new JMenuItem("New file");//, KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("New file");
        menuItem.addActionListener(l -> {
            if(changed == true) {
                int val = askForSaving();
                if (val == SAVE_FILE) {
                    saveFile();
                }
            }
            newFile();
        });
        menu.add(menuItem);

        //Save file button
        menuItem = new JMenuItem("Save file");//, KeyEvent.VK_I);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Save file");
        menuItem.addActionListener(l -> {
            saveFile();
        });
        menu.add(menuItem);        

        menu.addSeparator();

        //New Window Button
        menuItem = new JMenuItem("New Window");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.SHIFT_MASK + ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("New Window");
        menuItem.addActionListener(l -> {
            new TextEditor();
        });
        menu.add(menuItem);    

        menu.addSeparator();

        //Exit editor button
        menuItem = new JMenuItem("Close Editor");
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Exit");
        menuItem.addActionListener(l -> {
            if (changed == true) {
                int val = askForSaving();
                if(val == SAVE_FILE) saveFile();
                if(val == THROW_FILE) dispose();
                if(val == 0) {}
            } else {
                dispose();
            }
        });
        menu.add(menuItem);


        //Second menu for darkmode
        menu = new JMenu("Appearance");
        menu.setMnemonic(KeyEvent.VK_U);
        menu.getAccessibleContext().setAccessibleDescription("Appearance options");
        menuBar.add(menu);
        
        menuItem = new JCheckBoxMenuItem("Dark mode");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                int val = e.getStateChange();
                if(val == 1) {
                    editor.setBackground(Color.DARK_GRAY);
                    editor.setForeground(Color.WHITE);
                } else if (val == 2) {
                    editor.setBackground(Color.WHITE);
                    editor.setForeground(Color.BLACK);
                }
            }
            
        });
        menu.add(menuItem);

        return menuBar;
    }

    private void openFile() {
        if(editor.getText() != "") editor.setText("");
        JFileChooser choose = new JFileChooser();
        int valchoose = choose.showOpenDialog(this);
        if(valchoose == JFileChooser.APPROVE_OPTION) {
            File file = choose.getSelectedFile();
            try {
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String s = "";
                String result = "";
                try {
                    while((s = br.readLine()) != null) {
                        result += s + "\n";
                    }
                    br.close();
                    editor.setText(result);
                    setTitle(file.getName());
                    currentFile = file;
                    firstSaved = true;
                    changed = false;
                    setDefaultCloseOperation(EXIT_ON_CLOSE);
                } catch (IOException e) {
                    new JOptionPane("Error in reading File!", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch(FileNotFoundException e) {
                new JOptionPane("Error in choosing File!", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private int askForSaving() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        int val = JOptionPane.showConfirmDialog(this, "Save file before exiting?", "Save file", JOptionPane.YES_NO_CANCEL_OPTION);
        if(val == JOptionPane.YES_OPTION) {
            return SAVE_FILE;
        } else if(val == JOptionPane.NO_OPTION) {
            return THROW_FILE;
        } else {
            return 0;
        }
    }

    private void saveFile() {
        if(firstSaved == true) {
            File file = currentFile;
            if(file == null) new JOptionPane("Error while saving file", JOptionPane.ERROR_MESSAGE);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                PrintWriter pw = new PrintWriter(osw);
                String s = editor.getText();
                pw.println(s);
                pw.close();
                changed = false;
                setTitle(file.getName());
                }
            catch (IOException e) {
                new JOptionPane("Error while saving file", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JFileChooser save = new JFileChooser("Save file");
            int valsave = save.showSaveDialog(this);
            if (valsave == JFileChooser.APPROVE_OPTION) {
                File file = save.getSelectedFile();
                try {
                    if(file.createNewFile()) {
                        FileOutputStream fos = new FileOutputStream(file);
                        OutputStreamWriter osw = new OutputStreamWriter(fos);
                        PrintWriter pw = new PrintWriter(osw);
                        pw.println(editor.getText());
                        pw.close();
                    }
                    firstSaved = true;
                    changed = false;
                    currentFile = file;
                    setTitle(file.getName());
                } catch (IOException e) {
                    new JOptionPane("Error while saving file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void newFile() {
        setTitle("New file");
        editor.setText("");
        changed = false;
        firstSaved = false;
        currentFile = null;
    }

    private JScrollPane scrollPane() {
        Border border = BorderFactory.createLineBorder(getBackground());
        editor.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL, 30, 20, 0, 500);
        scrollBar.setVisible(true);
        JScrollPane scrollPane = new JScrollPane(editor);
        scrollPane.setVerticalScrollBar(scrollBar);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setVisible(true);
        return scrollPane;
    }

    public static void main(String[] args) {
        new TextEditor();
    }
}

    /*
    private void scrollFunctionality() {
        int lines = editor.getText().split("\n").length;
        String s = "";
        if(lines > editor.getHeight()) {
            for(int i = 0; i < editor.getHeight())

        }
    }

    private int linesOfText(File file) {
        int lines = 0;
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String s;
            while((s = br.readLine()) != null) {
                lines++;
            }
            br.close();
        } catch (IOException e) {
            //throw new NullFileException("Error");
            e.printStackTrace();
        }           
        return lines;
    }
    */
        //JScrollPane scrollPane = new JScrollPane(editor);
        //JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL, 30, 20, 0, 500);
        //scrollPane.setVerticalScrollBar(scrollBar);
        //scrollPane.setVisible(true);
        //getContentPane().add(scrollPane);
        //getContentPane().add(scrollPane);
        /*
        scrollBar = new JScrollBar(JScrollBar.VERTICAL, 30, 20, 0, 500);
        scrollBar.addAdjustmentListener(e -> {
            int scrollValue = scrollBar.getValue();
            editor.getCaret().setDot(scrollValue); // Set the caret position
        });

        getContentPane().add(scrollBar, BorderLayout.EAST);
        */
        //JScrollPane scrollPane = new JScrollPane(editor);
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //add(scrollPane);