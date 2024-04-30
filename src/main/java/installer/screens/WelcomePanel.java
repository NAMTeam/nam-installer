package installer.screens;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import installer.Settings;
import installer.JarProperties;
import installer.mainframe.InstallerTabs;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;

public class WelcomePanel extends Box {

  public WelcomePanel(final InstallerTabs tabs){
    super(SwingConstants.VERTICAL);
    JTextPane text = new JTextPane();
    text.setOpaque(false);
    String licence = JarProperties.getLicence();
    if (licence != null){
      if (licence.toLowerCase().startsWith("<html>")){
        text.setEditorKit(new HTMLEditorKit());
      } else {
        text.setEditorKit(new StyledEditorKit());
      }
      text.setText(licence);
    }
    text.setEditable(false);
    text.setBorder(new TitledBorder(new LineBorder(Settings.getColor("default.dark")), ""));
    text.addHyperlinkListener(new HyperlinkListener() {
      @Override
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          try {
            if (java.awt.Desktop.isDesktopSupported()) {
              java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
            } else {
              System.err.println("Cannot open hyperlink " + e.getURL());
            }
          } catch(java.net.URISyntaxException | java.io.IOException ex) {
            System.err.println("Cannot open hyperlink " + e.getURL());
          }
        }
      }
    });

    Box textBox = Box.createHorizontalBox();
    textBox.add(Box.createHorizontalStrut(5));
    textBox.add(text);
    textBox.add(Box.createHorizontalStrut(5));

    this.add(Box.createVerticalStrut(3));
    this.add(textBox);

    final JLabel contLabel = new JLabel(Settings.getString("welcome.next"));
    contLabel.setVisible(false);
    Box checkBoxBox = Box.createHorizontalBox();
    checkBoxBox.add(Box.createHorizontalStrut(5));
    checkBoxBox.add(contLabel);
    checkBoxBox.add(Box.createHorizontalGlue());
    final JCheckBox checkBox = new JCheckBox(Settings.getString("welcome.i_agree"));
    checkBox.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        tabs.licenceAgreed(checkBox.isSelected());
        contLabel.setVisible(checkBox.isSelected());
      }
    });
    checkBox.setOpaque(false);
    checkBoxBox.add(checkBox);

    this.add(Box.createVerticalGlue());
    this.add(checkBoxBox);
  }

}
