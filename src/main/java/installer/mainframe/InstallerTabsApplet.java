package installer.mainframe;

import installer.JarProperties;
import installer.Settings;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class InstallerTabsApplet extends javax.swing.JApplet {
	
	/* TODO lots of stuff ... */

	@Override public void init(){
		super.init();
		JarProperties.get();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.add(new InstallerTabs());
			this.setPreferredSize(new Dimension(Settings.getInt("width"),Settings.getInt("height")));
			this.setSize(this.getPreferredSize());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
}
