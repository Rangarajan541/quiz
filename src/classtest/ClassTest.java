/*
 * @author Rangarajan.A
 */
package classtest;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class ClassTest extends javax.swing.JFrame {

    //USER VARIABLES DECLARATION    
    private String currentTestID = null, loginID = null, loginName = null;
    private int loginStandard = -1;
    private final int TYPE_TEACHER = 1, TYPE_STUDENT = 0;
    //USER VARIABLES DECLARATION END

    //SYSTEM VARIABLES DECLARATION    
    private ArrayList<JFrame> allWindowList = new ArrayList<>();
    private ArrayList<String> questionList = new ArrayList<>();
    private ArrayList<String> questionListMod = new ArrayList<>();
    private ArrayList<Integer> answeredList = new ArrayList<>();
    private Connection con;
    private Statement stmt, stmt2;
    private int totalCheatSeconds = 30, totalAllowedCheats = 5, savedWakeUpSetting = 300, flashWarning = 60;
    private int PRESENTUSERTYPE = -1, wakeUpSeconds = 300, curQuesInd = 0, totalAnsweredQuestions = 0, totalQuestions = 0;
    private int totalFlagged = 0, testCountdown = 0, curEdit = 0, acSeconds = totalCheatSeconds, acCount = 0, issuedWarnings = 0;
    private int registrationsAllowed = 0;
    private final String separator = "==InternalSeparator==";
    private boolean isTestInProgress = false, canCheat = true, red = true, imageDisplayed = false;
    private static boolean correctKeyEntered = false, resetKeyEntered = false, teacherKeyEntered = false;
    private String logLocation, resLocation, programDataLocation;
    private int instantCheatAlarm = 0, currentAlarmIndex = 0;
    //SYSTEM VARIABLES DECLARATION END

    //TIMERS
    private java.util.Timer wakeUpTimer;
    private java.util.TimerTask wakeUpTimerTask;
    private java.util.TimerTask testTimerTask;
    private java.util.TimerTask antiCheatTask;
    private java.util.TimerTask instantCheatAlarmTask;
    //TIMERS END

    //Constructor beginning
    private ClassTest() {
        //Getting the system ProgramData location for default storage
        programDataLocation = System.getenv("PROGRAMDATA").replace('\\', '/');
        resLocation = programDataLocation + "/Quiz/Resources";
        logLocation = programDataLocation + "/Quiz/Error Log.txt";
        initComponents();

        if (correctKeyEntered) { //if db authentication is started, this starts
            initDatabaseSettingsWizard();
        } else {
            redirectPage.setVisible(true);
            try { //Decrpyting password, and using the same for connectivity
                FileReader fr = new FileReader(new File(programDataLocation + "/Quiz/key.txt"));
                BufferedReader br = new BufferedReader(fr);
                String dummy[] = br.readLine().split(";");
                String acc = dummy[0];
                String pass = dummy[1];
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz", decrypt(acc), decrypt(pass));
                stmt = con.createStatement();
                stmt2 = con.createStatement();

            } catch (ClassNotFoundException | SQLException | IOException | ArrayIndexOutOfBoundsException | NullPointerException ex) {
                logError("Error occured while establishing database link", ex);
                JOptionPane.showMessageDialog(null, "Error occured while establishing database link.\nPlease try re-running Authentication or contact an administrator.", "Data connectivity error", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
            if (resetKeyEntered) { //for full database reset
                resetAllData();
            }
            if (teacherKeyEntered) { //for approving teachers from outside the program
                try {
                    ResultSet rs = stmt.executeQuery("select count(*) from teacher_auth where status=0");
                    int count = -1;
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                    int val = JOptionPane.showConfirmDialog(null, "Are you sure you want to approve all teachers?\nThis gives them all administrator priveleges.\n\nNote: There are currently " + count + " teachers awaiting approval.", "Confirm Action", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (val == JOptionPane.YES_OPTION) {
                        stmt.executeUpdate("update teacher_auth set status=1;");
                        JOptionPane.showMessageDialog(null, "All teachers approved", "Action successful.", JOptionPane.INFORMATION_MESSAGE);
                    }
                    System.exit(0);
                } catch (SQLException ex) {
                    showException("Error occured while auto-approving teacher applications.", ex);
                }
            }
            fetchSystemParameters();
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
                    canCheat = false;
                    abortTest();
                }
            };
            MouseListener wakeUpListenerMouse = new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    resetWakeUpTimer();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    resetWakeUpTimer();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    resetWakeUpTimer();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    resetWakeUpTimer();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    resetWakeUpTimer();
                }
            };
            KeyListener wakeUpListenerKey = new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    resetWakeUpTimer();
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    resetWakeUpTimer();
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    resetWakeUpTimer();
                }
            };
            FocusListener wakeUpListenerFocus = new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    resetWakeUpTimer();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    resetWakeUpTimer();
                }
            };
            WindowFocusListener antiCheatListener = new WindowFocusListener() {
                @Override
                public void windowGainedFocus(WindowEvent e) {
                    resetCheatMeasures();
                    canCheat = true;
                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    if (!imageDisplayed && canCheat) {
                        preventCheating();
                    }
                }
            };
            WindowAdapter imageAdapter = new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    imageDisplayed = false;
                }
            };
            redirectPage.setTitle("Welcome");
            redirectPage.pack();
            redirectPage.setLocationRelativeTo(null);
            redirectPage.setResizable(false);
            redirectPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            studentRegisterPage.setTitle("Student Registration");
            studentRegisterPage.pack();
            studentRegisterPage.setLocationRelativeTo(null);
            studentRegisterPage.setResizable(false);
            studentRegisterPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            teacherRegisterPage.setTitle("Teacher Registration");
            teacherRegisterPage.pack();
            teacherRegisterPage.setLocationRelativeTo(null);
            teacherRegisterPage.setResizable(false);
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
            studentQuestionPage.addWindowFocusListener(antiCheatListener);
            studentQuestionPage.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            studentFinishTestPage.setTitle("Test Results");
            studentFinishTestPage.setResizable(false);
            studentFinishTestPage.pack();
            studentFinishTestPage.setLocationRelativeTo(null);
            studentFinishTestPage.addWindowListener(resultsCloseListener);
            studentFinishTestPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            studentPreviousResultsPage.setTitle("Previous Test Results");
            studentPreviousResultsPage.setResizable(false);
            studentPreviousResultsPage.pack();
            studentPreviousResultsPage.setLocationRelativeTo(studentPanelPage);
            studentPreviousResultsPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            teacherPanelPage.setTitle("Teacher Home");
            teacherPanelPage.setResizable(false);
            teacherPanelPage.pack();
            teacherPanelPage.setLocationRelativeTo(null);
            teacherPanelPage.addWindowListener(onCloseListener);
            teacherPanelPage.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            editQuestionPage.setTitle("Edit Test");
            editQuestionPage.setResizable(false);
            editQuestionPage.pack();
            editQuestionPage.setLocationRelativeTo(null);
            editQuestionPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            editQuestionBridge.setTitle("Edit question");
            editQuestionBridge.setResizable(false);
            editQuestionBridge.pack();
            editQuestionBridge.setLocationRelativeTo(editQuestionPage);
            editQuestionBridge.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            teacherQuestionPage.setTitle("Create Test");
            teacherQuestionPage.setResizable(false);
            teacherQuestionPage.pack();
            teacherQuestionPage.setLocationRelativeTo(teacherPanelPage);
            teacherQuestionPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            antiCheatFrame.setTitle("Anti Cheat System");
            antiCheatFrame.setResizable(false);
            antiCheatFrame.pack();
            antiCheatFrame.setLocationRelativeTo(null);
            antiCheatFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            teacherTestReportPage.setTitle("Test Report");
            teacherTestReportPage.setResizable(false);
            teacherTestReportPage.pack();
            teacherTestReportPage.setLocationRelativeTo(teacherPanelPage);
            teacherTestReportPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            logPage.setTitle("Administrator settings");
            logPage.setResizable(false);
            logPage.pack();
            logPage.setLocationRelativeTo(teacherPanelPage);
            logPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            adminPage.setTitle("Administrator settings");
            adminPage.setResizable(false);
            adminPage.pack();
            adminPage.setLocationRelativeTo(teacherPanelPage);
            adminPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            userHistoryPage.setTitle("Student history");
            userHistoryPage.setResizable(false);
            userHistoryPage.pack();
            userHistoryPage.setLocationRelativeTo(adminPage);
            userHistoryPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            imageDisplayPage.setTitle("Question image - You can resize this window");
            imageDisplayPage.setLocationRelativeTo(studentQuestionPage);
            imageDisplayPage.addWindowListener(imageAdapter);
            cheatAlarmPage.setTitle("Instant cheat alarm");
            cheatAlarmPage.pack();
            cheatAlarmPage.setLocationRelativeTo(null);
            cheatAlarmPage.setResizable(false);
            displayReportQuestionPage.setTitle("View Question");
            displayReportQuestionPage.pack();
            displayReportQuestionPage.setLocationRelativeTo(null);
            friendlyRetestPage.setTitle("Retest for students");
            friendlyRetestPage.setResizable(false);
            friendlyRetestPage.pack();
            friendlyRetestPage.setLocationRelativeTo(teacherPanelPage);
            friendlyRetestPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            allWindowList.add(logPage);
            allWindowList.add(adminPage);
            allWindowList.add(editQuestionBridge);
            allWindowList.add(editQuestionPage);
            allWindowList.add(loginPage);
            allWindowList.add(redirectPage);
            allWindowList.add(studentFinishTestPage);
            allWindowList.add(studentPanelPage);
            allWindowList.add(studentPreviousResultsPage);
            allWindowList.add(studentQuestionPage);
            allWindowList.add(studentRegisterPage);
            allWindowList.add(teacherPanelPage);
            allWindowList.add(teacherQuestionPage);
            allWindowList.add(teacherRegisterPage);
            allWindowList.add(teacherTestReportPage);
            allWindowList.add(userHistoryPage);
            allWindowList.add(imageDisplayPage);
            allWindowList.add(cheatAlarmPage);
            allWindowList.add(generateKeyPage);
            allWindowList.add(displayReportQuestionPage);
            allWindowList.add(friendlyRetestPage);
            for (JFrame x : allWindowList) {
                x.addFocusListener(wakeUpListenerFocus);
                x.addKeyListener(wakeUpListenerKey);
                x.addMouseListener(wakeUpListenerMouse);
            }
            generateYearModel();
        }
    }

    private void clearAuthFields() {
        jTextField33.setText(null);
        jTextField4.setText(null);
        jPasswordField3.setText(null);
        jPasswordField2.setText(null);
        jComboBox15.setSelectedIndex(0);
        jTextField34.setText(null);
        jTextField5.setText(null);
        jPasswordField4.setText(null);
        jPasswordField5.setText(null);
        jComboBox3.setSelectedIndex(0);
        jTextField1.setText(null);
        jPasswordField1.setText(null);
    }

    private void resetAllData() {
        try {
            int res = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete all data? This action cannot be undone.", "Confirm Reset?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                ResultSet rs = stmt.executeQuery("show tables");
                while (rs.next()) {
                    String tablename = (rs.getString(1));
                    if (tablename.equals("systemsettings")) {
                        resetDefaults();
                    } else if (tablename.startsWith("studenthistorydatabase_") || tablename.startsWith("testquestions_")) {
                        stmt2.executeUpdate("drop table " + tablename + ";");
                    } else {
                        stmt2.executeUpdate("delete from " + tablename + ";");
                    }
                }
                JOptionPane.showMessageDialog(null, "All data reset\n\nNote: Folder structure created earlier is unaffected.", "Action Successful", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            } else {
                System.exit(0);
            }
        } catch (SQLException ex) {
            logError("Unable to reset", ex);
            JOptionPane.showMessageDialog(null, "Unable to reset data. Please check Log.", "Action failed", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    private void resetDefaults() {
        try {
            String defLog = programDataLocation + "/Quiz/Error log.txt";
            String defRes = programDataLocation + "/Quiz/Resources/";
            stmt2.executeUpdate("update systemsettings set data=\"" + 10 + "\" where identifier=\"totalcheatseconds\";");
            stmt2.executeUpdate("update systemsettings set data=\"" + 2 + "\" where identifier=\"totalallowedwarnings\";");
            stmt2.executeUpdate("update systemsettings set data=\"" + 300 + "\" where identifier=\"wakeupseconds\";");
            stmt2.executeUpdate("update systemsettings set data=\"" + 60 + "\" where identifier=\"flashwarningseconds\";");
            stmt2.executeUpdate("update systemsettings set data=\"" + defLog + "\" where identifier=\"loglocation\";");
            stmt2.executeUpdate("update systemsettings set data=\"" + defRes + "\" where identifier=\"reslocation\";");
            stmt2.executeUpdate("update systemsettings set data=\"" + 1 + "\" where identifier=\"instantcheatalarm\";");
            stmt2.executeUpdate("update systemsettings set data=\"" + 1 + "\" where identifier=\"studentregistrationsallowed\";");
        } catch (SQLException ex) {
            logError("Unable to reset", ex);
            JOptionPane.showMessageDialog(null, "Unable to reset data. Please check Log.", "Action failed", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    @SuppressWarnings("unchecked")
    private void generateYearModel() {
        Calendar now = Calendar.getInstance();
        int x = now.get(Calendar.YEAR);
        String[] yearList = new String[x - 2015];
        for (int i = 0; i <= yearList.length - 1; i++) {
            yearList[i] = Integer.toString(2016 + i);
        }
        jComboBox7.setSelectedIndex(1);
        jComboBox14.setSelectedIndex(1);
        jComboBox10.setModel(new DefaultComboBoxModel(yearList));
        jComboBox12.setModel(new DefaultComboBoxModel(yearList));
        jComboBox10.setSelectedIndex(jComboBox10.getItemCount() - 1);
        jComboBox12.setSelectedIndex(jComboBox10.getItemCount() - 1);
        jComboBox8.setSelectedItem(Integer.toString(now.get(Calendar.DATE)));
        jComboBox13.setSelectedItem(Integer.toString(now.get(Calendar.DATE)));
        jComboBox9.setSelectedIndex(now.get(Calendar.MONTH));
        jComboBox11.setSelectedIndex(now.get(Calendar.MONTH));
    }

    private void resetCheatMeasures() {
        try {
            antiCheatTask.cancel();
        } catch (NullPointerException ex) {
        }
        antiCheatFrame.dispose();
        acSeconds = totalCheatSeconds;
    }

    private void preventCheating() {
        try {
            if (acCount == (totalAllowedCheats)) {
                antiCheatFrame.dispose();
                antiCheatTask.cancel();
                finishTest();
                JOptionPane.showMessageDialog(studentFinishTestPage, "Anti Cheat detection worked and the test locked.\nIf you think this was an error, contact system administrator.", "Test Locked", JOptionPane.ERROR_MESSAGE);
            } else {
                acCount++;
                antiCheatFrame.setVisible(true);
                jTextArea4.setText("Please go back to test window or test will lock within:\n\n" + acSeconds + " Seconds.\n\nThis is warning " + acCount + " of " + totalAllowedCheats);
                antiCheatTask = new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (acSeconds == 1) {
                            antiCheatFrame.dispose();
                            antiCheatTask.cancel();
                            finishTest();
                            JOptionPane.showMessageDialog(studentFinishTestPage, "Anti Cheat detection worked and the test locked.\nIf you think this was an error, contact system administrator.", "Test Locked", JOptionPane.ERROR_MESSAGE);
                        } else {
                            acSeconds--;
                            jTextArea4.setText("Please go back to test window or test will lock within:\n\n" + acSeconds + " Seconds.\n\nThis is warning " + acCount + " of " + totalAllowedCheats);
                        }
                    }
                };
                issuedWarnings++;
                logActivity(loginID, "Student issued cheat warnings.");
                try {
                    int curInd = 1;
                    ResultSet rs = stmt.executeQuery("select logno from instantcheatalarm order by logno desc;");
                    if (rs.next()) {
                        curInd = rs.getInt("logno") + 1;
                    }
                    stmt.executeUpdate("insert into instantcheatalarm values (" + curInd + ",\"" + currentTestID + "\",\"" + loginName + "\",now());");
                } catch (SQLException ex) {
                    logError("Error occured while alarming " + loginID + " for test", ex);
                }
                if (totalCheatSeconds != 0) {
                    wakeUpTimer.scheduleAtFixedRate(antiCheatTask, 1000, 1000);
                }
            }
        } catch (NullPointerException ex) {
        }
    }

    private void fetchSystemParameters() {
        try {
            ResultSet rs;
            rs = stmt.executeQuery("select * from systemsettings where identifier=\"totalcheatseconds\";");
            if (rs.next()) {
                totalCheatSeconds = Integer.parseInt(rs.getString("data"));
                jTextField11.setText(Integer.toString(totalCheatSeconds));
            }
            rs = stmt.executeQuery("select * from systemsettings where identifier=\"totalallowedwarnings\";");
            if (rs.next()) {
                totalAllowedCheats = Integer.parseInt(rs.getString("data"));
                jTextField25.setText(Integer.toString(totalAllowedCheats));
            }
            rs = stmt.executeQuery("select * from systemsettings where identifier=\"wakeupseconds\";");
            if (rs.next()) {
                savedWakeUpSetting = Integer.parseInt(rs.getString("data"));
                jTextField26.setText(Integer.toString(savedWakeUpSetting));
            }
            rs = stmt.executeQuery("select * from systemsettings where identifier=\"flashwarningseconds\";");
            if (rs.next()) {
                flashWarning = Integer.parseInt(rs.getString("data"));
                jTextField29.setText(Integer.toString(flashWarning));
            }
            rs = stmt.executeQuery("select * from systemsettings where identifier=\"loglocation\";");
            if (rs.next()) {
                logLocation = rs.getString("data");
                jTextField30.setText(logLocation);
            }
            rs = stmt.executeQuery("select * from systemsettings where identifier=\"reslocation\";");
            if (rs.next()) {
                resLocation = rs.getString("data");
                jTextField31.setText(resLocation);
                if (!new File(resLocation).exists()) {
                    new File(resLocation).mkdirs();
                }
            }
            rs = stmt.executeQuery("select * from systemsettings where identifier=\"instantcheatalarm\";");
            jRadioButton5.setSelected(true);
            if (rs.next()) {
                instantCheatAlarm = Integer.parseInt(rs.getString("data"));
                if (instantCheatAlarm == 1) {
                    jRadioButton6.setSelected(true);
                }
            }
            rs = stmt.executeQuery("select * from systemsettings where identifier=\"studentregistrationsallowed\";");
            jRadioButton8.setSelected(true);
            if (rs.next()) {
                registrationsAllowed = Integer.parseInt(rs.getString("data"));
                if (registrationsAllowed == 1) {
                    jRadioButton7.setSelected(true);
                }
            }
        } catch (SQLException ex) {
            showException("Error occured while loading parameters", ex);
        }
    }

    private void updateSystemParameters() {
        try {
            stmt.executeUpdate("update systemsettings set data=\"" + Integer.parseInt(jTextField11.getText().trim()) + "\" where identifier=\"totalcheatseconds\";");
            stmt.executeUpdate("update systemsettings set data=\"" + Integer.parseInt(jTextField25.getText().trim()) + "\" where identifier=\"totalallowedwarnings\";");
            stmt.executeUpdate("update systemsettings set data=\"" + Integer.parseInt(jTextField26.getText().trim()) + "\" where identifier=\"wakeupseconds\";");
            stmt.executeUpdate("update systemsettings set data=\"" + Integer.parseInt(jTextField29.getText().trim()) + "\" where identifier=\"flashwarningseconds\";");
            stmt.executeUpdate("update systemsettings set data=\"" + jTextField30.getText().trim() + "\" where identifier=\"loglocation\";");
            stmt.executeUpdate("update systemsettings set data=\"" + jTextField31.getText().trim() + "\" where identifier=\"reslocation\";");
            String alarmValue;
            if (jRadioButton6.isSelected()) {
                alarmValue = "1";
            } else {
                alarmValue = "0";
            }
            stmt.executeUpdate("update systemsettings set data=\"" + alarmValue + "\" where identifier=\"instantcheatalarm\";");
            String registrationValue;
            if (jRadioButton7.isSelected()) {
                registrationValue = "1";
            } else {
                registrationValue = "0";
            }
            stmt.executeUpdate("update systemsettings set data=\"" + registrationValue + "\" where identifier=\"studentregistrationsallowed\";");
            JOptionPane.showMessageDialog(adminPage, "Settings were successfully saved", "Action successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (NullPointerException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(adminPage, "Fields cannot be empty.\nSet value to 0 to disable the setting.", "Invalid parameters", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showException("Error occured while updating parameters", ex);
        } finally {
            fetchSystemParameters();
        }
    }

    private void resetWakeUpTimer() {
        wakeUpSeconds = savedWakeUpSetting;
    }

    private String decrypt(String a) {
        StringBuilder b = new StringBuilder("");
        for (int i = 0; i < a.length(); i++) {
            b.append((char) ((a.charAt(i)) + 3));
        }
        return b.toString();
    }

    private String getUsernameFromID(String userid, int type) {
        ResultSet rs = null;
        try {
            if (type == TYPE_TEACHER) {
                rs = stmt.executeQuery("select name from teacher_auth where userid=\"" + userid + "\";");
            } else if (type == TYPE_STUDENT) {
                rs = stmt.executeQuery("select name from student_auth where userid=\"" + userid + "\";");
            }
            if (rs != null) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException ex) {
            showException("An error occured while fetching name from ID", ex);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
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
        jLabel98 = new javax.swing.JLabel();
        jComboBox15 = new javax.swing.JComboBox<>();
        jLabel99 = new javax.swing.JLabel();
        jTextField33 = new javax.swing.JTextField();
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
        jTextField34 = new javax.swing.JTextField();
        jLabel100 = new javax.swing.JLabel();
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
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel84 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox();
        jButton51 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jButton19 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
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
        jButton56 = new javax.swing.JButton();
        jLabel87 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jLabel70 = new javax.swing.JLabel();
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
        jLabel76 = new javax.swing.JLabel();
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
        teacherPanelPage = new javax.swing.JFrame();
        jPanel15 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel56 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox();
        jButton30 = new javax.swing.JButton();
        jPanel32 = new javax.swing.JPanel();
        jButton15 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jLabel104 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jPanel33 = new javax.swing.JPanel();
        jButton12 = new javax.swing.JButton();
        jLabel105 = new javax.swing.JLabel();
        jButton14 = new javax.swing.JButton();
        jPanel34 = new javax.swing.JPanel();
        jLabel106 = new javax.swing.JLabel();
        jButton16 = new javax.swing.JButton();
        jButton64 = new javax.swing.JButton();
        jPanel35 = new javax.swing.JPanel();
        jButton39 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jMenuBar8 = new javax.swing.JMenuBar();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        teacherQuestionPage = new javax.swing.JFrame();
        jPanel17 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jLabel101 = new javax.swing.JLabel();
        jComboBox16 = new javax.swing.JComboBox<>();
        jPanel18 = new javax.swing.JPanel();
        jButton21 = new javax.swing.JButton();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuBar11 = new javax.swing.JMenuBar();
        jMenu11 = new javax.swing.JMenu();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        editQuestionPage = new javax.swing.JFrame();
        jPanel19 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jTextField16 = new javax.swing.JTextField();
        jTextField17 = new javax.swing.JTextField();
        jTextField18 = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jComboBox17 = new javax.swing.JComboBox<>();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable9 = new javax.swing.JTable();
        jLabel57 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jButton31 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton57 = new javax.swing.JButton();
        jMenuBar15 = new javax.swing.JMenuBar();
        jMenu15 = new javax.swing.JMenu();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem38 = new javax.swing.JMenuItem();
        jMenuItem39 = new javax.swing.JMenuItem();
        editQuestionBridge = new javax.swing.JFrame();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane13 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jButton32 = new javax.swing.JButton();
        jLabel67 = new javax.swing.JLabel();
        teacherTestReportPage = new javax.swing.JFrame();
        jPanel23 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jTextField21 = new javax.swing.JTextField();
        jTextField22 = new javax.swing.JTextField();
        jTextField23 = new javax.swing.JTextField();
        jButton63 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField20 = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        jTextField24 = new javax.swing.JTextField();
        jMenuBar10 = new javax.swing.JMenuBar();
        jMenu10 = new javax.swing.JMenu();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        logPage = new javax.swing.JFrame();
        jPanel24 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable10 = new javax.swing.JTable();
        jLabel75 = new javax.swing.JLabel();
        jButton24 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jComboBox7 = new javax.swing.JComboBox();
        jComboBox8 = new javax.swing.JComboBox();
        jComboBox9 = new javax.swing.JComboBox();
        jComboBox10 = new javax.swing.JComboBox();
        jComboBox11 = new javax.swing.JComboBox();
        jComboBox12 = new javax.swing.JComboBox();
        jComboBox13 = new javax.swing.JComboBox();
        jComboBox14 = new javax.swing.JComboBox();
        jButton52 = new javax.swing.JButton();
        jButton53 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jMenuBar12 = new javax.swing.JMenuBar();
        jMenu12 = new javax.swing.JMenu();
        jMenuItem29 = new javax.swing.JMenuItem();
        adminPage = new javax.swing.JFrame();
        jPanel25 = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        jTable11 = new javax.swing.JTable();
        jLabel33 = new javax.swing.JLabel();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jPanel37 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jComboBox5 = new javax.swing.JComboBox();
        jButton44 = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jPanel26 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jPasswordField6 = new javax.swing.JPasswordField();
        jPasswordField7 = new javax.swing.JPasswordField();
        jButton38 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        jButton46 = new javax.swing.JButton();
        jPanel27 = new javax.swing.JPanel();
        jButton23 = new javax.swing.JButton();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jTextField25 = new javax.swing.JTextField();
        jTextField26 = new javax.swing.JTextField();
        jButton47 = new javax.swing.JButton();
        jLabel80 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jTextField29 = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        jButton54 = new javax.swing.JButton();
        jTextField30 = new javax.swing.JTextField();
        jButton55 = new javax.swing.JButton();
        jLabel88 = new javax.swing.JLabel();
        jButton58 = new javax.swing.JButton();
        jTextField31 = new javax.swing.JTextField();
        jLabel90 = new javax.swing.JLabel();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jLabel91 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jButton69 = new javax.swing.JButton();
        jMenuBar13 = new javax.swing.JMenuBar();
        jMenu13 = new javax.swing.JMenu();
        jMenuItem32 = new javax.swing.JMenuItem();
        jMenuItem34 = new javax.swing.JMenuItem();
        jMenuItem33 = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        antiCheatFrame = new javax.swing.JFrame();
        jPanel22 = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jButton36 = new javax.swing.JButton();
        userHistoryPage = new javax.swing.JFrame();
        jPanel28 = new javax.swing.JPanel();
        jScrollPane16 = new javax.swing.JScrollPane();
        jTable12 = new javax.swing.JTable();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        jTable13 = new javax.swing.JTable();
        jButton48 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        jTextField27 = new javax.swing.JTextField();
        jButton50 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel83 = new javax.swing.JLabel();
        jTextField28 = new javax.swing.JTextField();
        jMenuBar16 = new javax.swing.JMenuBar();
        jMenu16 = new javax.swing.JMenu();
        jMenuItem40 = new javax.swing.JMenuItem();
        imageDisplayPage = new javax.swing.JFrame();
        jScrollPane18 = new javax.swing.JScrollPane();
        jLabel89 = new javax.swing.JLabel();
        cheatAlarmPage = new javax.swing.JFrame();
        jPanel29 = new javax.swing.JPanel();
        jScrollPane19 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton59 = new javax.swing.JButton();
        jButton60 = new javax.swing.JButton();
        jLabel92 = new javax.swing.JLabel();
        generateKeyPage = new javax.swing.JFrame();
        jPanel30 = new javax.swing.JPanel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jTextField32 = new javax.swing.JTextField();
        jPasswordField8 = new javax.swing.JPasswordField();
        jButton61 = new javax.swing.JButton();
        displayReportQuestionPage = new javax.swing.JFrame();
        jPanel31 = new javax.swing.JPanel();
        jScrollPane20 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jButton62 = new javax.swing.JButton();
        friendlyRetestPage = new javax.swing.JFrame();
        jPanel36 = new javax.swing.JPanel();
        jLabel107 = new javax.swing.JLabel();
        jTextField35 = new javax.swing.JTextField();
        jScrollPane21 = new javax.swing.JScrollPane();
        jTable14 = new javax.swing.JTable();
        jScrollPane22 = new javax.swing.JScrollPane();
        jTable15 = new javax.swing.JTable();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jTextField36 = new javax.swing.JTextField();
        jButton65 = new javax.swing.JButton();
        jButton66 = new javax.swing.JButton();
        jButton67 = new javax.swing.JButton();
        jButton68 = new javax.swing.JButton();
        jMenuBar17 = new javax.swing.JMenuBar();
        jMenu17 = new javax.swing.JMenu();
        jMenuItem20 = new javax.swing.JMenuItem();

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel51.setFont(new java.awt.Font("Walkway SemiBold", 1, 48)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(0, 0, 153));
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel51.setText("MAHATMA CBSE");

        jLabel8.setFont(new java.awt.Font("Walkway SemiBold", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(204, 0, 51));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("ONLINE TEST SYSTEM");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(122, 122, 122))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addGap(197, 197, 197))))
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

        jLabel13.setFont(new java.awt.Font("Walkway SemiBold", 0, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(240, 0, 0));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("STUDENT REGISTRATION FORM");

        jButton10.setText("Register");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabel98.setText("Standard:");

        jComboBox15.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        jLabel99.setText("User ID");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel13))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel99)
                            .addComponent(jPasswordField3, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(jLabel98)
                            .addComponent(jLabel12)
                            .addComponent(jPasswordField2, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(jLabel11)
                            .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(jLabel10)
                            .addComponent(jComboBox15, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField33, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addComponent(jButton10)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel99)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField33, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPasswordField3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel98)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox15, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton10)
                .addContainerGap())
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

        jLabel17.setFont(new java.awt.Font("Walkway SemiBold", 0, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(240, 0, 0));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("TEACHER REGISTRATION FORM");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Informatics Practices", "Economics", "Business Studies", "Accountancy", "English", "Physics", "Chemistry", "Biology", "Mathematics", "History", "Geography", "Civics" }));

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

        jLabel100.setText("User ID:");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addContainerGap(25, Short.MAX_VALUE)
                        .addComponent(jLabel17))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(99, 99, 99)
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
                            .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel100))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(150, 150, 150)
                .addComponent(jButton11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel100)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
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
                .addContainerGap())
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        teacherRegisterPageLayout.setVerticalGroup(
            teacherRegisterPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherRegisterPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Walkway SemiBold", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("LOGIN");

        jLabel2.setText("User ID");

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

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGap(132, 132, 132)
                                .addComponent(jButton1)))
                        .addGap(0, 69, Short.MAX_VALUE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
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

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Informatics Practices", "Economics", "Business Studies", "Accountancy", "English", "Physics", "Chemistry", "Biology", "Mathematics", "History", "Geography", "Civics" }));
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

        jLabel4.setText("Subject:");

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

        jLabel84.setText("Status:");

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Active", "Taken", "Locked" }));
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });

        jButton51.setText("Go");
        jButton51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton51ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, 0, 193, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6))
                        .addGap(24, 24, 24)
                        .addComponent(jLabel84)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton51, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton51, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel84)))
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

        jButton19.setText("See all results");
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

        jButton25.setText("Report");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel35)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                    .addComponent(jButton19)
                    .addComponent(jButton25))
                .addContainerGap())
        );

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Welcome,");

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
                .addGroup(studentPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(studentPanelPageLayout.createSequentialGroup()
                        .addGroup(studentPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        studentPanelPageLayout.setVerticalGroup(
            studentPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentPanelPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
        jTable8.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField2)
                        .addComponent(jLabel46)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jLabel48))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        jTextArea1.setFont(new java.awt.Font("Calibri Light", 0, 18)); // NOI18N
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

        jButton56.setText("View image");
        jButton56.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton56ActionPerformed(evt);
            }
        });

        jLabel87.setText("This question has a picture:");

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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton56)
                    .addComponent(jLabel87))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addGroup(studentQuestionPageLayout.createSequentialGroup()
                        .addComponent(jLabel70, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(studentQuestionPageLayout.createSequentialGroup()
                        .addGroup(studentQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(studentQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12))))
        );
        studentQuestionPageLayout.setVerticalGroup(
            studentQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentQuestionPageLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(studentQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(studentQuestionPageLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(studentQuestionPageLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(jLabel70)
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

        jLabel76.setText("Issued cheat warnings:");

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
                            .addComponent(jLabel24)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel76, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(157, 157, 157)
                .addComponent(jLabel21)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(jLabel76))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jLabel50)
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextField7.setEditable(false);
        jTextField7.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jTextField7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jButton18.setText("OK");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jLabel22.setText("Final Score:");

        jTextField6.setEditable(false);
        jTextField6.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

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

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Welcome,");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel19.setText("Tests List:");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Test ID", "Description", "Status", "Date added", "No of Questions", "Minutes", "Marks Per Question", "Standard"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setToolTipText("");
        jTable2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(jTable2);

        jLabel56.setText("Show: ");

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Only my tests", "All tests" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jButton30.setText("Go");
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jPanel32.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton15.setText("Create Test");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton27.setText("Edit Test");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jLabel104.setText("Primary Test Options:");

        jButton13.setText("Unlock Test");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel104))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addComponent(jLabel104)
                .addGap(14, 14, 14)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton15)
                    .addComponent(jButton27)
                    .addComponent(jButton13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel33.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton12.setText("Lock Test");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jLabel105.setText("Test restriction options:");

        jButton14.setText("Delete Test");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel105)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addComponent(jLabel105)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton12)
                    .addComponent(jButton14))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel34.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel34.setPreferredSize(new java.awt.Dimension(605, 66));

        jLabel106.setText("Student options");

        jButton16.setText("Test Report");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton64.setText("Enable retest for specific student");
        jButton64.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton64ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                        .addGap(8, 8, 8)
                        .addComponent(jButton64, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addComponent(jLabel106)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addComponent(jLabel106)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton16)
                    .addComponent(jButton64))
                .addContainerGap())
        );

        jPanel35.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton39.setText("System Admin");
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });

        jLabel27.setText("Advanced:");

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton39)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel56)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton30))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel56)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel35, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu8.setText("Nav");

        jMenuItem19.setText("Sign Out");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem19);

        jMenuItem18.setText("Exit");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem18);

        jMenuBar8.add(jMenu8);

        teacherPanelPage.setJMenuBar(jMenuBar8);

        javax.swing.GroupLayout teacherPanelPageLayout = new javax.swing.GroupLayout(teacherPanelPage.getContentPane());
        teacherPanelPage.getContentPane().setLayout(teacherPanelPageLayout);
        teacherPanelPageLayout.setHorizontalGroup(
            teacherPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherPanelPageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(teacherPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        teacherPanelPageLayout.setVerticalGroup(
            teacherPanelPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherPanelPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel25.setText("Enter Test Description (Short name):");

        jLabel26.setText("Enter points per question:");

        jTextField9.setText("1");

        jLabel36.setText("Please make sure that your description isn't too long (Not more than 5 words)");

        jLabel55.setText("Example description: Unit-2 Chapter-4");

        jLabel68.setText("Enter Time Allotted for test (Minutes):");

        jTextField19.setText("30");

        jLabel101.setText("Standard:");

        jComboBox16.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26)
                            .addComponent(jLabel25)
                            .addComponent(jLabel55)
                            .addComponent(jLabel68))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel101)
                        .addGap(171, 171, 171)
                        .addComponent(jComboBox16, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel68)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel101)
                    .addComponent(jComboBox16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(jLabel36)
                .addGap(18, 18, 18)
                .addComponent(jLabel55)
                .addContainerGap())
        );

        jPanel18.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton21.setText("Enter questions");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jLabel52.setText("Start entering questions:");

        jLabel53.setText("(Or)");

        jLabel54.setText("Choose a source file:");

        jButton28.setText("Choose File");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jButton29.setText("What's this?");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel54)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(182, 182, 182)
                        .addComponent(jLabel53)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                        .addGap(264, 264, 264)
                        .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(jButton21))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel53)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(jButton28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton29)
                .addContainerGap())
        );

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
                .addContainerGap()
                .addGroup(teacherQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        teacherQuestionPageLayout.setVerticalGroup(
            teacherQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherQuestionPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel59.setText("Test ID (Auto generated):");

        jLabel60.setText("Description:");

        jLabel61.setText("Marks Per Question:");

        jLabel62.setText("Time Allotted:");

        jLabel58.setText("Edit Details");

        jTextField15.setEditable(false);

        jLabel63.setText("minutes");

        jLabel102.setText("Standard");

        jComboBox17.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel59)
                    .addComponent(jLabel61)
                    .addComponent(jLabel102))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField17, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                    .addComponent(jTextField15, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                    .addComponent(jComboBox17, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel58)
                        .addGap(29, 29, 29))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel60)
                            .addComponent(jLabel62))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel63))
                    .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel58)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel59)
                            .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel60))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel61)
                            .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel63)
                            .addComponent(jLabel62))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(jLabel102)))
                .addContainerGap())
        );

        jPanel21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTable9.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Questions", "Answers", "Image"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable9.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable9MouseClicked(evt);
            }
        });
        jScrollPane10.setViewportView(jTable9);

        jLabel57.setText("Edit Questions (Double click Question to view and edit)");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane10)
                .addContainerGap())
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(155, 155, 155)
                .addComponent(jLabel57)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel64.setText("NOTE:");

        jLabel65.setText("Please ensure that answer column contains only one letter, (one of a,b,c,d)");

        jLabel66.setText("Please make description short. (Not more than 5 words). Example: Unit 5, Chapter 4");

        jButton31.setText("Save");
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jButton33.setText("Insert Question");
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });

        jButton34.setText("Remove Question");
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });

        jButton35.setText("Remove All Blank Questions");
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });

        jButton57.setText("Add image for selected question");
        jButton57.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton57ActionPerformed(evt);
            }
        });

        jMenu15.setText("Nav");

        jMenuItem17.setText("Back");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu15.add(jMenuItem17);

        jMenuItem38.setText("Sign Out");
        jMenuItem38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem38ActionPerformed(evt);
            }
        });
        jMenu15.add(jMenuItem38);

        jMenuItem39.setText("Exit");
        jMenuItem39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem39ActionPerformed(evt);
            }
        });
        jMenu15.add(jMenuItem39);

        jMenuBar15.add(jMenu15);

        editQuestionPage.setJMenuBar(jMenuBar15);

        javax.swing.GroupLayout editQuestionPageLayout = new javax.swing.GroupLayout(editQuestionPage.getContentPane());
        editQuestionPage.getContentPane().setLayout(editQuestionPageLayout);
        editQuestionPageLayout.setHorizontalGroup(
            editQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editQuestionPageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editQuestionPageLayout.createSequentialGroup()
                        .addComponent(jLabel64)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel66)
                            .addComponent(jLabel65))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(editQuestionPageLayout.createSequentialGroup()
                        .addGroup(editQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(editQuestionPageLayout.createSequentialGroup()
                                .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton35, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))
                            .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
            .addGroup(editQuestionPageLayout.createSequentialGroup()
                .addGap(270, 270, 270)
                .addComponent(jButton31)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        editQuestionPageLayout.setVerticalGroup(
            editQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editQuestionPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jButton57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton33)
                    .addComponent(jButton34)
                    .addComponent(jButton35))
                .addGap(29, 29, 29)
                .addGroup(editQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65)
                    .addComponent(jLabel64))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton31)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Calibri Light", 0, 14)); // NOI18N
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(5);
        jTextArea3.setWrapStyleWord(true);
        jScrollPane13.setViewportView(jTextArea3);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton32.setText("Done");
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel67.setText("TEST ID");

        javax.swing.GroupLayout editQuestionBridgeLayout = new javax.swing.GroupLayout(editQuestionBridge.getContentPane());
        editQuestionBridge.getContentPane().setLayout(editQuestionBridgeLayout);
        editQuestionBridgeLayout.setHorizontalGroup(
            editQuestionBridgeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editQuestionBridgeLayout.createSequentialGroup()
                .addGroup(editQuestionBridgeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editQuestionBridgeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(editQuestionBridgeLayout.createSequentialGroup()
                        .addGap(261, 261, 261)
                        .addComponent(jButton32)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(editQuestionBridgeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel67, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        editQuestionBridgeLayout.setVerticalGroup(
            editQuestionBridgeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editQuestionBridgeLayout.createSequentialGroup()
                .addGap(0, 26, Short.MAX_VALUE)
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton32)
                .addGap(7, 7, 7))
        );

        jPanel23.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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
        jTable5.setToolTipText("");
        jTable5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane7.setViewportView(jTable5);

        jLabel72.setText("Class Average:");

        jLabel73.setText("Highest score:");

        jLabel74.setText("Lowest score:");

        jButton7.setText("Save to CSV");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton22.setText("OK");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jTextField21.setEditable(false);

        jTextField22.setEditable(false);

        jTextField23.setEditable(false);

        jButton63.setText("Save to HTML");
        jButton63.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton63ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton63, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel23Layout.createSequentialGroup()
                                    .addComponent(jLabel73)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel23Layout.createSequentialGroup()
                                    .addComponent(jLabel72)
                                    .addGap(18, 18, 18)
                                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addGap(100, 100, 100)
                                .addComponent(jLabel74)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jButton22)
                                .addGap(18, 18, 18)
                                .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel72)
                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel73)
                    .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel74)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton63, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2))
        );

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("TEST REPORT");

        jLabel45.setText("Test ID:");

        jLabel71.setText("Test Description:");

        jTextField3.setEditable(false);

        jTextField20.setEditable(false);

        jLabel69.setText("Test Subject:");

        jTextField24.setEditable(false);

        jMenu10.setText("Nav");

        jMenuItem23.setText("Back");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem23);

        jMenuItem25.setText("Sign Out");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem25);

        jMenuItem24.setText("Exit");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem24);

        jMenuBar10.add(jMenu10);

        teacherTestReportPage.setJMenuBar(jMenuBar10);

        javax.swing.GroupLayout teacherTestReportPageLayout = new javax.swing.GroupLayout(teacherTestReportPage.getContentPane());
        teacherTestReportPage.getContentPane().setLayout(teacherTestReportPageLayout);
        teacherTestReportPageLayout.setHorizontalGroup(
            teacherTestReportPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherTestReportPageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(teacherTestReportPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(teacherTestReportPageLayout.createSequentialGroup()
                        .addGroup(teacherTestReportPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(teacherTestReportPageLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(teacherTestReportPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel45)
                                    .addComponent(jLabel71)
                                    .addComponent(jLabel69))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(teacherTestReportPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField3)
                                    .addComponent(jTextField20)
                                    .addComponent(jTextField24))))
                        .addGap(11, 11, 11)))
                .addContainerGap())
        );
        teacherTestReportPageLayout.setVerticalGroup(
            teacherTestReportPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, teacherTestReportPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(teacherTestReportPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(teacherTestReportPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel71)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(teacherTestReportPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "User ID", "Activity", "Time"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(jTable6);

        jLabel28.setText("Activity Log:");

        jTable10.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "User ID", "Error Description", "Time"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable10);

        jLabel75.setText("Error Log:");

        jButton24.setText("Clear Activity log");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jButton37.setText("Clear Error Log");
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "On", "On or Before", "On or After" }));

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2015", "2016", "2017" }));

        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2015", "2016", "2017" }));

        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        jComboBox14.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "On", "On or Before", "On or After" }));

        jButton52.setText("Go");
        jButton52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton52ActionPerformed(evt);
            }
        });

        jButton53.setText("Go");
        jButton53.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton53ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addComponent(jLabel75)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton53)
                        .addGap(1, 1, 1))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addGap(258, 258, 258)
                                .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addGap(264, 264, 264)
                                .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addGap(26, 26, 26)
                        .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox10, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton52)))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton52))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton24)
                .addGap(10, 10, 10)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel75)
                    .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton53))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton37)
                .addContainerGap())
        );

        jButton40.setText("OK");
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        jMenu12.setText("Nav");

        jMenuItem29.setText("Back");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jMenu12.add(jMenuItem29);

        jMenuBar12.add(jMenu12);

        logPage.setJMenuBar(jMenuBar12);

        javax.swing.GroupLayout logPageLayout = new javax.swing.GroupLayout(logPage.getContentPane());
        logPage.getContentPane().setLayout(logPageLayout);
        logPageLayout.setHorizontalGroup(
            logPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logPageLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(logPageLayout.createSequentialGroup()
                .addGap(345, 345, 345)
                .addComponent(jButton40)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        logPageLayout.setVerticalGroup(
            logPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton40)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTable11.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User ID", "Name", "Subject"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable11.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane15.setViewportView(jTable11);

        jLabel33.setText("Pending Teacher applications:");

        jButton42.setText("Approve");
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        jButton43.setText("Delete");
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton43ActionPerformed(evt);
            }
        });

        jButton45.setText("Refresh");
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });

        jPanel37.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel29.setText("Search for username:");

        jTextField10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField10KeyPressed(evt);
            }
        });

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Teacher", "Student" }));
        jComboBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox5ActionPerformed(evt);
            }
        });

        jButton44.setText("GO");
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User ID", "Name", "Type", "Subject"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable7.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane9.setViewportView(jTable7);

        jLabel31.setText("New Password:");

        jLabel32.setText("Confirm password:");

        jButton38.setText("Reset password");
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        jButton41.setForeground(new java.awt.Color(255, 0, 0));
        jButton41.setText("Delete user");
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jLabel30.setText("Options:");

        jButton46.setText("History");
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton46ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addGap(35, 35, 35)
                        .addComponent(jPasswordField6, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(18, 18, 18)
                        .addComponent(jPasswordField7))
                    .addComponent(jButton38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jPasswordField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(jPasswordField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton46)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel37Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane9)
                        .addGroup(jPanel37Layout.createSequentialGroup()
                            .addComponent(jLabel29)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel37Layout.createSequentialGroup()
                .addContainerGap(165, Short.MAX_VALUE)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
            .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel37Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel29)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton44))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(206, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton45))
                    .addComponent(jPanel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane15)
                    .addComponent(jButton42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jButton45))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton43)
                .addContainerGap())
        );

        jPanel27.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton23.setText("Error and Activity reports");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jLabel77.setText("Specify cheat warning seconds:");

        jLabel78.setText("Specify No. of cheat warnings:");

        jLabel79.setText("Specify wake up timer seconds:");

        jButton47.setText("SAVE");
        jButton47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton47ActionPerformed(evt);
            }
        });

        jLabel80.setText("Fields cannot be blank, set to 0 to disable setting.");

        jLabel85.setText("Specify test timer flash limit:");

        jLabel86.setText("Select Error log location:");

        jButton54.setText("Select directory");
        jButton54.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton54ActionPerformed(evt);
            }
        });

        jTextField30.setEditable(false);
        jTextField30.setText("Log location");

        jButton55.setText("Debug: Throw an exception");
        jButton55.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton55ActionPerformed(evt);
            }
        });

        jLabel88.setText("Select location to save images:");

        jButton58.setText("Select directory");
        jButton58.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton58ActionPerformed(evt);
            }
        });

        jTextField31.setEditable(false);
        jTextField31.setText("Image location");

        jLabel90.setText("Warning: Anti cheat in beta stage. May behave abnormally.");

        buttonGroup3.add(jRadioButton5);
        jRadioButton5.setText("Off");
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton5ActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioButton6);
        jRadioButton6.setText("On");
        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton6ActionPerformed(evt);
            }
        });

        jLabel91.setText("Instant cheat alarm (may affect peformance):");

        jLabel103.setText("Allow Student registrations:");

        buttonGroup4.add(jRadioButton7);
        jRadioButton7.setText("On");

        buttonGroup4.add(jRadioButton8);
        jRadioButton8.setText("Off");

        jButton69.setText("Reset defaults");
        jButton69.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton69ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField31)
                    .addComponent(jTextField30)
                    .addComponent(jButton47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel80, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel77)
                            .addComponent(jLabel78)
                            .addComponent(jLabel79)
                            .addComponent(jLabel85))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField11)
                            .addComponent(jTextField25)
                            .addComponent(jTextField26)
                            .addComponent(jTextField29)))
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addComponent(jLabel86)
                        .addGap(52, 52, 52)
                        .addComponent(jButton54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addComponent(jLabel88)
                        .addGap(18, 18, 18)
                        .addComponent(jButton58, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel93, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel91)
                            .addComponent(jLabel103))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jRadioButton6)
                            .addComponent(jRadioButton7))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton8)
                            .addComponent(jRadioButton5))
                        .addGap(21, 21, 21))
                    .addComponent(jButton69, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel90, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78)
                    .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel79)
                    .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85)
                    .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel80)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel86)
                    .addComponent(jButton54))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel88)
                    .addComponent(jButton58))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel91)
                    .addComponent(jRadioButton6)
                    .addComponent(jRadioButton5))
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButton7)
                        .addComponent(jRadioButton8))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel103)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel93, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(jButton47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton69)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel90)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton55)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton23)
                .addContainerGap())
        );

        jMenu13.setText("Nav");

        jMenuItem32.setText("Back");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        jMenu13.add(jMenuItem32);

        jMenuItem34.setText("Sign Out");
        jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem34ActionPerformed(evt);
            }
        });
        jMenu13.add(jMenuItem34);

        jMenuItem33.setText("Exit");
        jMenuItem33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem33ActionPerformed(evt);
            }
        });
        jMenu13.add(jMenuItem33);

        jMenuBar13.add(jMenu13);

        adminPage.setJMenuBar(jMenuBar13);

        javax.swing.GroupLayout adminPageLayout = new javax.swing.GroupLayout(adminPage.getContentPane());
        adminPage.getContentPane().setLayout(adminPageLayout);
        adminPageLayout.setHorizontalGroup(
            adminPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        adminPageLayout.setVerticalGroup(
            adminPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminPageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(adminPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        antiCheatFrame.setTitle("Anti Cheat System");
        antiCheatFrame.setAlwaysOnTop(true);

        jPanel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextArea4.setEditable(false);
        jTextArea4.setColumns(20);
        jTextArea4.setFont(new java.awt.Font("Calibri Light", 0, 24)); // NOI18N
        jTextArea4.setForeground(new java.awt.Color(255, 0, 0));
        jTextArea4.setLineWrap(true);
        jTextArea4.setRows(5);
        jTextArea4.setWrapStyleWord(true);
        jScrollPane14.setViewportView(jTextArea4);

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton36.setText("OK");
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout antiCheatFrameLayout = new javax.swing.GroupLayout(antiCheatFrame.getContentPane());
        antiCheatFrame.getContentPane().setLayout(antiCheatFrameLayout);
        antiCheatFrameLayout.setHorizontalGroup(
            antiCheatFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(antiCheatFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(antiCheatFrameLayout.createSequentialGroup()
                .addGap(202, 202, 202)
                .addComponent(jButton36)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        antiCheatFrameLayout.setVerticalGroup(
            antiCheatFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(antiCheatFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton36)
                .addContainerGap())
        );

        jPanel28.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTable12.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Test ID", "Marks earned", "Aborted", "Cheat warnings", "Date taken"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane16.setViewportView(jTable12);

        jLabel81.setText("Tests taken:");

        jLabel82.setText("Activity history");

        jTable13.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Activity", "Time"
            }
        ));
        jScrollPane17.setViewportView(jTable13);

        jButton48.setText("Enable retest");
        jButton48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton48ActionPerformed(evt);
            }
        });

        jButton49.setText("Clear log for this user");
        jButton49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton49ActionPerformed(evt);
            }
        });

        jButton50.setText("Search");
        jButton50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton50ActionPerformed(evt);
            }
        });

        jLabel83.setText("by");

        jTextField28.setEditable(false);

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel28Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel28Layout.createSequentialGroup()
                                .addComponent(jButton48)
                                .addGap(301, 301, 301))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel28Layout.createSequentialGroup()
                                .addComponent(jButton49)
                                .addGap(279, 279, 279))))
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel28Layout.createSequentialGroup()
                                .addComponent(jLabel81)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel83)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton50, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                            .addComponent(jSeparator2)
                            .addComponent(jScrollPane17)
                            .addComponent(jScrollPane16, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addComponent(jLabel82)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel81)
                    .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton50)
                    .addComponent(jLabel83)
                    .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(jLabel82)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane17, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton49)
                .addGap(16, 16, 16))
        );

        jMenu16.setText("Nav");

        jMenuItem40.setText("Back");
        jMenuItem40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem40ActionPerformed(evt);
            }
        });
        jMenu16.add(jMenuItem40);

        jMenuBar16.add(jMenu16);

        userHistoryPage.setJMenuBar(jMenuBar16);

        javax.swing.GroupLayout userHistoryPageLayout = new javax.swing.GroupLayout(userHistoryPage.getContentPane());
        userHistoryPage.getContentPane().setLayout(userHistoryPageLayout);
        userHistoryPageLayout.setHorizontalGroup(
            userHistoryPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userHistoryPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        userHistoryPageLayout.setVerticalGroup(
            userHistoryPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userHistoryPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel89.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jScrollPane18.setViewportView(jLabel89);

        javax.swing.GroupLayout imageDisplayPageLayout = new javax.swing.GroupLayout(imageDisplayPage.getContentPane());
        imageDisplayPage.getContentPane().setLayout(imageDisplayPageLayout);
        imageDisplayPageLayout.setHorizontalGroup(
            imageDisplayPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, imageDisplayPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                .addContainerGap())
        );
        imageDisplayPageLayout.setVerticalGroup(
            imageDisplayPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imageDisplayPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel29.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Calibri Light", 0, 18)); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setWrapStyleWord(true);
        jScrollPane19.setViewportView(jTextArea2);

        jButton59.setText("OK");
        jButton59.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton59ActionPerformed(evt);
            }
        });

        jButton60.setText("Disable alarm");
        jButton60.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton60ActionPerformed(evt);
            }
        });

        jLabel92.setText("It's possible that the following students are cheating now:");

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel92)
                .addContainerGap(118, Short.MAX_VALUE))
            .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel29Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane19)
                        .addGroup(jPanel29Layout.createSequentialGroup()
                            .addGap(151, 151, 151)
                            .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jButton60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton59, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(134, 134, 134)))
                    .addContainerGap()))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel92)
                .addContainerGap(306, Short.MAX_VALUE))
            .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel29Layout.createSequentialGroup()
                    .addGap(42, 42, 42)
                    .addComponent(jScrollPane19, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton59)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton60)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout cheatAlarmPageLayout = new javax.swing.GroupLayout(cheatAlarmPage.getContentPane());
        cheatAlarmPage.getContentPane().setLayout(cheatAlarmPageLayout);
        cheatAlarmPageLayout.setHorizontalGroup(
            cheatAlarmPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cheatAlarmPageLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        cheatAlarmPageLayout.setVerticalGroup(
            cheatAlarmPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cheatAlarmPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel30.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel94.setFont(new java.awt.Font("Walkway SemiBold", 1, 48)); // NOI18N
        jLabel94.setForeground(new java.awt.Color(0, 0, 153));
        jLabel94.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel94.setText("MAHATMA CBSE");

        jLabel95.setFont(new java.awt.Font("Walkway SemiBold", 1, 24)); // NOI18N
        jLabel95.setForeground(new java.awt.Color(204, 0, 51));
        jLabel95.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel95.setText("ONLINE TEST SYSTEM");

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel94, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel95, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel94, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel95, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel96.setText("User:");

        jLabel97.setText("Password:");

        jButton61.setText("Generate Key");
        jButton61.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton61ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout generateKeyPageLayout = new javax.swing.GroupLayout(generateKeyPage.getContentPane());
        generateKeyPage.getContentPane().setLayout(generateKeyPageLayout);
        generateKeyPageLayout.setHorizontalGroup(
            generateKeyPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generateKeyPageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generateKeyPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel30, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField32)
                    .addGroup(generateKeyPageLayout.createSequentialGroup()
                        .addGroup(generateKeyPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel96)
                            .addComponent(jLabel97))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPasswordField8))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, generateKeyPageLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton61)
                .addGap(174, 174, 174))
        );
        generateKeyPageLayout.setVerticalGroup(
            generateKeyPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generateKeyPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel96)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel97)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPasswordField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jButton61)
                .addContainerGap())
        );

        jPanel31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextArea5.setEditable(false);
        jTextArea5.setColumns(20);
        jTextArea5.setFont(new java.awt.Font("Calibri Light", 0, 18)); // NOI18N
        jTextArea5.setLineWrap(true);
        jTextArea5.setRows(5);
        jTextArea5.setWrapStyleWord(true);
        jScrollPane20.setViewportView(jTextArea5);

        jButton62.setText("OK");
        jButton62.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton62ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane20, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGap(209, 209, 209)
                .addComponent(jButton62)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane20, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton62)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout displayReportQuestionPageLayout = new javax.swing.GroupLayout(displayReportQuestionPage.getContentPane());
        displayReportQuestionPage.getContentPane().setLayout(displayReportQuestionPageLayout);
        displayReportQuestionPageLayout.setHorizontalGroup(
            displayReportQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(displayReportQuestionPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        displayReportQuestionPageLayout.setVerticalGroup(
            displayReportQuestionPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(displayReportQuestionPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel36.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel107.setText("Search for student:");

        jTable14.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "Name", "Standard"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable14MouseClicked(evt);
            }
        });
        jScrollPane21.setViewportView(jTable14);

        jTable15.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Test ID", "Subject", "Description", "Marks scored", "Total Marks", "Tried to Cheat", "Date Taken", "Closed Voluntarily"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane22.setViewportView(jTable15);

        jLabel108.setText("Tests Taken:");

        jLabel109.setText("Search by Test ID:");

        jButton65.setText("Enable Retest");
        jButton65.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton65ActionPerformed(evt);
            }
        });

        jButton66.setText("Go");
        jButton66.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton66ActionPerformed(evt);
            }
        });

        jButton67.setText("Go");
        jButton67.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton67ActionPerformed(evt);
            }
        });

        jButton68.setText("Showing tests for this student");
        jButton68.setEnabled(false);
        jButton68.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton68ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane21)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(jLabel107)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton66))
                    .addComponent(jScrollPane22)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(jLabel108)
                        .addGap(164, 164, 164)
                        .addComponent(jLabel109)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton67))
                    .addComponent(jButton68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel36Layout.createSequentialGroup()
                .addContainerGap(435, Short.MAX_VALUE)
                .addComponent(jButton65)
                .addGap(419, 419, 419))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel107)
                    .addComponent(jTextField35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton66))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane21, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton68)
                .addGap(13, 13, 13)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel108)
                    .addComponent(jLabel109)
                    .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton67))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane22, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton65)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu17.setText("Nav");

        jMenuItem20.setText("Back");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu17.add(jMenuItem20);

        jMenuBar17.add(jMenu17);

        friendlyRetestPage.setJMenuBar(jMenuBar17);

        javax.swing.GroupLayout friendlyRetestPageLayout = new javax.swing.GroupLayout(friendlyRetestPage.getContentPane());
        friendlyRetestPage.getContentPane().setLayout(friendlyRetestPageLayout);
        friendlyRetestPageLayout.setHorizontalGroup(
            friendlyRetestPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(friendlyRetestPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        friendlyRetestPageLayout.setVerticalGroup(
            friendlyRetestPageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(friendlyRetestPageLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAutoRequestFocus(false);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        if (jTable2.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(teacherPanelPage, "You need to select a test", "No Test Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String x = (String) jTable2.getValueAt(jTable2.getSelectedRow(), 0);
        String descx = (String) jTable2.getValueAt(jTable2.getSelectedRow(), 1);
        try {
            logActivity(loginID, "Locked test " + x);
            stmt.executeUpdate("update testlist set status=0 where testid=\"" + x + "\";");
            JOptionPane.showMessageDialog(teacherPanelPage, "Test " + descx + " was locked.", "Action Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showException("Error occured while locking test " + x, ex);
        }
        updateTeacherTestList();
    }//GEN-LAST:event_jButton12ActionPerformed
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        if (jComboBox2.getSelectedIndex() == 0) {
            if (registrationsAllowed == 1) {
                studentRegisterPage.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(redirectPage, "Registrations have been closed. Contact an administrator to register", "Registrations closed", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        teacherTestReportPage.dispose();
    }//GEN-LAST:event_jMenuItem23ActionPerformed
    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
    }//GEN-LAST:event_jMenuItem26ActionPerformed
    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed
        logPage.dispose();
    }//GEN-LAST:event_jMenuItem29ActionPerformed
    private void jMenuItem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem32ActionPerformed
        adminPage.dispose();
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
    private boolean validateRegID(String a, int type) {
        if (!checkValidCharsUsed(a, false, true)) {
            JOptionPane.showMessageDialog(this, "Your User-ID cannot contain spaces or symbols.", "Invalid ID", JOptionPane.ERROR_MESSAGE);
        } else {
            ResultSet rs;
            if (a.length() >= 4) {
                try {
                    if (type == TYPE_STUDENT) {
                        rs = stmt.executeQuery("select userid from student_auth where userid=\"" + a + "\";");
                    } else {
                        rs = stmt.executeQuery("select userid from teacher_auth where userid=\"" + a + "\";");
                    }
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "That User ID is taken. Please try something else.", "Name in use", JOptionPane.ERROR_MESSAGE);
                    } else {
                        return true;
                    }
                } catch (SQLException ex) {
                    showException("Error while checking for redundancy on registration.", ex);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Reg ID must contain atleast 4 characters.", "Invalid ID", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;

    }

    private BigInteger validatePassword(char[] a, char[] b, boolean check) {
        BigInteger big;
        boolean passwordsMatch;
        if (check) {
            passwordsMatch = Arrays.equals(a, b);
        } else {
            passwordsMatch = true;
        }
        if (passwordsMatch) {
            if (a.length >= 4) {
                MessageDigest digest;
                try {
                    digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(new String(a).getBytes(StandardCharsets.UTF_8));
                    big = new BigInteger(1, hash);
                    return big;
                } catch (NoSuchAlgorithmException ex) {
                    showException("Problem importing Java Security libraries. SHA256 NOT FOUND.", ex);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Your password must be at least 4 characters long.", "Password too short", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Your passwords do not match.", "Password mismatch", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        ResultSet rs;
        boolean regIDCorrect, passwordCorrect;
        String regID = jTextField33.getText();
        String regName = jTextField4.getText();
        regIDCorrect = validateRegID(regID, 0);
        char[] password = jPasswordField2.getPassword();
        char[] passwordConfirm = jPasswordField3.getPassword();
        BigInteger big = validatePassword(password, passwordConfirm, true);
        passwordCorrect = big != null;
        int std = jComboBox15.getSelectedIndex() + 1;
        if (regIDCorrect && passwordCorrect) {
            if (checkValidCharsUsed(regName, true, false)) {
                try {
                    stmt.executeUpdate("insert into student_auth values(\"" + regID + "\",\"" + regName + "\",\"" + big + "\",0," + std + ");");
                    stmt.executeUpdate("create table studentHistoryDatabase_" + regID + "(testid varchar(50), marksearned int(5), aborted int(1), cheatwarnings int (2),datetaken timestamp);");
                    JOptionPane.showMessageDialog(this, "Registration successful. You can log in now.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    logActivity(regID, "student registered");
                    studentRegisterPage.dispose();
                    loginPage.setVisible(true);
                    jRadioButton9.setSelected(true);
                } catch (SQLException ex) {
                    showException("Error on creating user record", ex);
                }
            } else {
                JOptionPane.showMessageDialog(teacherRegisterPage, "You cannot use numbers and symbols for name.", "Invalid Name", JOptionPane.WARNING_MESSAGE);
            }
        }
        clearAuthFields();
    }//GEN-LAST:event_jButton10ActionPerformed
    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        String regName = jTextField5.getText().trim();
        String regID = jTextField34.getText().trim();
        char[] password = jPasswordField4.getPassword();
        char[] passwordConfirm = jPasswordField5.getPassword();
        boolean regIDCorrect = validateRegID(regID, 1);
        boolean passwordCorrect = false;
        BigInteger big = validatePassword(password, passwordConfirm, true);
        if (big != null) {
            passwordCorrect = true;
        }
        String subject = (String) jComboBox3.getSelectedItem();
        if (regIDCorrect && passwordCorrect) {
            if (checkValidCharsUsed(regName, true, false)) {
                try {
                    stmt.executeUpdate("insert into teacher_auth values (\"" + regID + "\",\"" + regName + "\",\"" + big + "\",\"" + subject + "\",0,0);");
                    JOptionPane.showMessageDialog(this, "Your request has been submitted. Please have a system administrator review your request.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    logActivity(regID, "Teacher application submitted");
                    teacherRegisterPage.dispose();
                    loginPage.setVisible(true);
                    jRadioButton10.setSelected(true);
                } catch (SQLException ex) {
                    showException("Error on creating user record", ex);
                }
            } else {
                JOptionPane.showMessageDialog(teacherRegisterPage, "You cannot use numbers and symbols for name.", "Invalid Name", JOptionPane.WARNING_MESSAGE);
            }
        }
        clearAuthFields();
    }//GEN-LAST:event_jButton11ActionPerformed
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        fetchSystemParameters();
        ResultSet rs;
        loginID = jTextField1.getText().trim();
        char[] loginPassword = jPasswordField1.getPassword();
        boolean loginSuccess = false;
        try {
            if (jRadioButton9.isSelected()) {
                PRESENTUSERTYPE = TYPE_STUDENT;
                rs = stmt.executeQuery("select * from student_auth where userid=\"" + loginID + "\";");
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
                rs = stmt.executeQuery("select * from teacher_auth where userid=\"" + loginID + "\";");
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
                logActivity(loginID, "User logged in");
                resetWakeUpTimer();
                wakeUpTimer = new java.util.Timer();
                wakeUpTimerTask = new java.util.TimerTask() {
                    @Override
                    public void run() {
                        decreaseWakeUpTimer();
                    }
                };
                if (savedWakeUpSetting != 0) {
                    wakeUpTimer.scheduleAtFixedRate(wakeUpTimerTask, 1000, 1000);
                }
                loginPage.dispose();
                if (isUserStudent()) {
                    rs = stmt.executeQuery("select * from student_auth where userid=\"" + loginID + "\";");
                    if (rs.next()) {
                        loginName = rs.getString("name");
                        loginStandard = rs.getInt("standard");
                    }
                    stmt.executeUpdate("update student_auth set onlinestatus=1 where userid=\"" + loginID + "\";");
                    jLabel5.setText("Welcome, " + loginName);
                    studentPanelPage.setVisible(true);
                    updateStudentTestList();
                    updateStudentTestListForStatus();
                } else {
                    rs = stmt.executeQuery("select * from teacher_auth where userid=\"" + loginID + "\";");
                    if (rs.next()) {
                        loginName = rs.getString("name");
                    }
                    try {
                        rs = stmt.executeQuery("select logno from instantcheatalarm order by logno desc;");
                        if (rs.next()) {
                            currentAlarmIndex = rs.getInt("logno");
                        }
                    } catch (SQLException ex1) {
                        showException("Error occured while enabling cheat alarm", ex1);
                    }
                    if (instantCheatAlarm == 1) {
                        instantCheatAlarmTask = new java.util.TimerTask() {
                            @Override
                            public void run() {
                                alarmCheats();
                            }
                        };
                        wakeUpTimer.schedule(instantCheatAlarmTask, 1000, 1500);
                    }
                    stmt.executeUpdate("update teacher_auth set onlinestatus=1 where userid=\"" + loginID + "\";");
                    jLabel20.setText("Welcome, " + loginName);
                    teacherPanelPage.setVisible(true);
                    updateTeacherTestList();
                }
            }
        } catch (SQLException ex) {
            showException("Error while fetching login results", ex);
        }
        clearAuthFields();
    }//GEN-LAST:event_jButton1ActionPerformed
    private void alarmCheats() {
        ResultSet rs;
        boolean disp = false;
        try {
            rs = stmt.executeQuery("select * from instantcheatalarm where logno>" + currentAlarmIndex + " order by logno desc;");
            int a = 0;
            while (rs.next()) {
                disp = true;
                a = Math.max(a, rs.getInt("logno"));
                String name = rs.getString("username");
                String testid = rs.getString("testid");
                jTextArea2.append(name + " is possibly cheating on " + testid + "\n");
            }
            currentAlarmIndex = Math.max(currentAlarmIndex, a);
            if (disp) {
                cheatAlarmPage.setVisible(true);
                cheatAlarmPage.setAlwaysOnTop(true);
                cheatAlarmPage.setAlwaysOnTop(false);
            }
        } catch (SQLException ex) {
            showException("Error occured in cheat alarm functioning", ex);
        }
    }

    private void decreaseWakeUpTimer() {
        if (wakeUpSeconds == 1) {
            if (isTestInProgress) {
                try {
                    stmt.executeUpdate("update studenthistorydatabase_" + loginID + " set aborted=2 where testid=\"" + currentTestID + "\";");
                    logActivity(loginID, "Timed out");
                } catch (SQLException ex) {
                    showException("Error occured while aborting test due to inactivity", ex);
                }
            }
            logout();
            JOptionPane.showMessageDialog(loginPage, "You have been inactive for the past few minutes and hence have been logged out for security concerns.\nIf you think this was an error, please contact a system administrator.", "Inactivity Detection", JOptionPane.INFORMATION_MESSAGE);
        } else {
            wakeUpSeconds--;
        }
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        updateStudentTestList();
        updateStudentTestListForStatus();
    }//GEN-LAST:event_jButton2ActionPerformed
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        updateStudentTestList();
        updateStudentTestListForStatus();
    }//GEN-LAST:event_jComboBox1ActionPerformed
    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        logout();
    }//GEN-LAST:event_jMenuItem10ActionPerformed
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        fetchSystemParameters();
        ResultSet rs;
        totalQuestions = 0;
        int marks = 0;
        try {
            String testid = (String) jTable1.getValueAt(jTable1.getSelectedRow(), 0);
            switch ((String) jTable1.getValueAt(jTable1.getSelectedRow(), 2)) {
                case "Locked":
                    JOptionPane.showMessageDialog(studentPanelPage, "That test is locked.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    break;
                case "Taken":
                    JOptionPane.showMessageDialog(studentPanelPage, "You've already taken this test.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    int answer = JOptionPane.showConfirmDialog(studentPanelPage, "Are you sure? You cannot retake this test once started.", "Start confirmation", JOptionPane.YES_NO_OPTION);
                    if (answer == 0) {
                        stmt.executeUpdate("insert into studenthistorydatabase_" + loginID + "(testid,marksearned,datetaken,aborted) values (\"" + testid + "\",0,now(),0);");
                        studentPanelPage.dispose();
                        currentTestID = testid;
                        jLabel38.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 3) + " - " + jTable1.getValueAt(jTable1.getSelectedRow(), 1));
                        rs = stmt.executeQuery("select points from testlist  where status!=-1 and testid=\"" + testid + "\";");
                        if (rs.next()) {
                            marks = rs.getInt(1);
                        }
                        jLabel39.setText("Marks per Question: " + marks);
                        studentQuestionPage.setVisible(true);
                        rs = stmt.executeQuery("select seconds from testlist where status!=-1 and testid=\"" + testid + "\";");
                        if (rs.next()) {
                            testCountdown = rs.getInt(1);
                        }
                        testTimerTask = new java.util.TimerTask() {
                            @Override
                            public void run() {
                                if (testCountdown == 1) {
                                    canCheat = false;
                                    if (testTimerTask != null) {
                                        testTimerTask.cancel();
                                    }
                                    JOptionPane.showMessageDialog(studentQuestionPage, "Oops! Time up!", "Time up", JOptionPane.INFORMATION_MESSAGE);
                                    finishTest();
                                    testTimerTask.cancel();
                                    isTestInProgress = false;
                                }
                                if (testCountdown <= flashWarning) {
                                    Color r = new Color(255, 0, 0);
                                    Color b = new Color(0, 0, 0);
                                    if (red) {
                                        jTextField2.setForeground(r);
                                        red = false;
                                    } else {
                                        jTextField2.setForeground(b);
                                        red = true;
                                    }
                                }
                                testCountdown--;
                                int min = testCountdown / 60;
                                int sec = testCountdown % 60;
                                jTextField2.setText(min + ":" + sec);
                            }
                        };
                        wakeUpTimer.scheduleAtFixedRate(testTimerTask, 1000, 1000);
                        isTestInProgress = true;
                        rs = stmt.executeQuery("select count(*) from testquestions_" + testid);
                        if (rs.next()) {
                            totalQuestions = rs.getInt(1);
                        }
                        jTextField14.setText(Integer.toString(totalQuestions));
                        currentTestID = testid;
                        initiateTest(testid);
                        issuedWarnings = 0;
                    }
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(studentPanelPage, "You need to select a test.", "No test selected", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
        }
    }//GEN-LAST:event_jButton3ActionPerformed
    private void finishTest() {
        try {
            testTimerTask.cancel();
        } catch (NullPointerException ex) {
        }
        antiCheatFrame.dispose();
        antiCheatFrame.setAlwaysOnTop(false);
        acCount = 0;
        isTestInProgress = false;
        studentQuestionPage.dispose();
        imageDisplayPage.dispose();
        ArrayList<String> finalAnswersList = new ArrayList<>();
        ResultSet rs;
        int correctAnswers = 0, wrongAnswers = 0, marks = 1;
        try {
            rs = stmt.executeQuery("select points from testlist where status!=-1 and testid=\"" + currentTestID + "\";");
            if (rs.next()) {
                marks = rs.getInt(1);
            }
            DefaultTableModel resultsModel = (DefaultTableModel) jTable3.getModel();
            resultsModel.setRowCount(0);
            rs = stmt.executeQuery("select answer from testquestions_" + currentTestID + " ;");
            while (rs.next()) {
                finalAnswersList.add(rs.getString("answer"));
            }
            for (String x : questionList) {
                String xTokens[] = x.split(separator);
                int index = Integer.parseInt(xTokens[0]);
                String question = xTokens[1];
                String selectedAnswer = "x";
                try {
                    rs = stmt.executeQuery("select question_" + index + " from studenthistorydatabase_" + loginID + " where testid=\"" + currentTestID + "\";");
                    if (rs.next()) {
                        selectedAnswer = rs.getString(1);
                    }
                } catch (SQLException ex) {
                    selectedAnswer = "x";
                }
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
            jLabel49.setText("Correct Answers: " + correctAnswers);
            jLabel50.setText("Wrong Answers: " + wrongAnswers);
            jLabel76.setText("Issued cheat warnings: " + issuedWarnings);
            jTextField6.setText(Integer.toString(correctAnswers * marks));
            stmt.executeUpdate("update studenthistorydatabase_" + loginID + " set marksearned=" + Integer.toString(correctAnswers * marks) + " where testid=\"" + currentTestID + "\";");
            stmt.executeUpdate("update studenthistorydatabase_" + loginID + " set cheatwarnings=" + Integer.toString(issuedWarnings) + " where testid=\"" + currentTestID + "\";");
            logActivity(loginID, "User Finished Test");
            jTextField7.setText(Integer.toString(questionList.size() * marks));
            studentFinishTestPage.setVisible(true);
            studentFinishTestPage.setAlwaysOnTop(true);
            studentFinishTestPage.setAlwaysOnTop(false);
        } catch (SQLException ex) {
            showException("Error occured while displaying results", ex);
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
        jLabel70.setText(null);
        if (Integer.parseInt(jTextField13.getText()) == 1) {
            jLabel70.setText("You have no more questions before this one");
            return;
        }
        curQuesInd--;
        setNextQuestion(curQuesInd);
    }//GEN-LAST:event_jButton5ActionPerformed
    private void updateQuestionProgress() {
        ResultSet rs;
        jLabel41.setText("Total Answered: " + totalAnsweredQuestions);
        jLabel43.setText("Total Unanswered: " + Integer.toString(totalQuestions - totalAnsweredQuestions));
        buttonGroup1.clearSelection();
        try {
            rs = stmt.executeQuery("select question_" + getQuestionIndex(curQuesInd) + " from studenthistorydatabase_" + loginID + " where testid=\"" + currentTestID + "\";");
            if (rs.next()) {
                String ans = rs.getString(1);
                switch (ans) {
                    case "a":
                        jRadioButton1.setSelected(true);
                        break;
                    case "b":
                        jRadioButton2.setSelected(true);
                        break;
                    case "c":
                        jRadioButton3.setSelected(true);
                        break;
                    case "d":
                        jRadioButton4.setSelected(true);
                        break;
                    case "x":
                        buttonGroup1.clearSelection();
                        break;
                }
            }
        } catch (SQLException ex) {
            /*             showException("Error occured while checking if already answered to update radio",ex);*/        }
    }
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jLabel70.setText(null);
        if (Integer.parseInt(jTextField13.getText()) == Integer.parseInt(jTextField14.getText())) {
            jLabel70.setText("You have no more questions left.\tClick Finish test.");
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
        jLabel70.setText("Question Unflagged");
        DefaultListModel flaggedListModel = (DefaultListModel) jList2.getModel();
        if (flaggedListModel.removeElement("Question " + Integer.toString(curQuesInd + 1))) {
            totalFlagged--;
            System.out.println("Unflagging");
        }
        jLabel42.setText("Total Flagged: " + totalFlagged);
    }//GEN-LAST:event_jButton26ActionPerformed
    @SuppressWarnings("unchecked")
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        jLabel70.setText("Question Flagged");
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
            String selLine = (String) jList2.getSelectedValue();
            String selLineTokens[] = selLine.split(" ");
            curQuesInd = Integer.parseInt(selLineTokens[1]) - 1;
            setNextQuestion(curQuesInd);
        }
    }//GEN-LAST:event_jList2MouseClicked
    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        canCheat = false;
        int result = JOptionPane.showConfirmDialog(studentQuestionPage, "Are you sure you want to finish? You will not be able to re-take this test.", "Finish Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            finishTest();
        }
        canCheat = true;
    }//GEN-LAST:event_jButton17ActionPerformed
    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        if (evt.getClickCount() == 2) {
            jTextArea5.setText((String) (jTable3.getValueAt(jTable3.getSelectedRow(), 0)));
            displayReportQuestionPage.setVisible(true);
            displayReportQuestionPage.setAlwaysOnTop(true);
            displayReportQuestionPage.setAlwaysOnTop(false);
        }
    }//GEN-LAST:event_jTable3MouseClicked
    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        studentFinishTestPage.dispose();
        studentPanelPage.setVisible(true);
        cleanUpAfterTest();
    }//GEN-LAST:event_jButton18ActionPerformed
    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        try {
            Statement stmt3 = con.createStatement();
            ResultSet rs, rs2, rs3;
            DefaultTableModel prevResultsModel = (DefaultTableModel) jTable4.getModel();
            prevResultsModel.setRowCount(0);
            rs = stmt.executeQuery("select * from studenthistorydatabase_" + loginID + ";");
            while (rs.next()) {
                try {
                    String testid = rs.getString("testid");
                    rs3 = stmt3.executeQuery("select count(*) from testquestions_" + testid + ";");
                    int totalMarks = 0;
                    if (rs3.next()) {
                        totalMarks = rs3.getInt(1);
                    }
                    rs2 = stmt2.executeQuery("select description,subject,points from testlist where status!=-1 and testid=\"" + testid + "\";");
                    if (rs2.next()) {
                        totalMarks *= rs2.getInt("points");
                        String score = Integer.toString(rs.getInt("marksearned")) + "/" + totalMarks;
                        prevResultsModel.addRow(new Object[]{rs2.getString("subject"), rs2.getString("description"), score});
                    }
                } catch (SQLException ex) {
                }
            }
        } catch (SQLException ex) {
            showException("Error occured while displaying previous resuts", ex);
        }
        studentPreviousResultsPage.setVisible(true);
    }//GEN-LAST:event_jButton19ActionPerformed
    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        studentPreviousResultsPage.dispose();
    }//GEN-LAST:event_jButton20ActionPerformed
    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem12ActionPerformed
    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        logout();
    }//GEN-LAST:event_jMenuItem13ActionPerformed
    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        teacherQuestionPage.setVisible(true);
    }//GEN-LAST:event_jButton15ActionPerformed
    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        try {
            File f;
            int val = jFileChooser1.showOpenDialog(teacherQuestionPage);
            if (val == JFileChooser.APPROVE_OPTION) {
                createTestFrame();
                f = jFileChooser1.getSelectedFile();
                if (!f.getCanonicalPath().endsWith(".txt")) {
                    JOptionPane.showMessageDialog(teacherQuestionPage, "That file is not accepted. Please use only standard text (.txt) files.", "Invalid File", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                return;
            }
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String text = "";
            String dummy;
            DefaultTableModel questionEditModel = (DefaultTableModel) jTable9.getModel();
            questionEditModel.setRowCount(0);
            while ((dummy = br.readLine()) != null) {
                try {
                    String dummyTokens[] = dummy.split(";-;");
                    questionEditModel.addRow(new Object[]{dummyTokens[0], dummyTokens[1]});
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }
            teacherQuestionPage.dispose();
            editQuestionPage.setVisible(true);
        } catch (IOException ex) {
            showException("error occured while parsing source file", ex);
        }
    }//GEN-LAST:event_jButton28ActionPerformed
    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        updateTeacherTestList();
    }//GEN-LAST:event_jComboBox4ActionPerformed
    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        if (jTable2.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(teacherPanelPage, "You need to select a test", "No Test Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String x = (String) jTable2.getValueAt(jTable2.getSelectedRow(), 0);
        String descx = (String) jTable2.getValueAt(jTable2.getSelectedRow(), 1);
        int questionNo = (Integer) jTable2.getValueAt(jTable2.getSelectedRow(), 4);
        if (questionNo == 0) {
            JOptionPane.showMessageDialog(teacherPanelPage, "This test has no questions.\nConsider adding questions or deleting it.", "Empty Test", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            stmt.executeUpdate("update testlist set status=1 where testid=\"" + x + "\";");
            logActivity(loginID, "Unlocked test " + x);
            JOptionPane.showMessageDialog(teacherPanelPage, "Test " + descx + " was unlocked.", "Action Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showException("Error occured while unlocking test " + x, ex);
        }
        updateTeacherTestList();
    }//GEN-LAST:event_jButton13ActionPerformed
    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        try {
            String x = (String) jTable2.getValueAt(jTable2.getSelectedRow(), 0);
            String descx = (String) jTable2.getValueAt(jTable2.getSelectedRow(), 1);
            int result = JOptionPane.showConfirmDialog(teacherPanelPage, "Are you sure that you want to delete this test?\n\nTest ID: " + x + "\nTest Description: " + descx + "\nThis action cannot be undone.", "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    ResultSet rs = stmt.executeQuery("select imagesource from testquestions_" + x + ";");
                    File parentDir = null;
                    while (rs.next()) {
                        File tempFile = new File(rs.getString("imagesource"));
                        if (tempFile.exists()) {
                            parentDir = tempFile.getParentFile();
                            tempFile.delete();
                        }
                    }
                    try {
                        if (parentDir != null) {
                            for (File f : parentDir.listFiles()) {
                                f.delete();
                            }
                            parentDir.delete();
                        }
                    } catch (NullPointerException ex) {
                    }
                    stmt.executeUpdate("update testlist set status=-1 where testid=\"" + x + "\";");
                    stmt.executeUpdate("drop table testquestions_" + x);
                    JOptionPane.showMessageDialog(teacherPanelPage, "Test " + descx + " was deleted.", "Action Successful", JOptionPane.INFORMATION_MESSAGE);
                    logActivity(loginID, "Deleted Test " + x);
                } catch (SQLException ex) {
                    showException("Error occured while deleting test " + x, ex);
                }
                updateTeacherTestList();
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(teacherPanelPage, "You need to select a test to delete", "No Test Selected", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton14ActionPerformed
    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        if (jTable2.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(teacherPanelPage, "You need to select a test", "No Test Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        updateQuestionEditList();
        editQuestionPage.setVisible(true);
    }//GEN-LAST:event_jButton27ActionPerformed
    private void updateQuestionEditList() {
        ResultSet rs;
        try {
            String testid = (String) jTable2.getValueAt(jTable2.getSelectedRow(), 0);
            jTextField15.setText(testid);
            jTextField16.setText((String) jTable2.getValueAt(jTable2.getSelectedRow(), 1));
            jTextField17.setText(Integer.toString((Integer) jTable2.getValueAt(jTable2.getSelectedRow(), 6)));
            jTextField18.setText((String) jTable2.getValueAt(jTable2.getSelectedRow(), 5));
            int val = (Integer) jTable2.getValueAt(jTable2.getSelectedRow(), 7);
            jComboBox17.setSelectedIndex(val);
            DefaultTableModel editQuestionsModel = (DefaultTableModel) jTable9.getModel();
            editQuestionsModel.setRowCount(0);
            rs = stmt.executeQuery("select * from testquestions_" + testid + ";");
            while (rs.next()) {
                String question = rs.getString("question");
                String answer = rs.getString("answer");
                String imagesource = rs.getString("imagesource");
                editQuestionsModel.addRow(new Object[]{question, answer, imagesource});
            }
        } catch (SQLException ex) {
            showException("Error occured while updating edit question list", ex);
        }
    }
    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        fetchSystemParameters();
        ResultSet rs;
        String testid = jTextField15.getText().trim();
        if (jTextField16.getText().trim().length() == 0 || jTextField17.getText().trim().length() == 0 || jTextField18.getText().trim().length() == 0 || jComboBox17.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(editQuestionPage, "Please fill all fields at the top", "Empty Details", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int seconds;
            try {
                seconds = Integer.parseInt(jTextField18.getText().trim()) * 60;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editQuestionPage, "Please enter only whole numbers for minutes.", "Invalid Time", JOptionPane.ERROR_MESSAGE);
                return;
            }
            stmt.executeUpdate("update testlist set description=\"" + jTextField16.getText().trim() + "\" where testid=\"" + testid + "\";");
            stmt.executeUpdate("update testlist set points=\"" + jTextField17.getText().trim() + "\" where testid=\"" + testid + "\";");
            stmt.executeUpdate("update testlist set seconds=\"" + Integer.parseInt(jTextField18.getText().trim()) * 60 + "\" where testid=\"" + testid + "\";");
            stmt.executeUpdate("update testlist set standard=" + jComboBox17.getSelectedIndex() + " where testid =\"" + testid + "\";");
            for (int i = 0; i < jTable9.getRowCount(); i++) {
                String question = (String) jTable9.getValueAt(i, 0);
                if (question != null) {
                    if (question.length() < 5) {
                        JOptionPane.showMessageDialog(editQuestionPage, "Question " + Integer.toString(i + 1) + " is too short.", "Invalid Question", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String answer = ((String) jTable9.getValueAt(i, 1)).toLowerCase().trim();
                    if (answer != null) {
                        if ((answer.length() > 1) || !(answer.equals("a") || answer.equals("b") || answer.equals("c") || answer.equals("d"))) {
                            JOptionPane.showMessageDialog(editQuestionPage, "The answer for question " + Integer.toString(i + 1) + " is invalid.\nEnter only a (or) b (or) c (or) d.", "Invalid Answer", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } else {
                        JOptionPane.showMessageDialog(editQuestionPage, "You left the answer to question " + Integer.toString(i + 1) + " empty.", "Empty Question", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(editQuestionPage, "You left question " + Integer.toString(i + 1) + " empty.", "Empty Question", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            stmt.executeUpdate("delete from testquestions_" + testid + ";");
            if (jTable9.getRowCount() == 0) {
                int ans = JOptionPane.showConfirmDialog(editQuestionPage, "There aren't any questions. Test will be locked. Would you like to proceed?", "Empty Test", JOptionPane.YES_NO_OPTION);
                if (ans == JOptionPane.YES_OPTION) {
                    stmt.executeUpdate("update testlist set status=0 where testid=\"" + testid + "\";");
                } else {
                    return;
                }
            } else {
                try {
                    File folder;
                    if (resLocation.endsWith("/")) {
                        folder = new File(resLocation + jTextField15.getText().trim());
                    } else {
                        folder = new File(resLocation + "/" + jTextField15.getText().trim());
                    }
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    for (int i = 0; i < jTable9.getRowCount(); i++) {
                        String tempPath = (String) jTable9.getValueAt(i, 2);
                        String imgPath = "";
                        if (tempPath != null) {
                            File x = new File((String) jTable9.getValueAt(i, 2));
                            if (x.exists()) {
                                int index = x.getCanonicalPath().lastIndexOf('.');
                                String extension = x.getCanonicalPath().substring(index, x.getCanonicalPath().length());
                                File newPath = new File(folder.getCanonicalPath() + "/Question_" + Integer.toString(i + 1) + extension);
                                Files.copy(x.toPath(), newPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                imgPath = newPath.getCanonicalPath().replace('\\', '/');
                            }
                        }
                        stmt.executeUpdate("insert into testquestions_" + testid + " values (" + Integer.toString(i + 1) + ",\"" + jTable9.getValueAt(i, 0) + "\",\"" + ((String) jTable9.getValueAt(i, 1)).toLowerCase() + "\",\"" + imgPath + "\",0);");
                    }
                } catch (IOException ex) {
                    showException("Error occured while uploading image", ex);
                }
            }
            JOptionPane.showMessageDialog(editQuestionPage, "All changes successfully saved.", "Edit Successful", JOptionPane.INFORMATION_MESSAGE);
            logActivity(loginID, "Saved Test" + testid);
            editQuestionPage.dispose();
            updateTeacherTestList();
            cleanUpAfterCreation();
        } catch (SQLException ex) {
            showException("Error occured while updating edits to database", ex);
        }
    }//GEN-LAST:event_jButton31ActionPerformed
    private void cleanUpAfterCreation() {
        DefaultTableModel model = (DefaultTableModel) jTable9.getModel();
        model.setRowCount(0);
        jTextField16.setText(null);
        jTextField8.setText(null);
    }
    private void jTable9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable9MouseClicked
        if (evt.getClickCount() == 2) {
            curEdit = jTable9.getSelectedRow();
            jTextArea3.setText((String) jTable9.getValueAt(curEdit, 0));
            editQuestionBridge.setVisible(true);
            jLabel67.setText("Test ID: " + jTextField15.getText());
        }
    }//GEN-LAST:event_jTable9MouseClicked
    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        jTable9.setValueAt(jTextArea3.getText().trim(), curEdit, 0);
        jTextArea3.setText(null);
        editQuestionBridge.dispose();
    }//GEN-LAST:event_jButton32ActionPerformed
    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        DefaultTableModel editQuestionModel = (DefaultTableModel) jTable9.getModel();
        int ind = jTable9.getSelectedRow();
        if (ind == -1) {
            editQuestionModel.insertRow(jTable9.getRowCount(), new Object[]{});
        } else {
            editQuestionModel.insertRow(ind, new Object[]{});
        }
    }//GEN-LAST:event_jButton33ActionPerformed
    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        if (jTable9.getSelectedRow() != -1) {
            int ans = JOptionPane.showConfirmDialog(editQuestionPage, "Are you sure you want to delete Question " + Integer.toString(jTable9.getSelectedRow() + 1) + "? The only way to add it back would be to insert it again", "Delete Question Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ans == JOptionPane.YES_OPTION) {
                DefaultTableModel questionEditModel = (DefaultTableModel) jTable9.getModel();
                questionEditModel.removeRow(jTable9.getSelectedRow());
            }
        } else {
            JOptionPane.showMessageDialog(editQuestionPage, "Please select a question to delete", "No Question Selected", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton34ActionPerformed
    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        DefaultTableModel editQuestionsModel = (DefaultTableModel) jTable9.getModel();
        for (int i = jTable9.getRowCount() - 1; i >= 0; i--) {
            if (jTable9.getValueAt(i, 0) == null) {
                editQuestionsModel.removeRow(i);
            }
        }
    }//GEN-LAST:event_jButton35ActionPerformed
    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        updateTeacherTestList();
    }//GEN-LAST:event_jButton30ActionPerformed
    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        logout();
    }//GEN-LAST:event_jMenuItem19ActionPerformed
    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        logout();
        System.exit(0);
    }//GEN-LAST:event_jMenuItem18ActionPerformed
    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        int ans = JOptionPane.showConfirmDialog(editQuestionPage, "Save Changes before closing?", "Close confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ans == JOptionPane.YES_OPTION) {
            jButton31.doClick();
        } else if (ans == JOptionPane.NO_OPTION) {
            editQuestionPage.dispose();
        }
    }//GEN-LAST:event_jMenuItem17ActionPerformed
    private void jMenuItem38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem38ActionPerformed
        int ans = JOptionPane.showConfirmDialog(editQuestionPage, "Changes will not be saved. Are you sure?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            logout();
        }
    }//GEN-LAST:event_jMenuItem38ActionPerformed
    private void jMenuItem39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem39ActionPerformed
        int ans = JOptionPane.showConfirmDialog(editQuestionPage, "Changes will not be saved. Are you sure?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            logout();
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItem39ActionPerformed
    private String createTestFrame() {
        String testid = generateTestID();
        ResultSet rs;
        try {
            stmt.executeUpdate("insert into testlist values(\"" + testid + "\",\"" + loginID + "\",\"" + jTextField8.getText().trim() + "\",\"" + getTeacherSubject() + "\"," + Integer.parseInt(jTextField9.getText().trim()) + ",0,now()," + Double.toString(Double.parseDouble(jTextField19.getText().trim()) * 60) + "," + jComboBox16.getSelectedIndex() + ");");
            stmt.executeUpdate("create table testquestions_" + testid + "( sno int(11),question varchar(2500),answer varchar(5),imagesource varchar(2500),reserve int(1));");
            rs = stmt.executeQuery("select * from testlist where testid=\"" + testid + "\";");
            if (rs.next()) {
                jTextField15.setText(rs.getString("testid"));
                jTextField16.setText(rs.getString("description"));
                jTextField17.setText(rs.getString("points"));
                jTextField18.setText(Integer.toString(rs.getInt("seconds") / 60));
                jComboBox17.setSelectedIndex(rs.getInt("standard"));
            }
        } catch (SQLException ex) {
            showException("Error occured while creating testqsns table", ex);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(editQuestionPage, "Enter only numerical values for time and points per question.", "Invalid values", JOptionPane.ERROR_MESSAGE);
        }
        return testid;
    }
    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        createTestFrame();
        editQuestionPage.setVisible(true);
        teacherQuestionPage.dispose();
    }//GEN-LAST:event_jButton21ActionPerformed
    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        JOptionPane.showMessageDialog(teacherQuestionPage, "If you have a standard text (.txt) file with the questions and answer separated in the following manner:\n\nQuestion;-;Answer\nQuestion;-;Answer\nQuestion;-;Answer\n\nYou can select it to automatically upload questions for this test.\n\nIt is important for each question to begin in a new line, and that the answer for the question is in the same line, separated by ;-;\nAll Standard escape sequences like \\n \\t apply.", "Using source files", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton29ActionPerformed
    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        antiCheatFrame.dispose();
        studentQuestionPage.setAlwaysOnTop(true);
        studentQuestionPage.setAlwaysOnTop(false);
    }//GEN-LAST:event_jButton36ActionPerformed
    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        if (jTable2.getSelectedRow() != -1) {
            updateTestReport();
            teacherTestReportPage.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(teacherPanelPage, "You need to select a test", "No test selected", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton16ActionPerformed
    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        teacherTestReportPage.dispose();
    }//GEN-LAST:event_jButton22ActionPerformed
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String defName = jTextField3.getText().trim() + "_" + jTextField20.getText().trim() + ".csv";
        jFileChooser1.setSelectedFile(new File(defName));
        int ans = jFileChooser1.showSaveDialog(teacherTestReportPage);
        FileWriter fw = null;
        if (ans == JFileChooser.APPROVE_OPTION) {
            try {
                File f = jFileChooser1.getSelectedFile();
                if (!(f.getCanonicalPath().endsWith(".csv"))) {
                    f = new File(f.getCanonicalPath() + ".csv");
                }
                fw = new FileWriter(f);
                fw.write("Test Report" + System.getProperty("line.separator"));
                fw.write("Test Subject," + jTextField24.getText().trim() + System.getProperty("line.separator"));
                fw.write("Test Description," + jTextField20.getText().trim() + System.getProperty("line.separator"));
                for (int i = 0; i < jTable5.getRowCount(); i++) {
                    fw.write(jTable5.getValueAt(i, 0) + "," + Integer.toString((Integer) jTable5.getValueAt(i, 1)) + System.getProperty("line.separator"));
                }
                fw.write("Class Average," + jTextField21.getText() + System.getProperty("line.separator"));
                fw.write("Highest Score," + jTextField22.getText() + System.getProperty("line.separator"));
                fw.write("Lowest Score," + jTextField23.getText() + System.getProperty("line.separator"));
            } catch (IOException ex) {
                showException("Error while saving file", ex);
            } finally {
                try {
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException e3) {
                    showException("Error occured while producing resultsheet", e3);
                }
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed
    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        logout();
        System.exit(0);
    }//GEN-LAST:event_jMenuItem24ActionPerformed
    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        logout();
    }//GEN-LAST:event_jMenuItem25ActionPerformed
    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        updateLogs();
        logPage.setVisible(true);
        logActivity(loginID, "Accessed logs");
    }//GEN-LAST:event_jButton23ActionPerformed
    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        try {
            stmt.executeUpdate("delete from activitylog");
        } catch (SQLException ex) {
            showException("error while clearing activity log", ex);
        }
        updateLogs();
    }//GEN-LAST:event_jButton24ActionPerformed
    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        try {
            logActivity(loginID, "Cleared error log");
            stmt.executeUpdate("delete from errorlog");
        } catch (SQLException ex) {
            showException("error while clearing error log", ex);
        }
        updateLogs();
    }//GEN-LAST:event_jButton37ActionPerformed
    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
        updateSearchList();
        updatePendingList();
        adminPage.setVisible(true);
    }//GEN-LAST:event_jButton39ActionPerformed
    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        logPage.dispose();
    }//GEN-LAST:event_jButton40ActionPerformed
    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        if (jTable11.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(adminPage, "You need to select a user", "No User selected", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String id = (String) jTable11.getValueAt(jTable11.getSelectedRow(), 0);
        int ans = JOptionPane.showConfirmDialog(adminPage, "Are you sure that you want to grant teacher priveleges to " + id + "?\nWarning: User will be able to reset passwords and test settings. Caution advised.", "Confirm grant privileges", JOptionPane.WARNING_MESSAGE);
        if (ans == JOptionPane.YES_OPTION) {
            try {
                stmt.executeUpdate("update teacher_auth set status=1 where userid=\"" + id + "\"");
                logActivity(loginID, "Approved application of " + id);
            } catch (SQLException ex) {
                showException("Error while approving teacher", ex);
            }
        }
        updatePendingList();
    }//GEN-LAST:event_jButton42ActionPerformed
    private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
        updateSearchList();
    }//GEN-LAST:event_jButton44ActionPerformed
    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        if (jTable7.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(adminPage, "Please select a user", "No user selected", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String id = (String) jTable7.getValueAt(jTable7.getSelectedRow(), 0);
        try {
            BigInteger finalPass = validatePassword(jPasswordField6.getPassword(), jPasswordField7.getPassword(), true);
            if (finalPass != null) {
                String type = ((String) jTable7.getValueAt(jTable7.getSelectedRow(), 2)).trim().toLowerCase();
                stmt.executeUpdate("update " + type + "_auth set password=\"" + finalPass + "\" where userid=\"" + id + "\"");
                JOptionPane.showMessageDialog(adminPage, "Password reset successfully", "Action successful", JOptionPane.INFORMATION_MESSAGE);
                logActivity(loginID, "Password reset for " + id);
            }
        } catch (SQLException ex) {
            showException("Error while resetting pass", ex);
        }
        jPasswordField6.setText(null);
        jPasswordField7.setText(null);
    }//GEN-LAST:event_jButton38ActionPerformed
    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        if (jTable7.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(adminPage, "You need to select a user", "No User selected", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String id = (String) jTable7.getValueAt(jTable7.getSelectedRow(), 0);
        int ans = JOptionPane.showConfirmDialog(adminPage, "Are you sure you want to delete \nUser " + id + " ?", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ans == JOptionPane.YES_OPTION) {
            try {
                String type = ((String) jTable7.getValueAt(jTable7.getSelectedRow(), 2)).trim().toLowerCase();
                if (id.equals(loginID)) {
                    JOptionPane.showMessageDialog(adminPage, "You cannot delete yourself", "Invalid action", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                switch (type) {
                    case "teacher":
                        stmt.executeUpdate("delete from teacher_auth where userid=\"" + id + "\";");
                        break;
                    case "student":
                        stmt.executeUpdate("delete from student_auth where userid=\"" + id + "\";");
                        stmt.executeUpdate("drop table studenthistorydatabase_" + id);
                        break;
                }
                JOptionPane.showMessageDialog(adminPage, "User " + id + " was successfully removed from the database.", "User deleted", JOptionPane.INFORMATION_MESSAGE);
                logActivity(loginID, "Deleted user " + id);
            } catch (SQLException ex) {
                showException("Error occured while deleting user", ex);
            }
        }
        updateSearchList();
    }//GEN-LAST:event_jButton41ActionPerformed
    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        updatePendingList();
    }//GEN-LAST:event_jButton45ActionPerformed
    private void jButton43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton43ActionPerformed
        if (jTable11.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(adminPage, "You need to select a user", "No User selected", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int ans = JOptionPane.showConfirmDialog(adminPage, "Are you sure you want to delete this application?", "Confirm delete action", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ans == JOptionPane.YES_OPTION) {
            String id = (String) jTable11.getValueAt(jTable11.getSelectedRow(), 0);
            try {
                stmt.executeUpdate("delete from teacher_auth where userid=\"" + id + "\"");
                logActivity(loginID, "Deleted application of " + id);
            } catch (SQLException ex) {
                showException("Error while deleting teacher", ex);
            }
        }
        updatePendingList();
    }//GEN-LAST:event_jButton43ActionPerformed
    private void jMenuItem34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem34ActionPerformed
        logout();
    }//GEN-LAST:event_jMenuItem34ActionPerformed
    private void jMenuItem33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem33ActionPerformed
        logout();
        System.exit(0);
    }//GEN-LAST:event_jMenuItem33ActionPerformed
    private void jButton47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton47ActionPerformed
        updateSystemParameters();
        logActivity(loginID, "Changed system parameters");
    }//GEN-LAST:event_jButton47ActionPerformed
    private void jButton46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton46ActionPerformed
        if (jTable7.getSelectedRow() != -1) {
            String name = (String) jTable7.getValueAt(jTable7.getSelectedRow(), 0);
            if (((String) jTable7.getValueAt(jTable7.getSelectedRow(), 2)).trim().toLowerCase().equals("teacher")) {
                updateUserHistoryTable(name, TYPE_TEACHER);
            } else {
                updateUserHistoryTable(name, TYPE_STUDENT);
            }
            userHistoryPage.setVisible(true);
            logActivity(loginID, "History accessed for " + name);
        } else {
            JOptionPane.showMessageDialog(adminPage, "You need to select a user.", "No user selected", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton46ActionPerformed
    private void jButton50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton50ActionPerformed
        if (((String) jTable7.getValueAt(jTable7.getSelectedRow(), 2)).trim().toLowerCase().equals("teacher")) {
            updateUserHistoryTable((String) jTable7.getValueAt(jTable7.getSelectedRow(), 0), TYPE_TEACHER);
        } else {
            updateUserHistoryTable((String) jTable7.getValueAt(jTable7.getSelectedRow(), 0), TYPE_STUDENT);
        }
    }//GEN-LAST:event_jButton50ActionPerformed
    private void jButton48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton48ActionPerformed
        String id = jTextField28.getText().trim();
        if (jTable12.getSelectedRow() != -1) {
            String testid = (String) jTable12.getValueAt(jTable12.getSelectedRow(), 0);
            if (testid != null) {
                int ans = JOptionPane.showConfirmDialog(userHistoryPage, "Are you sure you want to enable a re-test? for\n\nTEST ID: " + testid + "\n\nYou cannot undo this action.", "Confirm action", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (ans == JOptionPane.YES_OPTION) {
                    try {
                        stmt.executeUpdate("delete from studenthistorydatabase_" + id + " where testid=\"" + testid + "\";");
                        updateUserHistoryTable(id, TYPE_STUDENT);
                        logActivity(loginID, "Enabled retest for " + id);
                        JOptionPane.showMessageDialog(userHistoryPage, "Re-test enabled.", "Action successful", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        showException("Error occured while enabling retest", ex);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(userHistoryPage, "You need to select a test.", "No test selected", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton48ActionPerformed
    private void jButton49ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton49ActionPerformed
        try {
            stmt.executeUpdate("delete from activitylog where userid=\"" + jTextField28.getText().trim() + "\";");
            logActivity(loginID, "Cleared log for " + jTextField28.getText().trim());
        } catch (SQLException ex) {
            showException("Error occured while deleting log for particular user.", ex);
        } finally {
            updateUserHistoryTable(jTextField28.getText().trim(), TYPE_TEACHER);
        }
    }//GEN-LAST:event_jButton49ActionPerformed
    private void jMenuItem40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem40ActionPerformed
        userHistoryPage.dispose();
    }//GEN-LAST:event_jMenuItem40ActionPerformed
    private void jTextField10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField10KeyPressed
        updateSearchList();
    }//GEN-LAST:event_jTextField10KeyPressed
    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
        updateSearchList();
    }//GEN-LAST:event_jComboBox5ActionPerformed
    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        if (jTable1.getSelectedRow() != -1) {
            if (((String) jTable1.getValueAt(jTable1.getSelectedRow(), 2)).trim().equals("Taken")) {
                String testid = ((String) jTable1.getValueAt(jTable1.getSelectedRow(), 0)).trim();
                ResultSet rs, rs2;
                DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
                model.setRowCount(0);
                try {
                    int correctAnswers = 0, wrongAnswers = 0;
                    rs = stmt.executeQuery("select * from testquestions_" + testid + " order by sno;");
                    int totalQuestionsLocal = 0;
                    while (rs.next()) {
                        totalQuestionsLocal++;
                        String question = rs.getString("question");
                        String correctAnswer = rs.getString("answer");
                        String selectedAnswer = "x";
                        try {
                            rs2 = stmt2.executeQuery("select question_" + rs.getInt("sno") + " from studenthistorydatabase_" + loginID + " where testid=\"" + testid + "\";");
                            if (rs2.next()) {
                                selectedAnswer = rs2.getString(1);
                            }
                        } catch (SQLException ex) {
                        }
                        if (selectedAnswer.equals("x")) {
                            selectedAnswer = "Not attempted";
                        }
                        model.addRow(new Object[]{question, correctAnswer, selectedAnswer});
                        if (selectedAnswer.equals(correctAnswer)) {
                            correctAnswers++;
                        } else {
                            wrongAnswers++;
                        }
                    }
                    int cheatWarnings = 0;
                    rs = stmt.executeQuery("select cheatwarnings from studenthistorydatabase_" + loginID + " where testid =\"" + testid + "\";");
                    if (rs.next()) {
                        cheatWarnings = rs.getInt(1);
                    }
                    int points = 1;
                    rs = stmt.executeQuery("select points from testlist where testid=\"" + testid + "\";");
                    if (rs.next()) {
                        points = rs.getInt(1);
                    }
                    jLabel49.setText("Correct Answers: " + correctAnswers);
                    jLabel50.setText("Wrong Answers: " + wrongAnswers);
                    jLabel76.setText("Issued cheat warnings: " + cheatWarnings);
                    jTextField6.setText(Integer.toString(correctAnswers * points));
                    jTextField7.setText(Integer.toString(totalQuestionsLocal * points));
                    studentFinishTestPage.setVisible(true);
                } catch (SQLException ex) {
                    showException("Error occured while showing sec results", ex);
                }
            } else {
                JOptionPane.showMessageDialog(studentPanelPage, "You haven't yet taken that test.", "Test not taken", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(studentPanelPage, "You need to select a test.", "No test selected", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton25ActionPerformed
    private void jButton51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton51ActionPerformed
        updateStudentTestList();
        updateStudentTestListForStatus();
    }//GEN-LAST:event_jButton51ActionPerformed
    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        updateStudentTestList();
        updateStudentTestListForStatus();
    }//GEN-LAST:event_jComboBox6ActionPerformed
    private void jButton54ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton54ActionPerformed
        try {
            jFileChooser1.setSelectedFile(new File(jTextField30.getText()));
            int val = jFileChooser1.showSaveDialog(adminPage);
            if (val == JFileChooser.APPROVE_OPTION) {
                File f = jFileChooser1.getSelectedFile();
                String logPath = f.getCanonicalPath();
                if (!logPath.endsWith(".txt")) {
                    logPath += ".txt";
                }
                logPath = logPath.replace('\\', '/');
                jTextField30.setText(logPath);
            }
        } catch (IOException ex) {
            showException("Error occured while selecting error log location", ex);
        }
    }//GEN-LAST:event_jButton54ActionPerformed
    private void jButton55ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton55ActionPerformed
        try {
            logActivity(loginID, "Threw an exception (debugging)");
            throw new Exception("User generated");
        } catch (Exception ex) {
            showException("User generated", ex);
        }
    }//GEN-LAST:event_jButton55ActionPerformed
    private void jButton52ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton52ActionPerformed
        updateLogs();
    }//GEN-LAST:event_jButton52ActionPerformed
    private void jButton53ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton53ActionPerformed
        updateLogs();
    }//GEN-LAST:event_jButton53ActionPerformed
    private void jButton57ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton57ActionPerformed
        if (jTable9.getSelectedRow() != -1) {
            int val = jFileChooser1.showOpenDialog(editQuestionPage);
            if (val == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = jFileChooser1.getSelectedFile();
                    String path = f.getCanonicalPath();
                    path = path.replace('\\', '/');
                    jTable9.setValueAt(path, jTable9.getSelectedRow(), 2);
                } catch (IOException ex) {
                    showException("Error occured while selecting image for question", ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(editQuestionPage, "Please select a question to add image", "No Question Selected", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton57ActionPerformed
    private void jButton58ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton58ActionPerformed
        JFileChooser jFileChooser2 = new JFileChooser();
        jFileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int val = jFileChooser2.showSaveDialog(adminPage);
        if (val == JFileChooser.APPROVE_OPTION) {
            try {
                File f = jFileChooser2.getSelectedFile();
                String path = f.getCanonicalPath();
                path = path.replace('\\', '/') + "/";
                jTextField31.setText(path);
            } catch (IOException ex) {
                showException("Error occured while choosing save location", ex);
            }
        }
    }//GEN-LAST:event_jButton58ActionPerformed
    private void jButton56ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton56ActionPerformed
        imageDisplayed = true;
        imageDisplayPage.setVisible(true);
    }//GEN-LAST:event_jButton56ActionPerformed
    private void jButton59ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton59ActionPerformed
        jTextArea2.setText(null);
        cheatAlarmPage.dispose();
    }//GEN-LAST:event_jButton59ActionPerformed
    private void jButton60ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton60ActionPerformed
        int val = JOptionPane.showConfirmDialog(null, "Are you sure you want to stop receiving cheating alerts?", "Disable cheat alarm?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (val == JOptionPane.YES_OPTION) {
            instantCheatAlarmTask.cancel();
            cheatAlarmPage.dispose();
        }
    }//GEN-LAST:event_jButton60ActionPerformed
    private void jRadioButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton6ActionPerformed
        jLabel93.setText("Save and restart program to enable/disable alarm");
    }//GEN-LAST:event_jRadioButton6ActionPerformed
    private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton5ActionPerformed
        jLabel93.setText("Save and restart program to enable/disable alarm");
    }//GEN-LAST:event_jRadioButton5ActionPerformed
    @SuppressWarnings("deprecation")
    private void jButton61ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton61ActionPerformed
        String a[] = new String[2];
        a[0] = jTextField32.getText();
        a[1] = jPasswordField8.getText();
        StringBuilder b = new StringBuilder("");
        for (String x : a) {
            for (int i = 0; i < x.length(); i++) {
                b.append((char) ((x.charAt(i)) - 3));
            }
            b.append(";");
        }
        try {
            boolean ok = false;
            File f = new File(programDataLocation + "/Quiz/key.txt");
            if (!f.exists()) {
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
                if (f.createNewFile()) {
                    ok = true;
                } else {
                    throw new IOException("Couldn't mkfile");
                }
            } else if (f.delete()) {
                ok = true;
            }
            if (ok) {
                try (FileWriter fw = new FileWriter(f);) {
                    fw.write(b.toString());
                    fw.close();
                } catch (IOException ex) {
                    showException("Error occured while writing to gen files", ex);
                }
                JOptionPane.showMessageDialog(null, "Authentication key generated successfully.", "Action successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Please run as administrator.", "Action failed", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        } catch (IOException ex) {
            logError("Error occured on creating gen files", ex);
        }
    }//GEN-LAST:event_jButton61ActionPerformed

    private void jButton62ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton62ActionPerformed
        jTextArea5.setText(null);
        displayReportQuestionPage.dispose();
    }//GEN-LAST:event_jButton62ActionPerformed

    private void jButton63ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton63ActionPerformed
        // TODO add your handling code here:
        String defName = jTextField3.getText().trim() + "_" + jTextField20.getText().trim() + ".html";
        jFileChooser1.setSelectedFile(new File(defName));
        int ans = jFileChooser1.showSaveDialog(teacherTestReportPage);
        FileWriter fw = null;
        if (ans == JFileChooser.APPROVE_OPTION) {
            try {
                File f = jFileChooser1.getSelectedFile();
                if (!(f.getCanonicalPath().endsWith(".html"))) {
                    f = new File(f.getCanonicalPath() + ".html");
                }
                fw = new FileWriter(f);
                fw.write("<html>");
                fw.write("<head><title>Test Report</title></head>");
                fw.write("<body><center><table width=60% border=2><tr><td colspan=2 align=\"center\"><h2>Test Report</h2></td><h3></tr><tr><td width=50%>Test Subject: </td><td>" + jTextField24.getText().trim() + "</td></tr>");
                fw.write("<tr><td>Test Description: </td><td>" + jTextField20.getText().trim() + "</h2></td></tr>");
                fw.write("<tr bgcolor=\"pink\"><td align=\"center\" colspan=2>Student results</td></tr>");
                for (int i = 0; i < jTable5.getRowCount(); i++) {
                    fw.write("<tr>");
                    fw.write("<td>" + jTable5.getValueAt(i, 0) + "</td>");
                    fw.write("<td>" + Integer.toString((Integer) jTable5.getValueAt(i, 1)) + "</td");
                    fw.write("</tr>");
                }
                fw.write("<tr bgcolor=\"pink\"><td align=\"center\" colspan=2>Class results</td></tr>");
                fw.write("<tr>");
                fw.write("<td>Class Average</td><td>" + jTextField21.getText() + "</td>");
                fw.write("</tr>");
                fw.write("<tr>");
                fw.write("<td>Highest Score</td><td>" + jTextField22.getText() + "</td>");
                fw.write("</tr>");
                fw.write("<tr>");
                fw.write("<td>Lowest Score</td><td>" + jTextField23.getText() + "</td>");
                fw.write("</center></body></html>");
            } catch (IOException ex) {
                showException("Error while saving file", ex);
            } finally {
                try {
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException e3) {
                    showException("Error occured while producing resultsheet", e3);
                }
            }
        }
    }//GEN-LAST:event_jButton63ActionPerformed

    private void jButton64ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton64ActionPerformed
        // TODO add your handling code here:
        jButton66.doClick();
        friendlyRetestPage.setVisible(true);

    }//GEN-LAST:event_jButton64ActionPerformed

    private void jButton66ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton66ActionPerformed
        // TODO add your handling code here:
        ResultSet rs;
        String name = jTextField35.getText().trim();
        DefaultTableModel model = (DefaultTableModel) jTable14.getModel();
        model.setRowCount(0);
        try {
            rs = stmt.executeQuery("select * from student_auth where name like \"%" + name + "%\" or userid like \"%" + name + "%\";");
            while (rs.next()) {
                String id = rs.getString("userid");
                String name1 = rs.getString("name");
                int standard = rs.getInt("standard");
                model.addRow(new Object[]{id, name1, standard});
            }
        } catch (SQLException ex) {
            showException("Error occured while searching for students", ex);
        }
    }//GEN-LAST:event_jButton66ActionPerformed

    private void jButton67ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton67ActionPerformed
        // TODO add your handling code here:
        lookForTests();
    }//GEN-LAST:event_jButton67ActionPerformed

    private void lookForTests() {
        ResultSet rs, rs2, rs3;
        if (jTable14.getSelectedRow() != -1) {
            String query = jTextField36.getText().trim();
            DefaultTableModel model = (DefaultTableModel) jTable15.getModel();
            model.setRowCount(0);
            String userid = (String) jTable14.getValueAt(jTable14.getSelectedRow(), 0);
            try {
                rs = stmt.executeQuery("select * from studenthistorydatabase_" + userid + " where testid like \"%" + query + "%\";");
                while (rs.next()) {
                    String testid = rs.getString("testid");
                    int cheatwarnings = rs.getInt("cheatwarnings");
                    String date = rs.getString("datetaken");
                    int marksearned = rs.getInt("marksearned");
                    int aborted = rs.getInt("aborted");
                    String abRep;
                    switch (aborted) {
                        case 0:
                            abRep = "No";
                            break;
                        case 1:
                            abRep = "Yes";
                            break;
                        case 2:
                            abRep = "Due to inactivity";
                            break;
                        default:
                            abRep = "Unknown";
                            break;
                    }
                    String desc = "Error", sub = "Error";
                    int tot = -1;
                    rs2 = stmt2.executeQuery("select * from testlist where testid=\"" + testid + "\";");
                    if (rs2.next()) {
                        desc = rs2.getString("description");
                        sub = rs2.getString("subject");
                        tot = rs2.getInt("points");
                    }
                    rs3 = stmt2.executeQuery("select count(*) from testquestions_" + testid + ";");
                    if (rs3.next()) {
                        tot *= rs3.getInt(1);
                    }
                    model.addRow(new Object[]{testid, sub, desc, marksearned, tot, cheatwarnings, date, abRep});

                }
            } catch (SQLException ex) {
                showException("Error occured while searching for tests", ex);
            }
        } else {
            JOptionPane.showMessageDialog(friendlyRetestPage, "You need to select a student first", "No student selected", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void jButton65ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton65ActionPerformed
        // TODO add your handling code here:
        int c1 = jTable14.getSelectedRow();
        int c2 = jTable15.getSelectedRow();
        if (c1 != -1 && c2 != -1) {
            String userid = (String) jTable14.getValueAt(jTable14.getSelectedRow(), 0);
            int res = JOptionPane.showConfirmDialog(friendlyRetestPage, "Enable retest for " + userid + "?\nThis action cannot be undone.", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                try {
                    String testid = (String) jTable15.getValueAt(jTable15.getSelectedRow(), 0);
                    stmt.executeUpdate("delete from studenthistorydatabase_" + userid + " where testid =\"" + testid + "\";");
                    logActivity(loginID, "Enabled retest for " + userid);
                    jButton67.doClick();
                    JOptionPane.showMessageDialog(friendlyRetestPage, "Re-test enabled.", "Action successful", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    showException("Unable to enable retest. Try again", ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(friendlyRetestPage, "Please select a student and test.", "No test/student selected", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton65ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        // TODO add your handling code here:
        friendlyRetestPage.dispose();
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jTable14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable14MouseClicked
        // TODO add your handling code here:
        lookForTests();
    }//GEN-LAST:event_jTable14MouseClicked

    private void jButton68ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton68ActionPerformed
        lookForTests();
    }//GEN-LAST:event_jButton68ActionPerformed

    private void jButton69ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton69ActionPerformed
        // TODO add your handling code here:
        resetDefaults();
        fetchSystemParameters();
        JOptionPane.showMessageDialog(adminPage, "Parameters reset", "Action completed", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton69ActionPerformed

    private void updateStudentTestListForStatus() {
        if (jComboBox6.getSelectedIndex() != 0) {
            DefaultTableModel statusModel = (DefaultTableModel) jTable1.getModel();
            String status = (String) jComboBox6.getSelectedItem();
            for (int i = jTable1.getRowCount() - 1; i >= 0; i--) {
                if (!(jTable1.getValueAt(i, 2).equals(status))) {
                    statusModel.removeRow(i);
                }
            }
        }
    }

    private void updateUserHistoryTable(String id, int userType) {
        jTextField28.setText(id);
        String query = jTextField27.getText().trim();
        ResultSet rs;
        DefaultTableModel studentHistoryModel = (DefaultTableModel) jTable12.getModel();
        studentHistoryModel.setRowCount(0);
        DefaultTableModel userLogModel = (DefaultTableModel) jTable13.getModel();
        userLogModel.setRowCount(0);
        try {
            if (userType == TYPE_STUDENT) {
                rs = stmt.executeQuery("select * from studenthistorydatabase_" + id + " where testid like \"%" + jTextField27.getText().trim() + "%\";");
                while (rs.next()) {
                    String testid = rs.getString("testid");
                    int marksearned = rs.getInt("marksearned");
                    int aborted = rs.getInt("aborted");
                    String abortedRep = "Not aborted";
                    switch (aborted) {
                        case 1: {
                            abortedRep = "Aborted";
                        }
                        break;
                        case 2: {
                            abortedRep = "Inactive";
                        }
                        break;
                    }
                    int cheats = rs.getInt("cheatwarnings");
                    String date = rs.getString("datetaken");
                    studentHistoryModel.addRow(new Object[]{testid, marksearned, abortedRep, cheats, date});
                }
            }
            rs = stmt.executeQuery("select * from activitylog where userid=\"" + id + "\";");
            while (rs.next()) {
                String activity = rs.getString("activity");
                String time = rs.getString("time");
                userLogModel.addRow(new Object[]{activity, time});
            }
        } catch (SQLException ex) {
            showException("Error occured while fetching studenthistorydatabase", ex);
        }
    }

    private void updatePendingList() {
        ResultSet rs;
        try {
            rs = stmt.executeQuery("select * from teacher_auth where status=0;");
            DefaultTableModel pendingModel = (DefaultTableModel) jTable11.getModel();
            pendingModel.setRowCount(0);
            while (rs.next()) {
                pendingModel.addRow(new Object[]{rs.getString("userid"), rs.getString("name"), rs.getString("subject")});
            }
        } catch (SQLException ex) {
            showException("Error occured while updating pending", ex);
        }
    }

    private void updateSearchList() {
        ResultSet rs;
        String name = jTextField10.getText();
        DefaultTableModel searchModel = (DefaultTableModel) jTable7.getModel();
        searchModel.setRowCount(0);
        try {
            if (jComboBox5.getSelectedIndex() == 0) {
                rs = stmt.executeQuery("select * from teacher_auth where name like \"%" + name + "%\";");
                while (rs.next()) {
                    searchModel.addRow(new Object[]{rs.getString("userid"), rs.getString("name"), "Teacher", rs.getString("subject")});
                }
            } else if (jComboBox5.getSelectedIndex() == 1) {
                rs = stmt.executeQuery("select * from student_auth where name like \"%" + name + "%\";");
                while (rs.next()) {
                    searchModel.addRow(new Object[]{rs.getString("userid"), rs.getString("name"), "Student"});
                }
            }
        } catch (SQLException ex) {
        }
    }

    private void updateLogs() {
        ResultSet rs = null;
        try {
            DefaultTableModel actModelTable = (DefaultTableModel) jTable6.getModel();
            DefaultTableModel errModelTable = (DefaultTableModel) jTable10.getModel();
            actModelTable.setRowCount(0);
            errModelTable.setRowCount(0);
            Calendar activityDate = Calendar.getInstance();
            activityDate.set(Calendar.YEAR, Integer.parseInt((String) jComboBox10.getSelectedItem()));
            activityDate.set(Calendar.MONTH, jComboBox9.getSelectedIndex());
            activityDate.set(Calendar.DATE, Integer.parseInt((String) jComboBox8.getSelectedItem()));
            Calendar errorDate = Calendar.getInstance();
            errorDate.set(Calendar.YEAR, Integer.parseInt((String) jComboBox12.getSelectedItem()));
            errorDate.set(Calendar.MONTH, jComboBox11.getSelectedIndex());
            errorDate.set(Calendar.DATE, Integer.parseInt((String) jComboBox13.getSelectedItem()));
            String actString = (new SimpleDateFormat("yyyyMMdd").format(activityDate.getTime()));
            String errString = (new SimpleDateFormat("yyyyMMdd").format(errorDate.getTime()));
            int searchOption = jComboBox7.getSelectedIndex();
            switch (searchOption) {
                case 0: {
                    rs = stmt.executeQuery("select * from activitylog where date(" + actString + ") = date(time);");
                }
                break;
                case 1: {
                    rs = stmt.executeQuery("select * from activitylog where date(time)<=date(" + actString + ");");
                }
                break;
                case 2: {
                    rs = stmt.executeQuery("select * from activitylog where date(time)>=date(" + actString + ");");
                }
                break;
            }
            if (rs != null) {
                while (rs.next()) {
                    actModelTable.addRow(new Object[]{rs.getString("userid"), rs.getString("activity"), rs.getString("time")});
                }
            }
            searchOption = jComboBox14.getSelectedIndex();
            switch (searchOption) {
                case 0: {
                    rs = stmt.executeQuery("select * from errorlog where date(time)=date(" + errString + ");");
                }
                break;
                case 1: {
                    rs = stmt.executeQuery("select * from errorlog where date(time)<=date(" + errString + ");");
                }
                break;
                case 2: {
                    rs = stmt.executeQuery("select * from errorlog where date(time)>=date(" + errString + ");");
                }
                break;
            }
            if (rs != null) {
                while (rs.next()) {
                    errModelTable.addRow(new Object[]{rs.getString("userid"), rs.getString("particulars"), rs.getString("time")});
                }
            }
        } catch (SQLException ex) {
            showException("Error occured while updating logs", ex);
        }
    }

    private void updateTestReport() {
        ResultSet rs, rs2;
        int totalMarks = 0;
        String testid = (String) jTable2.getValueAt(jTable2.getSelectedRow(), 0);
        String desc = (String) jTable2.getValueAt(jTable2.getSelectedRow(), 1);
        jTextField3.setText(testid);
        jTextField20.setText(desc);
        DefaultTableModel testReportModel = (DefaultTableModel) jTable5.getModel();
        testReportModel.setRowCount(0);
        try {
            rs = stmt.executeQuery("select subject from testlist where status!=-1 and testid=\"" + testid + "\";");
            if (rs.next()) {
                jTextField24.setText(rs.getString("subject"));
            }
            rs = stmt.executeQuery("select * from student_auth;");
            while (rs.next()) {
                rs2 = stmt2.executeQuery("select marksearned from studenthistorydatabase_" + rs.getString("userid") + " where testid=\"" + testid + "\";");
                if (rs2.next()) {
                    testReportModel.addRow(new Object[]{rs.getString("name"), rs2.getInt("marksearned")});
                }
            }
            int i, highest = 0, lowest = (Integer) jTable5.getValueAt(0, 1);
            for (i = 0; i < jTable5.getRowCount(); i++) {
                int curMark = (Integer) jTable5.getValueAt(i, 1);
                totalMarks += curMark;
                lowest = Math.min(lowest, curMark);
                highest = Math.max(highest, curMark);
            }
            jTextField21.setText(Double.toString(totalMarks / (i)));
            jTextField23.setText(Integer.toString(lowest));
            jTextField22.setText(Integer.toString(highest));
        } catch (ArrayIndexOutOfBoundsException ex) {
        } catch (SQLException ex) {
            showException("Error occured while updating test report", ex);
        }
    }

    private String generateTestID() {
        ResultSet rs;
        String result = null;
        String teacherSub = getTeacherSubject();
        int resultNo = 1;
        switch (teacherSub) {
            case "Business Studies":
                result = "biz_";
                break;
            case "Accountancy":
                result = "acc_";
                break;
            case "Informatics Practices":
                result = "ip_";
                break;
            case "Economics":
                result = "eco_";
                break;
            case "English":
                result = "eng_";
                break;
            case "Physics":
                result = "phy_";
                break;
            case "Chemistry":
                result = "chem_";
                break;
            case "Biology":
                result = "bio_";
                break;
            case "Mathematics":
                result = "math_";
                break;
            case "History":
                result = "his_";
                break;
            case "Geography":
                result = "geo_";
                break;
            case "Civics":
                result = "civ_";
                break;
        }
        try {
            rs = stmt.executeQuery("select testid from testlist where subject=\"" + teacherSub + "\" order by testid desc;");
            if (rs.next()) {
                String tempTokens[] = rs.getString("testid").split("_");
                resultNo = Integer.parseInt(tempTokens[1]) + 1;
            }
        } catch (SQLException ex) {
            showException("Error occured while generating Test ID", ex);
        }
        result += Integer.toString(resultNo);
        return result;
    }

    private String getTeacherSubject() {
        ResultSet rs;
        String subject = null;
        try {
            rs = stmt.executeQuery("select subject from teacher_auth where userid=\"" + loginID + "\";");
            if (rs.next()) {
                subject = rs.getString("subject");
            }
        } catch (SQLException ex) {
            showException("Error occured while fetching subject from" + loginID, ex);
        }
        return subject;
    }

    private void cleanUpAfterTest() {
        try {
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
        } catch (NullPointerException ex) {
        }
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
        }
        if (!alreadyAnswered) {
            answeredList.add(curQuesInd);
            totalAnsweredQuestions++;
        }
        String temp[];
        try {
            String questionLine = questionListMod.get(curQuesInd);
            temp = questionLine.split(separator);
            try {
                stmt.executeUpdate("alter table studenthistorydatabase_" + loginID + " add column question_" + temp[1] + " varchar(1) default \"x\";");
            } catch (SQLException ex) {
            }
            stmt.executeUpdate("update studenthistorydatabase_" + loginID + " set question_" + temp[1] + " = \"" + answer + "\" where testid=\"" + currentTestID + "\";");
            jLabel70.setText("Answer saved");
        } catch (SQLException ex) {
            showException("Error occured while updating answer", ex);
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
            logActivity(loginID, "User Started Test");
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
            int listSize = questionListMod.size();
            DefaultTableModel questionListModel = (DefaultTableModel) jTable8.getModel();
            questionListModel.setRowCount(0);
            for (int j = 1; j <= listSize; j++) {
                questionListModel.addRow(new Object[]{"Question " + j, "No"});
            }
            curQuesInd = 0;
            setNextQuestion(curQuesInd);
        } catch (SQLException ex) {
            showException("Error occured whilie initiating test", ex);
        }
    }

    private void setNextQuestion(int i) { //updates the text area with the question as per index
        String questionLine = questionListMod.get(i);
        String[] tempTokens = questionLine.split(separator);
        imageDisplayPage.setVisible(false);
        imageDisplayed = false;
        File img;
        try {
            int ind = Integer.parseInt(tempTokens[1]);
            ResultSet rs = stmt.executeQuery("select imagesource from testquestions_" + currentTestID + " where sno=" + ind + ";");
            if (rs.next()) {
                img = new File(rs.getString("imagesource"));
                if (img.exists()) {
                    ImageIcon icon = new ImageIcon(img.getCanonicalPath());
                    jScrollPane18.setPreferredSize(new Dimension(icon.getIconHeight() + 50, icon.getIconWidth() + 50));
                    jLabel89.setPreferredSize(new Dimension(icon.getIconHeight() + 25, icon.getIconWidth() + 25));
                    jLabel89.setIcon(icon);
                    imageDisplayPage.pack();
                    imageDisplayed = true;
                    imageDisplayPage.setVisible(true);
                    imageDisplayPage.setAlwaysOnTop(true);
                    imageDisplayPage.setAlwaysOnTop(false);
                    jLabel87.setText("This question has an image: ");
                    jLabel87.setForeground(new Color(255, 0, 0));
                    jButton56.setEnabled(true);
                } else {
                    jLabel87.setText("This question does not have an image.");
                    jLabel87.setForeground(new Color(0, 0, 0));
                    jButton56.setEnabled(false);
                }
            }
        } catch (SQLException | IOException ex) {
            showException("Error occured while testing to see if question has an image.", ex);
        }
        jTextArea1.setText(tempTokens[2]);
        jTextField13.setText(tempTokens[0]);
        updateQuestionProgress();
    }

    private void logActivity(String username, String event) {
        java.util.Date datetime = new java.util.Date();
        SimpleDateFormat format;
        try {
            format = new SimpleDateFormat("yyyyMMddHHmmss");
            stmt.executeUpdate("insert into activitylog values (\"" + username + "\", \"" + event + "\",\"" + format.format(new java.util.Date()) + "\");");
        } catch (SQLException ex) {
            showException("Failed Activity logging", ex);
        }
    }

    private void updateStudentTestList() {
        ResultSet rs, rs2;
        String selectedSubject, statusRep = "Error";
        String subject = null;
        try {
            if (jComboBox1.getSelectedIndex() == 0) {
                rs = stmt.executeQuery("select * from testlist where status!=-1 and standard=" + loginStandard + " order by status desc");
            } else {
                selectedSubject = (String) (jComboBox1.getSelectedItem());
                rs = stmt.executeQuery("select * from testlist where status!=-1 and subject=\"" + selectedSubject + "\" and standard=" + loginStandard + " order by status desc;");
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
                rs2 = stmt2.executeQuery("select * from studenthistorydatabase_" + loginID + " where testid=\"" + testid + "\";");
                if (rs2.next()) {
                    statusRep = "Taken";
                }
                rs2 = stmt2.executeQuery("select * from testlist where status!=-1 and testid=\"" + testid + "\";");
                if (rs2.next()) {
                    subject = rs2.getString("subject");
                }
                studentTestTableModel.addRow(new Object[]{testid, testName, statusRep, subject});
                int pendingTotal = 0;
                for (int i = 0; i < jTable1.getRowCount(); i++) {
                    try {
                        String x = (String) jTable1.getValueAt(i, 2);
                        if (x.equals("Active")) {
                            pendingTotal++;
                        }
                    } catch (NullPointerException ex) {
                    }
                }
                jTextField12.setText(Integer.toString(pendingTotal));
            }
        } catch (SQLException ex) {
            showException("Error while fetching student test list", ex);
        }
    }

    private boolean checkValidCharsUsed(String a, boolean spaceDotAllowed, boolean numbersAllowed) {
        char tempChar;
        for (int i = 0; i < a.length(); i++) {
            tempChar = a.toUpperCase().charAt(i);
            if ((tempChar == 32 || tempChar == 46) || (tempChar >= 48 && tempChar <= 57) || (tempChar >= 65 && tempChar <= 90) || (tempChar >= 97 && tempChar <= 122)) {
                if (tempChar >= 48 && tempChar <= 57) {
                    if (!numbersAllowed) {
                        return false;
                    }
                }
                if (tempChar == 32 || tempChar == 46) {
                    if (!spaceDotAllowed) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private int getUserType() {
        return PRESENTUSERTYPE;
    }

    private boolean isUserStudent() {
        return PRESENTUSERTYPE == TYPE_STUDENT;
    }

    private boolean isUserTeacher() {
        return PRESENTUSERTYPE != TYPE_STUDENT;
    }

    private boolean abortTest() {
        canCheat=false;
        int result = JOptionPane.showConfirmDialog(studentQuestionPage, "Are you sure you want to abort test? You will not be able to re-take this test.", "Abort confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            try {
                stmt.executeUpdate("update studenthistorydatabase_" + loginID + " set aborted=1 where testid=\"" + currentTestID + "\";");
                logActivity(loginID, "User Aborted Test");
                studentQuestionPage.dispose();
                finishTest();
                cleanUpAfterTest();
                studentPanelPage.setVisible(true);
                return true;
            } catch (SQLException ex) {
                showException("Error occured while aborting", ex);
            }
        }
        canCheat = true;
        return false;
    }

    private void showException(String a, Exception ex) {
        logError(a, ex);
        if (isUserStudent()) {
            JOptionPane.showMessageDialog(null, a + "\n\nPlease contact system administrator for more details and to resolve issue.", "Internal error occured.", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, a + "\n\nPlease contact administrator to resolve issue. \nFor more details, check error log located at:\n" + logLocation, "Internal error occured", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        try {
            if (isUserStudent()) {
                stmt.executeUpdate("update student_auth set onlinestatus=0 where userid=\"" + loginID + "\";");
            } else if (isUserTeacher()) {
                stmt.executeUpdate("update teacher_auth set onlinestatus=0 where userid=\"" + loginID + "\";");
            }
            disposeAllFrames();
            loginPage.setVisible(true);
        } catch (SQLException ex) {
            showException("Error occured while signing user out", ex);
        }
        wakeUpTimer.cancel();
        logActivity(loginID, "User logged out");
    }

    private void logError(String a, Exception ex) {
        SimpleDateFormat format;
        try {
            format = new SimpleDateFormat("yyyyMMddHHmmss");
            if (loginID != null) {
                stmt.executeUpdate("insert into errorlog values(\"" + loginID + "\",\"" + ex.getMessage() + "\"," + format.format(new java.util.Date()) + ");");
            } else {
                stmt.executeUpdate("insert into errorlog values(\"" + "SYSTEM" + "\",\"" + ex.getMessage() + "\"," + format.format(new java.util.Date()) + ");");
            }
        } catch (SQLException | NullPointerException e) {
            if (con != null) {
                JOptionPane.showMessageDialog(null, "An error occured while updating error database. Check Error log located at:\n" + logLocation, "Error Logging error", JOptionPane.ERROR_MESSAGE);
            }
        }
        try (FileWriter fw = new FileWriter(logLocation, true);) {
            format = new SimpleDateFormat("yyyy MMMMM dd - HH:mm:ss");
            fw.write("Error Log Time: " + format.format(new java.util.Date()));
            fw.write(System.getProperty("line.separator"));
            fw.write("Error message(Annotated): " + a);
            fw.write(System.getProperty("line.separator"));
            fw.write("Error message(System): " + ex.getMessage());
            fw.write(System.getProperty("line.separator"));
            fw.write("Error occured for user: " + loginID);
            fw.write(System.getProperty("line.separator"));
            StackTraceElement logLines[] = ex.getStackTrace();
            for (StackTraceElement x : logLines) {
                fw.write(x.toString());
                fw.write(System.getProperty("line.separator"));
            }
            fw.write("End");
            fw.write(System.getProperty("line.separator"));
            fw.write("___________");
            fw.write(System.getProperty("line.separator"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error occured while trying to log errors.");
        }
    }

    private void disposeAllFrames() {
        for (JFrame x : allWindowList) {
            x.dispose();
        }
    }

    private void updateTeacherTestList() {
        ResultSet rs, rs2;
        int selInd = jComboBox4.getSelectedIndex();
        try {
            int noQuestions = 0;
            if (selInd == 0) {
                rs = stmt.executeQuery("select * from testlist where status!=-1 and userid=\"" + loginID + "\";");
            } else {
                rs = stmt.executeQuery("select * from testlist where status!=-1 ;");
            }
            DefaultTableModel teacherTable = (DefaultTableModel) jTable2.getModel();
            teacherTable.setRowCount(0);
            while (rs.next()) {
                String desc = rs.getString("description");
                String status = "Error";
                int statusTemp = rs.getInt("status");
                switch (statusTemp) {
                    case 0:
                        status = "Locked";
                        break;
                    case 1:
                        status = "Active";
                        break;
                }
                String testid = rs.getString("testid");
                String time = rs.getString("datecreated");
                String allottedTime = Integer.toString(rs.getInt("seconds") / 60);
                rs2 = stmt2.executeQuery("select count(*) from testquestions_" + testid + ";");
                if (rs2.next()) {
                    noQuestions = rs2.getInt(1);
                }
                int marksPQ = rs.getInt("points");
                int std = rs.getInt("standard");
                teacherTable.addRow(new Object[]{testid, desc, status, time, noQuestions, allottedTime, marksPQ, std});
            }
        } catch (SQLException ex) {
            showException("Error occured while updating teacher table", ex);
        }
    }

    private void initDatabaseSettingsWizard() {
        generateKeyPage.setTitle("Database access settings");
        generateKeyPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        generateKeyPage.setResizable(false);
        generateKeyPage.pack();
        generateKeyPage.setLocationRelativeTo(null);
        generateKeyPage.setVisible(true);
    }

    public static void main(String args[]) {

        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClassTest.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        try {
            if (args != null) {
                switch (args[0]) {
                    case "GenerateKey010":
                        correctKeyEntered = true;
                        break;
                    case "Reset010":
                        resetKeyEntered = true;
                        break;
                    case "ApproveTeachers010":
                        teacherKeyEntered = true;
                        break;
                    default:
                        break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClassTest();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFrame adminPage;
    private javax.swing.JFrame antiCheatFrame;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JFrame cheatAlarmPage;
    private javax.swing.JFrame displayReportQuestionPage;
    private javax.swing.JFrame editQuestionBridge;
    private javax.swing.JFrame editQuestionPage;
    private javax.swing.JFrame friendlyRetestPage;
    private javax.swing.JFrame generateKeyPage;
    private javax.swing.JFrame imageDisplayPage;
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
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButton55;
    private javax.swing.JButton jButton56;
    private javax.swing.JButton jButton57;
    private javax.swing.JButton jButton58;
    private javax.swing.JButton jButton59;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton60;
    private javax.swing.JButton jButton61;
    private javax.swing.JButton jButton62;
    private javax.swing.JButton jButton63;
    private javax.swing.JButton jButton64;
    private javax.swing.JButton jButton65;
    private javax.swing.JButton jButton66;
    private javax.swing.JButton jButton67;
    private javax.swing.JButton jButton68;
    private javax.swing.JButton jButton69;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox10;
    private javax.swing.JComboBox jComboBox11;
    private javax.swing.JComboBox jComboBox12;
    private javax.swing.JComboBox jComboBox13;
    private javax.swing.JComboBox jComboBox14;
    private javax.swing.JComboBox<String> jComboBox15;
    private javax.swing.JComboBox<String> jComboBox16;
    private javax.swing.JComboBox<String> jComboBox17;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JComboBox jComboBox9;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
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
    private javax.swing.JLabel jLabel36;
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
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JList jList2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu12;
    private javax.swing.JMenu jMenu13;
    private javax.swing.JMenu jMenu14;
    private javax.swing.JMenu jMenu15;
    private javax.swing.JMenu jMenu16;
    private javax.swing.JMenu jMenu17;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar10;
    private javax.swing.JMenuBar jMenuBar11;
    private javax.swing.JMenuBar jMenuBar12;
    private javax.swing.JMenuBar jMenuBar13;
    private javax.swing.JMenuBar jMenuBar14;
    private javax.swing.JMenuBar jMenuBar15;
    private javax.swing.JMenuBar jMenuBar16;
    private javax.swing.JMenuBar jMenuBar17;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JMenuBar jMenuBar4;
    private javax.swing.JMenuBar jMenuBar5;
    private javax.swing.JMenuBar jMenuBar6;
    private javax.swing.JMenuBar jMenuBar7;
    private javax.swing.JMenuBar jMenuBar8;
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
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem35;
    private javax.swing.JMenuItem jMenuItem36;
    private javax.swing.JMenuItem jMenuItem37;
    private javax.swing.JMenuItem jMenuItem38;
    private javax.swing.JMenuItem jMenuItem39;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem40;
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
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
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
    private javax.swing.JPasswordField jPasswordField8;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable10;
    private javax.swing.JTable jTable11;
    private javax.swing.JTable jTable12;
    private javax.swing.JTable jTable13;
    private javax.swing.JTable jTable14;
    private javax.swing.JTable jTable15;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    private javax.swing.JTable jTable9;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField35;
    private javax.swing.JTextField jTextField36;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JFrame logPage;
    private javax.swing.JFrame loginPage;
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
    private javax.swing.JFrame userHistoryPage;
    // End of variables declaration//GEN-END:variables
}
