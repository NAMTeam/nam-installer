package installer.mainframe;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import installer.Settings;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class QuitDialogJFrame extends JFrame {

  public QuitDialogJFrame(){
    this("");
  }

  public QuitDialogJFrame(String title){
    super(title);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }

  @Override
  public void dispose(){
    if (!Settings.installerFinished){
      int n = JOptionPane.showConfirmDialog(
          this,
          "Are you sure you want to quit?",
          "Quit?",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE);
      if (n == JOptionPane.YES_OPTION){
        super.dispose();
      }
    } else {
      if (Settings.getString("on_finish.open") != null){
        File doc = new File(Settings.INSTALL_DESTINATION + "/" + Settings.getString("on_finish.open"));
        if (doc.exists()){
          try {
            Desktop.getDesktop().open(doc);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
      if (Settings.getString("on_finish.run") != null &&
          new File(Settings.getString("on_finish.run")).exists()){
        try {
          Runtime.getRuntime().exec(Settings.getString("on_finish.run"),
              new String[]{}, new File(Settings.INSTALL_DESTINATION));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      super.dispose();
    }
  }

}
