package installer.screens;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import installer.JarProperties;
import installer.Settings;
import installer.tree.JarEntryNode;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class InstallProgressPanel extends Box implements ActionListener {

	private JTextPane textPane; 
	private StringBuffer text;
	private JButton startButton;

	public InstallProgressPanel(){
		super(SwingConstants.VERTICAL);

		startButton = new JButton(Settings.getString("install.button"));
		//startButton.setForeground(Settings.getColor("buttons.text"));
		startButton.setOpaque(false);
		startButton.addActionListener(this);

		text = new StringBuffer();
		
		Box startButtonBox = Box.createHorizontalBox();
		startButtonBox.add(startButton);
		startButtonBox.add(Box.createHorizontalGlue());
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		//textPane.setOpaque(false);
		textPane.setForeground(Settings.getColor("textfield.text"));
		textPane.setBackground(Settings.getColor("textfield.back"));

		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(textPane);
		scroll.getViewport().setOpaque(false);
		scroll.setOpaque(false);
		
		Box scrollBox = Box.createHorizontalBox();
		scrollBox.add(Box.createHorizontalStrut(1));
		scrollBox.add(scroll);
		scrollBox.setOpaque(false);

		this.add(Box.createVerticalStrut(2));
		this.add(startButtonBox);
		this.add(Box.createVerticalStrut(2));
		this.add(scrollBox);
		this.setOpaque(false);

	}

	// avoids having to re-allocate a buffer for each file
	// access should be synchronized in multi-threaded environment!
	private static byte[] IOBUFFER = new byte[1024];

	private void install(String dest, JarEntryNode node, boolean createSubfolders) {
		if (!node.isSelected()){
			return;
		}
		File destFolder = new File(dest);
		if (!destFolder.exists()){
			destFolder.mkdirs();
			append(destFolder.toString());
		}
		if (node.getInstallNodes() != null){
			for (JarEntryNode n : node.getInstallNodes()){
				File destFile  = new File(dest + File.separator + n.toString());
				if (destFile.exists()){
					/* TODO check if file from installer is newer instead of always overwriting */
					destFile.delete();
				}
				try {
					BufferedInputStream in = new BufferedInputStream(
							JarProperties.getJarFile().getInputStream(n.getEntry()));
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
					int amountRead;
					while ((amountRead = in.read(IOBUFFER)) != -1) {
						out.write(IOBUFFER, 0, amountRead); 
					}
					append(destFile.toString());
					in.close();
					out.close();
				} catch (IOException e){
					System.err.println("failed installing "+destFile);
				}
			}	
		}
		for (int i = 0; i < node.getChildCount() ; i++){
			JarEntryNode sub = (JarEntryNode) node.getChildAt(i);
			if (createSubfolders){
				install(dest + File.separator + sub.toString(), sub, createSubfolders && sub.allowsSubfolders());
			} else {
				install(dest, sub, false);
			}
		}
	}

	private void append(String str){
		text.append(str);
		text.append("\n");
		textPane.setText(text.toString());
		textPane.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// start installation
		if (Settings.installerFinished){
			return;
		}
		startButton.setEnabled(false);
		Thread run = new Thread(){
			public void run(){
				final String rootFolder = Settings.INSTALL_DESTINATION;
				final JarEntryNode node = JarProperties.getRoot();
				
				install(rootFolder, node, true);
				
				//startButton.setEnabled(true);
				append("----------------------------------");
				append("All selected components installed!");
				Settings.installerFinished = true;
			}
		};
		run.start();
	}



}
