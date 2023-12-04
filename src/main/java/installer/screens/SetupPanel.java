package installer.screens;

import installer.Settings;
import installer.mainframe.*;
import installer.screens.scanner.FileSearcher;
import installer.screens.scanner.Scanner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

public class SetupPanel extends Box implements ActionListener, FileSearcher {
  // combines cleanitol and dependency panels

  private JButton startButton;
  private JTabbedPane tabs;

  private CleanitolPanel clean;
  private DependencyPanel depends;

  public SetupPanel(){
    super(SwingConstants.VERTICAL);

    startButton = new JButton(Settings.getString("depends.start"));
    startButton.addActionListener(this);
    startButton.setOpaque(false);
    Box startButtonBox = Box.createHorizontalBox();

    startButtonBox.add(startButton);
    startButtonBox.add(Box.createHorizontalGlue());

    this.add(Box.createVerticalStrut(2));
    this.add(startButtonBox);

    tabs = new JTabbedPane();
    tabs.addTab(Settings.getString("cleanitol.title"), clean = new CleanitolPanel(false));
    tabs.addTab(Settings.getString("depends.title"), depends = new DependencyPanel(false));
    new InstallerTabUI(tabs);

    this.add(tabs);

  }

  private Scanner scanner;

  @Override
  public void actionPerformed(ActionEvent ev) {
    if (ev.getSource().equals(startButton)){
      if (ev.getActionCommand().equals(Settings.getString("depends.start"))){
        Set<String> files = Settings.dependencyFiles.keySet();
        List<String> list[] = new List[]{new ArrayList<String>(files), Settings.cleanitolFiles,
            new ArrayList()};
        scanner = new Scanner(list, new FileSearcher[]{depends, clean, this});
        scanner.startScan(new File(Settings.PLUGINS_FOLDER));
      } else if (ev.getActionCommand().equals("depends.stop")){
        if (scanner != null){
          scanner.stopScan();
        }
      }
    }
  }

  @Override
  public void found(File file) {
    // TODO Auto-generated method stub

  }


  @Override
  public void searchAborted() {
    startButton.setText(Settings.getString("depends.start"));

  }

  @Override
  public void searchFinished() {
    startButton.setEnabled(false);
    startButton.setText(Settings.getString("depends.done"));

  }

  @Override
  public void searchStarted() {
    startButton.setText(Settings.getString("depends.stop"));

  }

}
