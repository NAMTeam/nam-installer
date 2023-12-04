package installer.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import installer.Settings;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class InstallLocationPanel extends Box implements ActionListener {

	JTextField field;
	
	public InstallLocationPanel() {
		super(SwingConstants.VERTICAL);
		
		
		JLabel label = new JLabel("Please select a location to install files");
		Box labelBox = Box.createHorizontalBox();
		labelBox.add(Box.createRigidArea(new Dimension(5,2)));
		labelBox.add(label);
		labelBox.add(Box.createHorizontalGlue());
				
		
		JButton browse = new JButton(Settings.getString("location.button"));
		browse.setForeground(Settings.getColor("buttons.text"));
		browse.setBackground(Settings.getColor("default.light"));
		browse.setOpaque(false);
		browse.addActionListener(this);
		field = new JTextField(Settings.INSTALL_DESTINATION);
		field.setMaximumSize(new Dimension(1000, browse.getPreferredSize().height));
		field.setEditable(false);
		field.setBackground(Settings.getColor("textfield.back"));
		field.setForeground(Settings.getColor("textfield.text"));
		
		Box browseBox = Box.createHorizontalBox();
		browseBox.add(Box.createRigidArea(new Dimension(2,2)));
		browseBox.add(field);
		browseBox.add(Box.createRigidArea(new Dimension(2,2)));
		browseBox.add(browse);
		//browseBox.setBorder(new TitledBorder(Settings.getString("location.border")));
		
		this.add(Box.createVerticalStrut(5));
		this.add(labelBox);
		this.add(Box.createVerticalStrut(4));
		this.add(browseBox);
		this.add(Box.createVerticalGlue());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser(field.getText());	    
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       field.setText(chooser.getSelectedFile().getPath());
	       Settings.INSTALL_DESTINATION = chooser.getSelectedFile().getPath();
	    }
		
	}

}
