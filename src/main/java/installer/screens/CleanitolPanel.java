package installer.screens;

import installer.Settings;
import installer.screens.scanner.FileSearcher;
import installer.screens.scanner.Scanner;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class CleanitolPanel extends Box implements ActionListener, FileSearcher {

  private JButton startButton, deleteButton, backupButton;
  private JTextArea textArea;

  public CleanitolPanel(){
    this(true);
  }

  public CleanitolPanel(boolean withStartButton) {
    super(SwingConstants.VERTICAL);

    startButton = new JButton(Settings.getString("cleanitol.start"));
    if (withStartButton){
      startButton.setOpaque(false);
      startButton.addActionListener(this);
      Box startButtonBox = Box.createHorizontalBox();
      startButtonBox.add(Box.createRigidArea(new Dimension(1,2)));
      startButtonBox.add(startButton);
      startButtonBox.add(Box.createHorizontalGlue());

      this.add(Box.createVerticalStrut(2));
      this.add(startButtonBox);
    }

    textArea = new JTextArea();
    textArea.setEditable(false);
    //textArea.setOpaque(false);
    textArea.setForeground(Settings.getColor("textfield.text"));
    textArea.setBackground(Settings.getColor("textfield.back"));

    JPanel p = new JPanel();
    p.setOpaque(false);
    p.add(textArea);

    JScrollPane scroll = new JScrollPane();
    scroll.getViewport().add(p);
    scroll.getViewport().setBackground(Settings.getColor("textfield.back"));
    //scroll.getViewport().setOpaque(false);
    scroll.setPreferredSize(new Dimension(1000,1000));
    scroll.setOpaque(false);

    Box scrollBox = Box.createHorizontalBox();
    scrollBox.add(Box.createRigidArea(new Dimension(1,2)));
    scrollBox.add(scroll);
    scrollBox.setOpaque(false);

    deleteButton = new JButton(Settings.getString("cleanitol.delete"));
    deleteButton.setOpaque(false);
    deleteButton.addActionListener(this);
    deleteButton.setEnabled(false);
    backupButton = new JButton(Settings.getString("cleanitol.backup"));
    backupButton.setOpaque(false);
    backupButton.addActionListener(this);
    backupButton.setEnabled(false);

    Box actionBox = Box.createHorizontalBox();
    actionBox.add(Box.createHorizontalStrut(1));
    actionBox.add(deleteButton);
    actionBox.add(Box.createHorizontalStrut(1));
    actionBox.add(backupButton);
    actionBox.add(Box.createHorizontalStrut(3));
    //actionBox.add(new JLabel("C:\\Users\\Joris\\Documents\\Simcity 4\\Backup_Plugins\\"));
    actionBox.add(Box.createHorizontalGlue());

    this.add(Box.createVerticalStrut(2));
    this.add(scrollBox);
    this.add(Box.createVerticalStrut(2));
    this.add(actionBox);
  }

  private List<File> found;

  public void found(File f){
    found.add(f);
    append(f.toString());
  }

  private int delete(){
    int t = 0;
    if (found != null){
      List<File> deleted = new ArrayList<File>();
      for (File f : found){
        if (f.delete()){
          t++;
          deleted.add(f);
        }
      }
      found.removeAll(deleted);
    }
    return t;
  }

  private int move(){
    int t = 0;
    if (found != null){
      List<File> moved = new ArrayList<File>();
      File backupdir = new File(Settings.CLEANITOL_BACKUP);
      if (!backupdir.exists()){
        backupdir.mkdir();
      }
      for(File f: found){
        File newFile = new File(Settings.CLEANITOL_BACKUP + "\\" + f.getName());
        if (!newFile.exists()){
          if (f.renameTo(newFile)){
            t++;
            moved.add(f);
          }
        }
      }
      found.removeAll(moved);
    }
    return t;
  }

  private void error(){
    if (found != null && found.size() > 0){
      StringBuffer text = new StringBuffer();
      text.append("\n-------------------------------------------\n");
      text.append(Settings.getString("cleanitol.error"));
      text.append("\n");
      for(File f: found){
        text.append(f.getPath());
        text.append("\n");
      }
      append(text.toString());
    }
  }

  private void append(String str){
    textArea.append(str + "\n");
    textArea.setCaretPosition(textArea.getDocument().getLength());
  }

  private Scanner scanner;

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(backupButton)){
      int n = found.size();
      int m = move();
      if (m < n){
        error();
      }
      JOptionPane.showMessageDialog(this, m + " " + Settings.getString("cleanitol.backup.popup")
          + "\n" + Settings.CLEANITOL_BACKUP);
      backupButton.setEnabled(false);
      deleteButton.setEnabled(false);
    } else if (e.getSource().equals(deleteButton)){
      int n = found.size();
      int d = delete();
      if (d < n){
        error();
      }
      JOptionPane.showMessageDialog(this, d  + " " + Settings.getString("cleanitol.delete.popup"));
      backupButton.setEnabled(false);
      deleteButton.setEnabled(false);
    } else if (e.getSource().equals(startButton)){
      if (e.getActionCommand().equals(Settings.getString("cleanitol.start"))){
        File root = new File(Settings.PLUGINS_FOLDER);
        scanner = new Scanner(Settings.cleanitolFiles, this);
        scanner.startScan(root);
      } else if (e.getActionCommand().equals(Settings.getString("cleanitol.stop"))){
        scanner.stopScan();
        startButton.setText(Settings.getString("cleanitol.start"));
      }
    }

  }

  @Override
  public void searchAborted() {
    startButton.setText(Settings.getString("cleanitol.start"));
  }

  @Override
  public void searchFinished() {
    startButton.setText(Settings.getString("cleanitol.done"));
    startButton.setEnabled(false);
    StringBuffer text = new StringBuffer();
    text.append("-------------------------------------------\n");
    text.append(found.size() + " " + Settings.getString("cleanitol.found") + "\n");
    if (found.size() > 0){
      text.append(Settings.getString("cleanitol.msg"));
      deleteButton.setEnabled(true);
      backupButton.setEnabled(true);
    }
    append(text.toString());
  }

  @Override
  public void searchStarted() {
    found = new Vector<File>();
    startButton.setText(Settings.getString("cleanitol.stop"));
  }


}
