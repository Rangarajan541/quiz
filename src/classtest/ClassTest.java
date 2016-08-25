package classtest;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.security.*;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;

public class ClassTest extends javax.swing.JFrame {

    private String currentTestID = null;
    private ArrayList<String> questionList = new ArrayList<>();
    private ArrayList<String> questionListMod = new ArrayList<>();
    private ArrayList<Integer> answeredList = new ArrayList<>();
    private Connection con;
    private Statement stmt, stmt2;
    private final int TYPE_TEACHER = 1, TYPE_STUDENT = 0;
    private String SUBJECT = null, loginName;
    private int PRESENTUSERTYPE = -1, wakeUpSeconds = 300, curQuesInd = 0, totalAnsweredQuestions = 0, totalQuestions = 0, totalFlagged = 0, testCountdown = 0;
    private java.util.Timer wakeUpTimer;
    private java.util.TimerTask wakeUpTimerTask;
    private java.util.TimerTask testTimerTask;
    private final String separator = "==InternalSeparator==";

    public ClassTest() {
        initComponents();
        redirectPage.setVisible(true);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz", "root", "open");
            stmt = con.createStatement();
            stmt2 = con.createStatement();
        } catch (ClassNotFoundException | SQLException ex) {
            showSQLException("Error occured while registering sql driver");
            ex.printStackTrace();
        }

        WindowAdapter onCloseListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit? You will be automatically logged out.", "Exit confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    logout();
                    System.exit(0);
                }
            }
        };
        WindowAdapter resultsCloseListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanUpAfterTest();
                studentPanelPage.setVisible(true);
            }
        };
        WindowAdapter abortTestListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                abortTest();
            }
        };

        MouseListener wakeUpListenerMouse = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                wakeUpSeconds = 300;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                wakeUpSeconds = 300;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                wakeUpSeconds = 300;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                wakeUpSeconds = 300;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                wakeUpSeconds = 300;
            }
        };
        KeyListener wakeUpListenerKey = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                wakeUpSeconds = 300;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                wakeUpSeconds = 300;
            }

            @Override
            public void keyTyped(KeyEvent e) {
                wakeUpSeconds = 300;
            }
        };

        FocusListener wakeUpListenerFocus = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                wakeUpSeconds = 300;
            }

            @Override
            public void focusLost(FocusEvent e) {
                wakeUpSeconds = 300;
            }
        };
        redirectPage.setTitle("Welcome");
        redirectPage.pack();
        redirectPage.setResizable(false);
        redirectPage.setLocationRelativeTo(null);
        redirectPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        studentRegisterPage.setTitle("Student Registration");
        studentRegisterPage.pack();
        studentRegisterPage.setResizable(false);
        studentRegisterPage.setLocationRelativeTo(null);
        studentRegisterPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        teacherRegisterPage.setTitle("Teacher Registration");
        teacherRegisterPage.pack();
        teacherRegisterPage.setResizable(false);
        teacherRegisterPage.setLocationRelativeTo(null);
        teacherRegisterPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginPage.setTitle("Login");
        loginPage.setResizable(false);
        loginPage.pack();
        loginPage.setLocationRelativeTo(null);
        loginPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        studentPanelPage.setTitle("Student Home");
        studentPanelPage.setResizable(false);
        studentPanelPage.pack();
        studentPanelPage.setLocationRelativeTo(null);
        studentPanelPage.addWindowListener(onCloseListener);
        studentPanelPage.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        studentQuestionPage.setTitle("Online Test In Progress - Do not close");
        studentQuestionPage.setResizable(false);
        studentQuestionPage.pack();
        studentQuestionPage.setLocationRelativeTo(null);
        studentQuestionPage.addWindowListener(abortTestListener);
        studentQuestionPage.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        studentFinishTestPage.setTitle("Test Results");
        studentFinishTestPage.setResizable(false);
        studentFinishTestPage.pack();
        studentFinishTestPage.setLocationRelativeTo(null);
        studentFinishTestPage.addWindowListener(resultsCloseListener);
        studentFinishTestPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        studentPreviousResultsPage.setTitle("Previous Test Results");
        studentPreviousResultsPage.setResizable(false);
        studentPreviousResultsPage.setLocationRelativeTo(studentPanelPage);
        studentPreviousResultsPage.pack();
        studentPreviousResultsPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        redirectPage = new javax.swing.JFrame();
        jPanel9 = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jButton9 = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        studentRegisterPage = new javax.swing.JFrame();
        jPanel11 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        jTextField4 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPasswordField3 = new javax.swing.JPasswordField();
        jLabel13 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        teacherRegisterPage = new javax.swing.JFrame();
        jPanel12 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jPasswordField4 = new javax.swing.JPasswordField();
        jTextField5 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPasswordField5 = new javax.swing.JPasswordField();
        jMenuBar3 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        loginPage = new javax.swing.JFrame();
        jPanel13 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jTextField1 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jMenuBar4 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        studentPanelPage = new javax.swing.JFrame();
        jPanel5 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jButton19 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jMenuBar5 = new javax.swing.JMenuBar();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        studentPreviousResultsPage = new javax.swing.JFrame();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jButton20 = new javax.swing.JButton();
        jMenuBar6 = new javax.swing.JMenuBar();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        studentQuestionPage = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();
        jLabel48 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jMenuBar7 = new javax.swing.JMenuBar();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        studentFinishTestPage = new javax.swing.JFrame();
        jPanel7 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jTextField7 = new javax.swing.JTextField();
        jButton18 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jMenuBar14 = new javax.swing.JMenuBar();
        jMenu14 = new javax.swing.JMenu();
        jMenuItem35 = new javax.swing.JMenuItem();
        jMenuItem37 = new javax.swing.JMenuItem();
        jMenuItem36 = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        teacherPanelPage = new javax.swing.JFrame();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jMenuBar8 = new javax.swing.JMenuBar();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        questionUploadPage = new javax.swing.JFrame();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel45 = new javax.swing.JLabel();
        jMenuBar9 = new javax.swing.JMenuBar();
        jMenu9 = new javax.swing.JMenu();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        buttonGroup2 = new javax.swing.ButtonGroup();
        teacherTestReportPage = new javax.swing.JFrame();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jMenuBar10 = new javax.swing.JMenuBar();
        jMenu10 = new javax.swing.JMenu();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenuItem25 = new javax.swing.JMenuItem();
        teacherQuestionPage = new javax.swing.JFrame();
        jLabel25 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jButton21 = new javax.swing.JButton();
        jMenuBar11 = new javax.swing.JMenuBar();
        jMenu11 = new javax.swing.JMenu();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        adminPage = new javax.swing.JFrame();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jLabel29 = new javax.swing.JLabel();
        jRadioButton11 = new javax.swing.JRadioButton();
        jRadioButton12 = new javax.swing.JRadioButton();
        jTextField10 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jButton24 = new javax.swing.JButton();
        jMenuBar12 = new javax.swing.JMenuBar();
        jMenu12 = new javax.swing.JMenu();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenuItem30 = new javax.swing.JMenuItem();
        jMenuItem31 = new javax.swing.JMenuItem();
        adminResetPassPage = new javax.swing.JFrame();
        jTextField11 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jPasswordField6 = new javax.swing.JPasswordField();
        jLabel33 = new javax.swing.JLabel();
        jPasswordField7 = new javax.swing.JPasswordField();
        jButton25 = new javax.swing.JButton();
        jMenuBar13 = new javax.swing.JMenuBar();
        jMenu13 = new javax.swing.JMenu();
        jMenuItem32 = new javax.swing.JMenuItem();
        jMenuItem33 = new javax.swing.JMenuItem();
        jMenuItem34 = new javax.swing.JMenuItem();

        redirectPage.setSize(new java.awt.Dimension(434, 361));

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel51.setFont(new java.awt.Font("Times New Roman", 0, 48)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(0, 110, 223));
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel51.setText("MAHATMA CBSE");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(220, 0, 0));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("ONLINE TEST SYSTEM");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setText("I am a:");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Student", "Teacher" }));

        jButton9.setText("Proceed to Login");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel37.setText("New?");

        jButton8.setText("Register");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(108, 108, 108)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addGap(76, 76, 76))
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel10Layout.createSequentialGroup()
                            .addComponent(jLabel9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(124, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        jMenu1.setText("Nav");

        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        redirectPage.setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout redirectPageLayout = new javax.swing.GroupLayout(redirectPage.getContentPane());
        redirectPage.getContentPane().setLayout(redirectPageLayout);
        redirectPageLayout.setHorizontalGroup(
            redirectPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, redirectPageLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(redirectPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        redirectPageLayout.setVerticalGroup(
            redirectPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(redirectPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setText("Full Name:");

        jLabel11.setText("Preferred Password:");

        jLabel12.setText("Confirm Password:");

        jLabel13.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(240, 0, 0));
        jLabel13.setText("STUDENT REGISTRATION FORM");

        jButton10.setText("Register");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 375, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                            .addGap(0, 88, Short.MAX_VALUE)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                    .addComponent(jButton10)
                                    .addGap(63, 63, 63))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPasswordField3)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel11)
                                    .addComponent(jTextField4)
                                    .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10)))
                            .addGap(97, 97, 97))
                        .addComponent(jLabel13))
                    .addContainerGap()))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 294, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel13)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel10)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel11)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel12)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jPasswordField3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jButton10)
                    .addContainerGap(33, Short.MAX_VALUE)))
        );

        jMenu2.setText("Nav");

        jMenuItem3.setText("Back");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar2.add(jMenu2);

        studentRegisterPage.setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout studentRegisterPageLayout = new javax.swing.GroupLayout(studentRegisterPage.getContentPane());
        studentRegisterPage.getContentPane().setLayout(studentRegisterPageLayout);
        studentRegisterPageLayout.setHorizontalGroup(
            studentRegisterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, studentRegisterPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        studentRegisterPageLayout.setVerticalGroup(
            studentRegisterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, studentRegisterPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel17.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(240, 0, 0));
        jLabel17.setText("TEACHER REGISTRATION FORM");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Informatics Practices", "Economics", "Business Studies", "Accountancy" }));

        jLabel18.setText("Select subject:");

        jButton11.setText("Register");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jLabel14.setText("Full Name:");

        jLabel15.setText("Preferred Password:");

        jLabel16.setText("Confirm Password:");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 380, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel12Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel12Layout.createSequentialGroup()
                            .addGap(79, 79, 79)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel18)
                                    .addComponent(jPasswordField5)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel15)
                                    .addComponent(jTextField5)
                                    .addComponent(jPasswordField4)
                                    .addComponent(jLabel14)
                                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addGap(66, 66, 66)
                                    .addComponent(jButton11)))
                            .addGap(101, 101, 101)))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel12Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel17)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel14)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel15)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPasswordField4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel16)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jPasswordField5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel18)
                    .addGap(18, 18, 18)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(jButton11)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jMenu3.setText("Nav");

        jMenuItem4.setText("Back");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenuItem5.setText("Exit");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuBar3.add(jMenu3);

        teacherRegisterPage.setJMenuBar(jMenuBar3);

        javax.swing.GroupLayout teacherRegisterPageLayout = new javax.swing.GroupLayout(teacherRegisterPage.getContentPane());
        teacherRegisterPage.getContentPane().setLayout(teacherRegisterPageLayout);
        teacherRegisterPageLayout.setHorizontalGroup(
            teacherRegisterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherRegisterPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );
        teacherRegisterPageLayout.setVerticalGroup(
            teacherRegisterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherRegisterPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 0, 0));
        jLabel1.setText("LOGIN");

        jLabel2.setText("Name:");

        jLabel3.setText("Password:");

        jButton1.setText("Login");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton9);
        jRadioButton9.setText("Student");
        jRadioButton9.setEnabled(false);

        buttonGroup2.add(jRadioButton10);
        jRadioButton10.setText("Teacher");
        jRadioButton10.setEnabled(false);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                                    .addComponent(jRadioButton9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jRadioButton10))
                                .addComponent(jPasswordField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(jLabel1))))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(132, 132, 132)
                        .addComponent(jButton1)))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton9)
                    .addComponent(jRadioButton10))
                .addGap(36, 36, 36)
                .addComponent(jButton1)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jMenu4.setText("Nav");

        jMenuItem6.setText("Back");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem6);

        jMenuItem7.setText("Exit");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem7);

        jMenuBar4.add(jMenu4);

        loginPage.setJMenuBar(jMenuBar4);

        javax.swing.GroupLayout loginPageLayout = new javax.swing.GroupLayout(loginPage.getContentPane());
        loginPage.getContentPane().setLayout(loginPageLayout);
        loginPageLayout.setHorizontalGroup(
            loginPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        loginPageLayout.setVerticalGroup(
            loginPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Informatics Practices", "Economics", "Business Studies", "Accountancy" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton2.setText("Go");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel4.setText("Select Subject:");

        jLabel5.setText("Welcome,");

        jLabel6.setText("Select Test:");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Test ID", "Description", "Status", "Subject"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel34.setText("You currently have ");

        jTextField12.setEditable(false);

        jLabel35.setText("pending tests.");

        jButton19.setText("See Previous Results");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton3.setText("Take Test!");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel35)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton19)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton19))
                .addContainerGap())
        );

        jMenu5.setText("Nav");

        jMenuItem10.setText("Sign Out");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem10);

        jMenuItem9.setText("Exit");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem9);

        jMenuBar5.add(jMenu5);

        studentPanelPage.setJMenuBar(jMenuBar5);

        javax.swing.GroupLayout studentPanelPageLayout = new javax.swing.GroupLayout(studentPanelPage.getContentPane());
        studentPanelPage.getContentPane().setLayout(studentPanelPageLayout);
        studentPanelPageLayout.setHorizontalGroup(
            studentPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentPanelPageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(studentPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        studentPanelPageLayout.setVerticalGroup(
            studentPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentPanelPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Subject", "Description", "Your Score"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(jTable4);

        jButton20.setText("OK");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(174, 174, 174)
                .addComponent(jButton20)
                .addContainerGap(186, Short.MAX_VALUE))
            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel14Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(264, Short.MAX_VALUE)
                .addComponent(jButton20)
                .addContainerGap())
            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel14Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(40, Short.MAX_VALUE)))
        );

        jMenu6.setText("Nav");

        jMenuItem11.setText("Back");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem11);

        jMenuItem13.setText("Sign Out");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem13);

        jMenuItem12.setText("Exit");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem12);

        jMenuBar6.add(jMenu6);

        studentPreviousResultsPage.setJMenuBar(jMenuBar6);

        javax.swing.GroupLayout studentPreviousResultsPageLayout = new javax.swing.GroupLayout(studentPreviousResultsPage.getContentPane());
        studentPreviousResultsPage.getContentPane().setLayout(studentPreviousResultsPageLayout);
        studentPreviousResultsPageLayout.setHorizontalGroup(
            studentPreviousResultsPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, studentPreviousResultsPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        studentPreviousResultsPageLayout.setVerticalGroup(
            studentPreviousResultsPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentPreviousResultsPageLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        studentQuestionPage.setSize(new java.awt.Dimension(0, 0));

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField2.setEditable(false);
        jTextField2.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jList2.setModel(new DefaultListModel());
        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
        });
        jScrollPane11.setViewportView(jList2);

        jLabel46.setText("Flagged (Double click to go to question)");

        jLabel47.setText("Navigator (Double click to go to question)");

        jTable8.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Question No.", "Answered"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable8MouseClicked(evt);
            }
        });
        jScrollPane12.setViewportView(jTable8);

        jLabel48.setText("Time Left:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField2)
                    .addComponent(jLabel46)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel48)
                .addGap(82, 82, 82))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel48)
                .addGap(2, 2, 2)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel44.setText("of");

        jTextField14.setEditable(false);

        jLabel38.setText("Test Description and Subject");

        jLabel39.setText("Marks per question:");

        jLabel40.setText("Question #:");

        jLabel41.setText("Total Answered:");

        jLabel42.setText("Total Flagged:");

        jLabel43.setText("Total Unanswered:");

        jTextField13.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(jLabel39))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(jLabel43))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton5.setText("Previous");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Flag question");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton26.setText("Unflag Question");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jTextArea1);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Option A");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Option B");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Option C");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("Option D");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        jButton4.setText("Next");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jRadioButton3)
                        .addGap(86, 86, 86)
                        .addComponent(jRadioButton4))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioButton1)
                .addGap(78, 78, 78)
                .addComponent(jRadioButton2)
                .addGap(316, 316, 316))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton4)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton17.setText("Finish Test");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                .addContainerGap())
        );

        jMenu7.setText("Nav");

        jMenuItem14.setText("Back");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem14);

        jMenuItem16.setText("Sign Out");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem16);

        jMenuItem15.setText("Exit");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem15);

        jMenuBar7.add(jMenu7);

        studentQuestionPage.setJMenuBar(jMenuBar7);

        javax.swing.GroupLayout studentQuestionPageLayout = new javax.swing.GroupLayout(studentQuestionPage.getContentPane());
        studentQuestionPage.getContentPane().setLayout(studentQuestionPageLayout);
        studentQuestionPageLayout.setHorizontalGroup(
            studentQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentQuestionPageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(studentQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(studentQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        studentQuestionPageLayout.setVerticalGroup(
            studentQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentQuestionPageLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(studentQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(studentQuestionPageLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(studentQuestionPageLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel21.setText("Test Results");

        jLabel24.setText("Click Question to view fully");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Question", "Correct Answer", "Your Answer"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jTable3);

        jLabel49.setText("Correct Answers:");

        jLabel50.setText("Wrong Answers:");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(161, 161, 161)
                                .addComponent(jLabel21))
                            .addComponent(jLabel24)))
                    .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jLabel50)
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextField7.setEditable(false);

        jButton18.setText("OK");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jLabel22.setText("Final Score:");

        jTextField6.setEditable(false);

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("/");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(78, 78, 78)
                                .addComponent(jLabel22))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(165, 165, 165)
                        .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextField6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton18)
                .addGap(5, 5, 5))
        );

        jMenu14.setText("Nav");

        jMenuItem35.setText("Back");
        jMenuItem35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem35ActionPerformed(evt);
            }
        });
        jMenu14.add(jMenuItem35);

        jMenuItem37.setText("Sign Out");
        jMenuItem37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem37ActionPerformed(evt);
            }
        });
        jMenu14.add(jMenuItem37);

        jMenuItem36.setText("Exit");
        jMenuItem36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem36ActionPerformed(evt);
            }
        });
        jMenu14.add(jMenuItem36);

        jMenuBar14.add(jMenu14);

        studentFinishTestPage.setJMenuBar(jMenuBar14);

        javax.swing.GroupLayout studentFinishTestPageLayout = new javax.swing.GroupLayout(studentFinishTestPage.getContentPane());
        studentFinishTestPage.getContentPane().setLayout(studentFinishTestPageLayout);
        studentFinishTestPageLayout.setHorizontalGroup(
            studentFinishTestPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, studentFinishTestPageLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(studentFinishTestPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        studentFinishTestPageLayout.setVerticalGroup(
            studentFinishTestPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentFinishTestPageLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Description", "Status", "Date added", "No of Questions", "Time Allotted"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setToolTipText("");
        jScrollPane4.setViewportView(jTable2);

        jLabel19.setText("My tests:");

        jButton12.setText("Lock Test");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("Unlock Test");

        jButton14.setText("Delete Test");

        jLabel20.setText("Welcome,");

        jButton15.setText("Create Test");

        jButton16.setText("Report");

        jButton23.setText("System Admin ");

        jLabel27.setText("Advanced:");

        jMenu8.setText("Nav");

        jMenuItem17.setText("Back");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem17);

        jMenuItem18.setText("Exit");
        jMenu8.add(jMenuItem18);

        jMenuItem19.setText("Sign Out");
        jMenu8.add(jMenuItem19);

        jMenuBar8.add(jMenu8);

        teacherPanelPage.setJMenuBar(jMenuBar8);

        javax.swing.GroupLayout teacherPanelPageLayout = new javax.swing.GroupLayout(teacherPanelPage.getContentPane());
        teacherPanelPage.getContentPane().setLayout(teacherPanelPageLayout);
        teacherPanelPageLayout.setHorizontalGroup(
            teacherPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherPanelPageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(teacherPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(teacherPanelPageLayout.createSequentialGroup()
                        .addGroup(teacherPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(teacherPanelPageLayout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton23))
                            .addGroup(teacherPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel20)
                                .addComponent(jLabel19)
                                .addComponent(jButton15)
                                .addGroup(teacherPanelPageLayout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(jButton12)
                                    .addGap(10, 10, 10)
                                    .addComponent(jButton13)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton14)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton16))))
                        .addGap(0, 28, Short.MAX_VALUE)))
                .addContainerGap())
        );
        teacherPanelPageLayout.setVerticalGroup(
            teacherPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherPanelPageLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel20)
                .addGap(18, 18, 18)
                .addComponent(jButton15)
                .addGap(18, 18, 18)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(teacherPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton12)
                    .addComponent(jButton13)
                    .addComponent(jButton14)
                    .addComponent(jButton16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(teacherPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton23)
                    .addComponent(jLabel27))
                .addContainerGap())
        );

        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setText("Enter Question here");
        jTextArea2.setToolTipText("");
        jTextArea2.setWrapStyleWord(true);
        jScrollPane3.setViewportView(jTextArea2);

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setText("Option A");

        buttonGroup1.add(jRadioButton6);
        jRadioButton6.setText("Option B");
        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton6ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton7);
        jRadioButton7.setText("Option C");

        buttonGroup1.add(jRadioButton8);
        jRadioButton8.setText("Option D");

        jLabel7.setText("You are now creating question:");

        jTextField3.setEditable(false);

        jButton7.setText("Next");

        jButton22.setText("Done");

        jCheckBox1.setText("Make this a reserve Question");

        jLabel45.setText("Choose correct Answer:");

        jMenu9.setText("Nav");

        jMenuItem20.setText("Back");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem20);

        jMenuItem21.setText("Exit");
        jMenu9.add(jMenuItem21);

        jMenuItem22.setText("Sign Out");
        jMenu9.add(jMenuItem22);

        jMenuBar9.add(jMenu9);

        questionUploadPage.setJMenuBar(jMenuBar9);

        javax.swing.GroupLayout questionUploadPageLayout = new javax.swing.GroupLayout(questionUploadPage.getContentPane());
        questionUploadPage.getContentPane().setLayout(questionUploadPageLayout);
        questionUploadPageLayout.setHorizontalGroup(
            questionUploadPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(questionUploadPageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(questionUploadPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(questionUploadPageLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jCheckBox1))
                    .addGroup(questionUploadPageLayout.createSequentialGroup()
                        .addGap(159, 159, 159)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton22))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, questionUploadPageLayout.createSequentialGroup()
                        .addComponent(jRadioButton5)
                        .addGap(41, 41, 41)
                        .addComponent(jRadioButton6)
                        .addGap(34, 34, 34)
                        .addComponent(jRadioButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jRadioButton8))
                    .addGroup(questionUploadPageLayout.createSequentialGroup()
                        .addComponent(jLabel45)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        questionUploadPageLayout.setVerticalGroup(
            questionUploadPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, questionUploadPageLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(questionUploadPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(questionUploadPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton5)
                    .addComponent(jRadioButton6)
                    .addComponent(jRadioButton7)
                    .addComponent(jRadioButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(questionUploadPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton22))
                .addContainerGap())
        );

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Name", "Score"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(jTable5);

        jMenu10.setText("Nav");

        jMenuItem23.setText("Back");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem23);

        jMenuItem24.setText("Exit");
        jMenu10.add(jMenuItem24);

        jMenuItem25.setText("Sign Out");
        jMenu10.add(jMenuItem25);

        jMenuBar10.add(jMenu10);

        teacherTestReportPage.setJMenuBar(jMenuBar10);

        javax.swing.GroupLayout teacherTestReportPageLayout = new javax.swing.GroupLayout(teacherTestReportPage.getContentPane());
        teacherTestReportPage.getContentPane().setLayout(teacherTestReportPageLayout);
        teacherTestReportPageLayout.setHorizontalGroup(
            teacherTestReportPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, teacherTestReportPageLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        teacherTestReportPageLayout.setVerticalGroup(
            teacherTestReportPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherTestReportPageLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jLabel25.setText("Enter Test Description (Short name):");

        jLabel26.setText("Enter points per question:");

        jButton21.setText("Enter questions");

        jMenu11.setText("Nav");

        jMenuItem26.setText("Back");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem26);

        jMenuItem27.setText("Exit");
        jMenu11.add(jMenuItem27);

        jMenuItem28.setText("Sign Out");
        jMenu11.add(jMenuItem28);

        jMenuBar11.add(jMenu11);

        teacherQuestionPage.setJMenuBar(jMenuBar11);

        javax.swing.GroupLayout teacherQuestionPageLayout = new javax.swing.GroupLayout(teacherQuestionPage.getContentPane());
        teacherQuestionPage.getContentPane().setLayout(teacherQuestionPageLayout);
        teacherQuestionPageLayout.setHorizontalGroup(
            teacherQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherQuestionPageLayout.createSequentialGroup()
                .addGroup(teacherQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(teacherQuestionPageLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(teacherQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(teacherQuestionPageLayout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(teacherQuestionPageLayout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(teacherQuestionPageLayout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addComponent(jButton21)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        teacherQuestionPageLayout.setVerticalGroup(
            teacherQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherQuestionPageLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(teacherQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(teacherQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addComponent(jButton21)
                .addContainerGap(140, Short.MAX_VALUE))
        );

        jLabel28.setText("Activity Log:");

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Name", "Type", "Last login", "Remark"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(jTable6);

        jLabel29.setText("Reset Password:");

        jRadioButton11.setText("Student");

        jRadioButton12.setText("Teacher");

        jLabel30.setText("Name:");

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(jTable7);

        jButton24.setText("Select");

        jMenu12.setText("Nav");

        jMenuItem29.setText("Back");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jMenu12.add(jMenuItem29);

        jMenuItem30.setText("Exit");
        jMenu12.add(jMenuItem30);

        jMenuItem31.setText("Sign Out");
        jMenu12.add(jMenuItem31);

        jMenuBar12.add(jMenu12);

        adminPage.setJMenuBar(jMenuBar12);

        javax.swing.GroupLayout adminPageLayout = new javax.swing.GroupLayout(adminPage.getContentPane());
        adminPage.getContentPane().setLayout(adminPageLayout);
        adminPageLayout.setHorizontalGroup(
            adminPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminPageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(adminPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .addComponent(jScrollPane9)
                    .addGroup(adminPageLayout.createSequentialGroup()
                        .addGroup(adminPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel28)
                            .addGroup(adminPageLayout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addGap(37, 37, 37)
                                .addComponent(jRadioButton11)
                                .addGap(34, 34, 34)
                                .addComponent(jRadioButton12))
                            .addGroup(adminPageLayout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField10)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(adminPageLayout.createSequentialGroup()
                .addGap(215, 215, 215)
                .addComponent(jButton24)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        adminPageLayout.setVerticalGroup(
            adminPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminPageLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(adminPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jRadioButton11)
                    .addComponent(jRadioButton12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(adminPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton24)
                .addGap(12, 12, 12))
        );

        jLabel31.setText("Reset password for:");

        jLabel32.setText("Enter New Password:");

        jLabel33.setText("Confirm new Password:");

        jButton25.setText("Go");

        jMenu13.setText("Nav");

        jMenuItem32.setText("Back");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        jMenu13.add(jMenuItem32);

        jMenuItem33.setText("Exit");
        jMenu13.add(jMenuItem33);

        jMenuItem34.setText("Sign Out");
        jMenu13.add(jMenuItem34);

        jMenuBar13.add(jMenu13);

        adminResetPassPage.setJMenuBar(jMenuBar13);

        javax.swing.GroupLayout adminResetPassPageLayout = new javax.swing.GroupLayout(adminResetPassPage.getContentPane());
        adminResetPassPage.getContentPane().setLayout(adminResetPassPageLayout);
        adminResetPassPageLayout.setHorizontalGroup(
            adminResetPassPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminResetPassPageLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(adminResetPassPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton25)
                    .addGroup(adminResetPassPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPasswordField7, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel33)
                        .addGroup(adminResetPassPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPasswordField6, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(jLabel32)
                            .addComponent(jLabel31)
                            .addComponent(jTextField11, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))))
                .addContainerGap(181, Short.MAX_VALUE))
        );
        adminResetPassPageLayout.setVerticalGroup(
            adminResetPassPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminResetPassPageLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPasswordField6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPasswordField7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(jButton25)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAutoRequestFocus(false);
        setEnabled(false);
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 386, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 322, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton6ActionPerformed

    }//GEN-LAST:event_jRadioButton6ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed

    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed

    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed

        if (jComboBox2.getSelectedIndex() == 0) {
            studentRegisterPage.setVisible(true);
        } else if (jComboBox2.getSelectedIndex() == 1) {
            teacherRegisterPage.setVisible(true);
        }
        redirectPage.dispose();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed

        loginPage.setVisible(true);
        redirectPage.dispose();
        if (jComboBox2.getSelectedIndex() == 0) {
            jRadioButton9.setSelected(true);
        } else if (jComboBox2.getSelectedIndex() == 1) {
            jRadioButton10.setSelected(true);
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

        redirectPage.setVisible(true);
        studentRegisterPage.dispose();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed

        redirectPage.setVisible(true);
        teacherRegisterPage.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed

        redirectPage.setVisible(true);
        loginPage.dispose();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed

        studentPreviousResultsPage.dispose();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed

    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed

    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed

    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed

    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed

    }//GEN-LAST:event_jMenuItem29ActionPerformed

    private void jMenuItem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem32ActionPerformed

    }//GEN-LAST:event_jMenuItem32ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

        System.exit(0);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed

        System.exit(0);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed

        System.exit(0);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed

        System.exit(0);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem35ActionPerformed

        studentPanelPage.setVisible(true);
        studentFinishTestPage.dispose();
    }//GEN-LAST:event_jMenuItem35ActionPerformed

    private void jMenuItem36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem36ActionPerformed

        logout();
        System.exit(0);
    }//GEN-LAST:event_jMenuItem36ActionPerformed

    private void jMenuItem37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem37ActionPerformed

        logout();
    }//GEN-LAST:event_jMenuItem37ActionPerformed
    private boolean validateRegName(String a, int type) {
        ResultSet rs;
        if (checkValidCharsUsed(a)) {
            if (a.length() >= 4) {
                try {
                    if (type == TYPE_STUDENT) {
                        rs = stmt.executeQuery("select name from student_auth where name=\"" + a + "\";");
                    } else {
                        rs = stmt.executeQuery("select name from teacher_auth where name=\"" + a + "\";");
                    }

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "That name is taken. Please try something else.", "Name in use", JOptionPane.ERROR_MESSAGE);
                    } else {
                        return true;
                    }
                } catch (SQLException ex) {
                    showSQLException("Error while checking for redundancy on registration.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Name must contain atleast 4 characters.", "Invalid Name", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Your Name cannot contain special characters or Numbers.", "Invalid Name", JOptionPane.WARNING_MESSAGE);
        }
        return false;
    }

    private BigInteger validatePassword(char[] a, char[] b, boolean check) {
        BigInteger big = null;
        boolean passwordsMatch = false;
        if (check) {
            passwordsMatch = Arrays.equals(a, b);
        } else {
            passwordsMatch = true;
        }
        if (passwordsMatch) {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(new String(a).getBytes(StandardCharsets.UTF_8));
                big = new BigInteger(1, hash);
                return big;
            } catch (NoSuchAlgorithmException ex) {
                showException("Problem importing Java Security libraries. SHA256 NOT FOUND.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Your passwords do not match.", "Password mismatch", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed

        ResultSet rs;

        boolean regNameCorrect = false, passwordCorrect = false;
        String regName = jTextField4.getText().trim();
        regNameCorrect = validateRegName(regName, 0);
        char[] password = jPasswordField2.getPassword();
        char[] passwordConfirm = jPasswordField3.getPassword();
        BigInteger big = validatePassword(password, passwordConfirm, true);
        if (big == null) {
            passwordCorrect = false;
        } else {
            passwordCorrect = true;
        }
        if (regNameCorrect && passwordCorrect) {
            try {
                stmt.executeUpdate("insert into student_auth values(\"" + regName + "\",\"" + big + "\",0);");
                stmt.executeUpdate("create table studentHistoryDatabase_" + regName + "(testid varchar(50), marksearned int(5), aborted int(1), datetaken timestamp);");
                JOptionPane.showMessageDialog(this, "Registration successful. You can log in now.", "Success", JOptionPane.INFORMATION_MESSAGE);
                logActivity(regName, "student registered");
                studentRegisterPage.dispose();
                loginPage.setVisible(true);
                jRadioButton9.setSelected(true);
            } catch (SQLException ex) {
                showSQLException("Error on creating user record");
                ex.printStackTrace();
            }
        }
        jPasswordField2.setText(null);
        jPasswordField3.setText(null);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed

        String regName = jTextField5.getText().trim();
        char[] password = jPasswordField4.getPassword();
        char[] passwordConfirm = jPasswordField5.getPassword();
        boolean regNameCorrect = validateRegName(regName, 1);
        boolean passwordCorrect = false;
        BigInteger big = validatePassword(password, passwordConfirm, true);
        if (big != null) {
            passwordCorrect = true;
        }
        String subject = (String) jComboBox3.getSelectedItem();
        if (regNameCorrect && passwordCorrect) {
            try {
                stmt.executeUpdate("insert into teacher_auth values (\"" + regName + "\",\"" + big + "\",\"" + subject + "\",0,0);");
                JOptionPane.showMessageDialog(this, "Your request has been submitted. Please have a system administrator review your request.", "Success", JOptionPane.INFORMATION_MESSAGE);
                logActivity(regName, "Teacher application submitted");
                teacherRegisterPage.dispose();
                loginPage.setVisible(true);
                jRadioButton10.setSelected(true);
            } catch (SQLException ex) {
                showSQLException("Error on creating user record");
                ex.printStackTrace();
            }
        }
        jPasswordField4.setText(null);
        jPasswordField5.setText(null);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        ResultSet rs;
        loginName = jTextField1.getText().trim();
        char[] loginPassword = jPasswordField1.getPassword();
        boolean loginSuccess = false;
        try {
            if (jRadioButton9.isSelected()) {
                PRESENTUSERTYPE = TYPE_STUDENT;
                rs = stmt.executeQuery("select * from student_auth where name=\"" + loginName + "\";");
                if (rs.next()) {
                    String digest = rs.getString("password");
                    if ((validatePassword(loginPassword, null, false)).toString().equals(digest)) {
                        loginSuccess = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Your password is incorrect. If you've forgotten your password, contact system administrator for assistance.", "Invalid details", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "That account doesn't exist. Please register.", "Invalid details", JOptionPane.ERROR_MESSAGE);
                }
            } else if (jRadioButton10.isSelected()) {
                PRESENTUSERTYPE = TYPE_TEACHER;
                rs = stmt.executeQuery("select * from teacher_auth where name=\"" + loginName + "\";");
                if (rs.next()) {
                    int privilege = rs.getInt("status");
                    if (privilege == 0) {
                        JOptionPane.showMessageDialog(this, "Your details are yet to be reviewed. Please contact system administrator", "Application in Review", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String digest = rs.getString("password");
                    if ((validatePassword(loginPassword, null, false)).toString().equals(digest)) {
                        loginSuccess = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Your password is incorrect. If you've forgotten your password, contact system administrator for assistance.", "Invalid details", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "That account doesn't exist. Please register.", "Invalid details", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (loginSuccess) {
                logActivity(loginName, "User logged in");
                wakeUpSeconds = 300;
                wakeUpTimer = new java.util.Timer();
                wakeUpTimerTask = new java.util.TimerTask() {
                    @Override
                    public void run() {
                        decreaseWakeUpTimer();
                    }
                };
                wakeUpTimer.scheduleAtFixedRate(wakeUpTimerTask, 1000, 1000);
                loginPage.dispose();
                if (isUserStudent()) {
                    stmt.executeUpdate("update student_auth set onlinestatus=1 where name=\"" + loginName + "\";");
                    jLabel5.setText("Welcome, " + loginName);
                    studentPanelPage.setVisible(true);
                    updateStudentTestList();
                } else {
                    stmt.executeUpdate("update teacher_auth set onlinestatus=1 where name=\"" + loginName + "\";");
                    jLabel20.setText("Welcome, " + loginName);
                    teacherPanelPage.setVisible(true);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showSQLException("Error while fetching login results");
        }
        jTextField1.setText(null);
        jPasswordField1.setText(null);
    }//GEN-LAST:event_jButton1ActionPerformed
    private void decreaseWakeUpTimer() {
        if (wakeUpSeconds == 1) {
            logout();
            JOptionPane.showMessageDialog(loginPage, "You have been inactive for the past few minutes and hence have been logged out for security concerns.", "Inactivity Detection", JOptionPane.INFORMATION_MESSAGE);
        } else {
            wakeUpSeconds--;
        }
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        updateStudentTestList();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed

        updateStudentTestList();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed

        logout();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        ResultSet rs;
        totalQuestions = 0;
        int marks = 0;
        try {
            String testid = (String) jTable1.getValueAt(jTable1.getSelectedRow(), 0);
            if (((String) jTable1.getValueAt(jTable1.getSelectedRow(), 2)).equals("Locked")) {
                JOptionPane.showMessageDialog(studentPanelPage, "That test is locked.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            } else if (((String) jTable1.getValueAt(jTable1.getSelectedRow(), 2)).equals("Taken")) {
                JOptionPane.showMessageDialog(studentPanelPage, "You've already taken this test.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            } else {
                int answer = JOptionPane.showConfirmDialog(studentPanelPage, "Are you sure? You cannot retake this test once started.", "Start confirmation", JOptionPane.YES_NO_OPTION);

                if (answer == 0) {
                    stmt.executeUpdate("insert into studenthistorydatabase_" + loginName + "(testid,marksearned,datetaken,aborted) values (\"" + testid + "\",0,now(),0);");
                    studentPanelPage.dispose();
                    currentTestID = testid;
                    jLabel38.setText((String) jTable1.getValueAt(jTable1.getSelectedRow(), 3) + " - " + (String) jTable1.getValueAt(jTable1.getSelectedRow(), 1));
                    rs = stmt.executeQuery("select points from testlist where testid=\"" + testid + "\";");
                    if (rs.next()) {
                        marks = rs.getInt(1);
                    }
                    jLabel39.setText("Marks per Question: " + marks);
                    studentQuestionPage.setVisible(true);
                    rs = stmt.executeQuery("select seconds from testlist where testid=\"" + testid + "\";");
                    if (rs.next()) {
                        testCountdown = rs.getInt(1);
                    }
                    testTimerTask = new java.util.TimerTask() {
                        public void run() {
                            if (testCountdown == 1) {
                                JOptionPane.showMessageDialog(studentQuestionPage, "Oops! Time up!", "Time up", JOptionPane.INFORMATION_MESSAGE);
                                finishTest();
                            }
                            testCountdown--;
                            int min = testCountdown / 60;
                            int sec = testCountdown % 60;
                            jTextField2.setText(min + ":" + sec);
                        }
                    };
                    wakeUpTimer.scheduleAtFixedRate(testTimerTask, 1000, 1000);
                    rs = stmt.executeQuery("select count(*) from testquestions_" + testid);
                    if (rs.next()) {
                        totalQuestions = rs.getInt(1);
                    }
                    jTextField14.setText(Integer.toString(totalQuestions));
                    currentTestID = testid;
                    initiateTest(testid);
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(studentPanelPage, "You need to select a test.", "No test selected", JOptionPane.WARNING_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();

        }
    }//GEN-LAST:event_jButton3ActionPerformed
    private void finishTest() {
        studentQuestionPage.dispose();
        studentFinishTestPage.setVisible(true);
        ArrayList<String> finalAnswersList = new ArrayList<String>();
        ResultSet rs;
        int correctAnswers = 0, wrongAnswers = 0, marks = 1;
        try {
            rs = stmt.executeQuery("select points from testlist where testid=\"" + currentTestID + "\";");
            if (rs.next()) {
                marks = rs.getInt(1);
            }
            DefaultTableModel resultsModel = (DefaultTableModel) jTable3.getModel();
            resultsModel.setRowCount(0);
            rs = stmt.executeQuery("select answer from testquestions_" + currentTestID + " ;");
            while (rs.next()) {
                finalAnswersList.add(rs.getString(1));
            }

            for (String x : questionList) {
                String xTokens[] = x.split(separator);
                int index = Integer.parseInt(xTokens[0]);
                String question = xTokens[1];
                rs = stmt.executeQuery("select question_" + index + " from studenthistorydatabase_" + loginName + " where testid=\"" + currentTestID + "\";");
                if (rs.next()) {
                    String selectedAnswer = rs.getString(1);
                    if (selectedAnswer.equals("x")) {
                        selectedAnswer = "Not Attempted";
                    }
                    String correctAnswer = finalAnswersList.get(index - 1);
                    resultsModel.addRow(new Object[]{question, correctAnswer, selectedAnswer});
                    if (correctAnswer.equals(selectedAnswer)) {
                        correctAnswers++;
                    } else {
                        wrongAnswers++;
                    }
                }
            }
            jLabel49.setText("Correct Answers: " + correctAnswers);
            jLabel50.setText("Wrong Answers: " + wrongAnswers);
            jTextField6.setText(Integer.toString(correctAnswers * marks));
            stmt.executeUpdate("update studenthistorydatabase_" + loginName + " set marksearned=" + Integer.toString(correctAnswers * marks) + " where testid=\"" + currentTestID + "\";");
            jTextField7.setText(Integer.toString(questionList.size() * marks));
        } catch (SQLException ex) {
            ex.printStackTrace();
            showSQLException("Error occured while displaying results");
        }
    }
    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed

        if (!abortTest()) {
            return;
        }
        logout();
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed

        if (!abortTest()) {
            return;
        }
        logout();
        System.exit(0);
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed

        abortTest();
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        if (Integer.parseInt(jTextField13.getText()) == 1) {
            JOptionPane.showMessageDialog(studentPanelPage, "You have no more questions before this one. \nYou can do any of the following: \n1) Answer Flagged questions (if any)\n2) Click Next.\n3) Click Finish Test.", "No Previous Questions", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        curQuesInd--;
        setNextQuestion(curQuesInd);

    }//GEN-LAST:event_jButton5ActionPerformed
    private void updateQuestionProgress() {
        ResultSet rs;
        jLabel41.setText("Total Answered: " + totalAnsweredQuestions);
        jLabel43.setText("Total Unanswered: " + Integer.toString(totalQuestions - totalAnsweredQuestions));
        try {
            rs = stmt.executeQuery("select question_" + getQuestionIndex(curQuesInd) + " from studenthistorydatabase_" + loginName + " where testid=\"" + currentTestID + "\";");
            if (rs.next()) {
                String ans = rs.getString(1);
                if (ans.equals("a")) {
                    jRadioButton1.setSelected(true);
                } else if (ans.equals("b")) {
                    jRadioButton2.setSelected(true);
                } else if (ans.equals("c")) {
                    jRadioButton3.setSelected(true);
                } else if (ans.equals("d")) {
                    jRadioButton4.setSelected(true);
                } else if (ans.equals("x")) {
                    buttonGroup1.clearSelection();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showSQLException("Error occured while checking if already answered to update radio");
        }
    }
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        if (Integer.parseInt(jTextField13.getText()) == Integer.parseInt(jTextField14.getText())) {
            JOptionPane.showMessageDialog(studentPanelPage, "You have no more questions left. \nYou can do any of the following: \n1) Answer Flagged questions (if any)\n2) See previous questions\n3) Click Finish test.", "Reached end of test", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        curQuesInd++;
        setNextQuestion(curQuesInd);

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed

        updateAnswer("d");
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed

        updateAnswer("c");
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed

        updateAnswer("b");
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed

        updateAnswer("a");
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed

        DefaultListModel flaggedListModel = (DefaultListModel) jList2.getModel();
        if (flaggedListModel.removeElement("Question " + Integer.toString(curQuesInd + 1))) {
            totalFlagged--;
        };
        jLabel42.setText("Total Flagged: " + totalFlagged);
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        DefaultListModel flagListModel = (DefaultListModel) jList2.getModel();
        if (!flagListModel.contains("Question " + Integer.toString(curQuesInd + 1))) {
            flagListModel.addElement("Question " + Integer.toString(curQuesInd + 1));
            totalFlagged++;
        }
        jLabel42.setText("Total Flagged: " + totalFlagged);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTable8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable8MouseClicked

        if (evt.getClickCount() == 2) {
            String selLine = (String) (jTable8.getValueAt(jTable8.getSelectedRow(), 0));
            String selLineTokens[] = selLine.split(" ");
            curQuesInd = Integer.parseInt(selLineTokens[1]) - 1;
            setNextQuestion(curQuesInd);
        }
    }//GEN-LAST:event_jTable8MouseClicked

    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseClicked

        if (evt.getClickCount() == 2) {
            totalFlagged++;
            String selLine = (String) jList2.getSelectedValue();
            String selLineTokens[] = selLine.split(" ");
            curQuesInd = Integer.parseInt(selLineTokens[1]) - 1;
            setNextQuestion(curQuesInd);
        }
    }//GEN-LAST:event_jList2MouseClicked

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed

        int result = JOptionPane.showConfirmDialog(studentQuestionPage, "Are you sure you want to finish? You will not be able to re-take this test.", "Finish Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            finishTest();
        }
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked

        if (evt.getClickCount() == 2) {
            JOptionPane.showMessageDialog(studentFinishTestPage, jTable3.getValueAt(jTable3.getSelectedRow(), 0), "View Question", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed

        studentFinishTestPage.dispose();
        studentPanelPage.setVisible(true);
        cleanUpAfterTest();
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed

        ResultSet rs, rs2;
        DefaultTableModel prevResultsModel = (DefaultTableModel) jTable4.getModel();
        prevResultsModel.setRowCount(0);
        try {
            rs = stmt.executeQuery("select * from studenthistorydatabase_" + loginName + ";");
            while (rs.next()) {
                String testid = rs.getString("testid");
                rs2 = stmt2.executeQuery("select description,subject from testlist where testid=\"" + testid + "\";");
                if (rs2.next()) {
                    prevResultsModel.addRow(new Object[]{rs2.getString(2), rs2.getString(1), Integer.toString(rs.getInt("marksearned"))});
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showSQLException("Error occured while displaying previous resuts");
        }
        studentPreviousResultsPage.setVisible(true);
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed

        studentPreviousResultsPage.dispose();
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        // TODO add your handling code here:
        logout();
    }//GEN-LAST:event_jMenuItem13ActionPerformed
    private void cleanUpAfterTest() {
        updateStudentTestList();
        testTimerTask.cancel();
        questionList.clear();
        answeredList.clear();
        questionListMod.clear();
        DefaultTableModel cleanModel;
        DefaultListModel cleanListModel = (DefaultListModel) jList2.getModel();
        cleanListModel.setSize(0);
        cleanModel = (DefaultTableModel) jTable8.getModel();
        jTextField2.setText(null);
        buttonGroup1.clearSelection();
        cleanModel.setRowCount(0);
        currentTestID = null;
        curQuesInd = 0;
        totalAnsweredQuestions = 0;
        totalQuestions = 0;
        totalFlagged = 0;
        testCountdown = 0;
    }

    private void updateAnswer(String answer) {
        ResultSet rs;
        boolean alreadyAnswered = false;
        jTable8.setValueAt("Yes", curQuesInd, 1);
        try {
            for (Integer x : answeredList) {
                if (x == curQuesInd) {
                    alreadyAnswered = true;
                    break;
                }
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        if (!alreadyAnswered) {
            answeredList.add(curQuesInd);
            totalAnsweredQuestions++;
        }
        String temp[] = null;
        try {
            String questionLine = questionListMod.get(curQuesInd);
            temp = questionLine.split(separator);
            int dataIndex = Integer.parseInt(temp[1]);
            rs = stmt.executeQuery("select question_" + temp[1] + " from studenthistorydatabase_" + loginName + " where testid=\"" + currentTestID + "\";");
            stmt.executeUpdate("update studenthistorydatabase_" + loginName + " set question_" + temp[1] + " = \"" + answer + "\" where testid=\"" + currentTestID + "\";");

        } catch (SQLException ex) {
            try {

                stmt.executeUpdate("alter table studenthistorydatabase_" + loginName + " add column question_" + temp[1] + " varchar(1) default \"x\";");
                updateAnswer(answer);
                return;
            } catch (SQLException ex1) {
                showSQLException("Error occured while creating column for answer");
            }
            showSQLException("Error occured while updating answer");
            ex.printStackTrace();
        }
    }

    private int getQuestionIndex(int a) {

        String questionLine = questionListMod.get(a);
        String questionLineTokens[] = questionLine.split(separator);
        return Integer.parseInt(questionLineTokens[1]);
    }

    private void initiateTest(String testid) {
        ResultSet rs;

        try {
            rs = stmt.executeQuery("select * from testquestions_" + testid + ";");
            while (rs.next()) {
                questionList.add(Integer.toString(rs.getInt("sno")) + separator + rs.getString("question") + separator + rs.getString("answer") + separator + Integer.toString(rs.getInt("reserve")));
            }
            Collections.shuffle(questionList);
            int i = 1;
            for (String temp : questionList) {
                temp = Integer.toString(i) + separator + temp;
                questionListMod.add(temp);
                i++;
            }
            questionList.get(0);
            int listSize = questionListMod.size();
            DefaultTableModel questionListModel = (DefaultTableModel) jTable8.getModel();
            questionListModel.setRowCount(0);
            for (int j = 1; j <= listSize; j++) {
                questionListModel.addRow(new Object[]{"Question " + j, "No"});
            }

            curQuesInd = 0;
            setNextQuestion(curQuesInd);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showSQLException("Error occured whilie initiating test");
        }
    }

    private void setNextQuestion(int i) {

        String questionLine = questionListMod.get(i);
        String[] tempTokens = questionLine.split(separator);
        jTextArea1.setText(tempTokens[2]);
        jTextField13.setText(tempTokens[0]);
        updateQuestionProgress();

    }

    private void logActivity(String username, String event) {
        java.util.Date datetime = new java.util.Date();
        SimpleDateFormat format = null;
        try {
            format = new SimpleDateFormat("yyyyMMddHHmmss");
            stmt.executeUpdate("insert into activitylog values (\"" + username + "\", \"" + event + "\",\"" + format.format(new java.util.Date()) + "\");");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showSQLException("Failed Activity logging");
        }
    }

    private void updateStudentTestList() {
        ResultSet rs, rs2;
        int pendingTotal = 0;
        String selectedSubject = null, statusRep = "Error";
        String subject = null;
        try {
            rs = stmt.executeQuery("select count(*) from testlist where status=1");
            if (rs.next()) {
                pendingTotal = rs.getInt(1);
            }
            if (jComboBox1.getSelectedIndex() == 0) {
                rs = stmt.executeQuery("select * from testlist order by status desc");
            } else {
                selectedSubject = (String) (jComboBox1.getSelectedItem());
                rs = stmt.executeQuery("select * from testlist where subject=\"" + selectedSubject + "\" order by status desc;");
            }
            DefaultTableModel studentTestTableModel = (DefaultTableModel) (jTable1.getModel());
            studentTestTableModel.setRowCount(0);
            while (rs.next()) {
                String testid = rs.getString("testid");
                String testName = rs.getString("description");
                int statusNumber = rs.getInt("status");
                switch (statusNumber) {
                    case 0:
                        statusRep = "Locked";
                        break;
                    case 1:
                        statusRep = "Active";
                        break;
                }
                rs2 = stmt2.executeQuery("select * from studenthistorydatabase_" + loginName + " where testid=\"" + testid + "\";");
                if (rs2.next()) {
                    statusRep = "Taken";
                    pendingTotal--;
                }

                rs2 = stmt2.executeQuery("select * from testlist where testid=\"" + testid + "\";");
                if (rs2.next()) {
                    subject = rs2.getString("subject");
                }
                studentTestTableModel.addRow(new Object[]{testid, testName, statusRep, subject});
                jTextField12.setText(Integer.toString(pendingTotal));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showSQLException("Error while fetching student test list");
        }
    }

    private boolean checkValidCharsUsed(String a) {
        char tempChar;
        for (int i = 0; i < a.length(); i++) {
            tempChar = a.charAt(i);
            if ((int) tempChar > 32 && (int) tempChar < 65) {
                if ((int) tempChar == 32 || (int) tempChar == 46) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    private int getUserType() {
        return PRESENTUSERTYPE;
    }

    private boolean isUserStudent() {
        if (PRESENTUSERTYPE == TYPE_STUDENT) {
            return true;
        }
        return false;
    }

    private boolean isUserTeacher() {
        if (PRESENTUSERTYPE != TYPE_STUDENT) {
            return true;
        }
        return false;
    }

    private boolean abortTest() {
        int result = JOptionPane.showConfirmDialog(studentQuestionPage, "Are you sure you want to abort test? You will not be able to re-take this test.", "Abort confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            try {
                stmt.executeUpdate("update studenthistorydatabase_" + loginName + " set aborted=1 where testid=\"" + currentTestID + "\";");
                studentQuestionPage.dispose();
                cleanUpAfterTest();
                studentPanelPage.setVisible(true);
                return true;
            } catch (SQLException ex) {
                ex.printStackTrace();
                showSQLException("Error occured while aborting");
            }
        }
        return false;
    }

    private void showSQLException(String a) {
        System.out.println(a);
    }

    private void logout() {
        try {
            if (isUserStudent()) {
                stmt.executeUpdate("update student_auth set onlinestatus=0 where name=\"" + loginName + "\";");
            } else if (isUserTeacher()) {
                stmt.executeUpdate("update teacher_auth set onlinestatus=0 where name=\"" + loginName + "\";");
            }
            disposeAllFrames();
            loginPage.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showSQLException("Error occured while signing user out");
        }
        wakeUpTimer.cancel();
        logActivity(loginName, "User logged out");
    }

    private void showException(String a) {

    }

    private void disposeAllFrames() {
        adminPage.dispose();
        adminResetPassPage.dispose();
        loginPage.dispose();
        questionUploadPage.dispose();
        redirectPage.dispose();
        studentFinishTestPage.dispose();
        studentPanelPage.dispose();
        studentPreviousResultsPage.dispose();
        studentQuestionPage.dispose();
        studentRegisterPage.dispose();
        teacherPanelPage.dispose();
        teacherQuestionPage.dispose();
        teacherRegisterPage.dispose();
        teacherTestReportPage.dispose();
    }

    public static void main(String args[]) {
        try {
            /*for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
             if ("Nimbus".equals(info.getName())) {
             javax.swing.UIManager.setLookAndFeel(info.getClassName());
             break;

             }
             }*/
            UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClassTest.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClassTest.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClassTest.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClassTest.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClassTest();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFrame adminPage;
    private javax.swing.JFrame adminResetPassPage;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu12;
    private javax.swing.JMenu jMenu13;
    private javax.swing.JMenu jMenu14;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar10;
    private javax.swing.JMenuBar jMenuBar11;
    private javax.swing.JMenuBar jMenuBar12;
    private javax.swing.JMenuBar jMenuBar13;
    private javax.swing.JMenuBar jMenuBar14;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JMenuBar jMenuBar4;
    private javax.swing.JMenuBar jMenuBar5;
    private javax.swing.JMenuBar jMenuBar6;
    private javax.swing.JMenuBar jMenuBar7;
    private javax.swing.JMenuBar jMenuBar8;
    private javax.swing.JMenuBar jMenuBar9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem35;
    private javax.swing.JMenuItem jMenuItem36;
    private javax.swing.JMenuItem jMenuItem37;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JPasswordField jPasswordField3;
    private javax.swing.JPasswordField jPasswordField4;
    private javax.swing.JPasswordField jPasswordField5;
    private javax.swing.JPasswordField jPasswordField6;
    private javax.swing.JPasswordField jPasswordField7;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton12;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JFrame loginPage;
    private javax.swing.JFrame questionUploadPage;
    private javax.swing.JFrame redirectPage;
    private javax.swing.JFrame studentFinishTestPage;
    private javax.swing.JFrame studentPanelPage;
    private javax.swing.JFrame studentPreviousResultsPage;
    private javax.swing.JFrame studentQuestionPage;
    private javax.swing.JFrame studentRegisterPage;
    private javax.swing.JFrame teacherPanelPage;
    private javax.swing.JFrame teacherQuestionPage;
    private javax.swing.JFrame teacherRegisterPage;
    private javax.swing.JFrame teacherTestReportPage;
    // End of variables declaration//GEN-END:variables
}
