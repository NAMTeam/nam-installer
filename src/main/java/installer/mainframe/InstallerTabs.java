package installer.mainframe;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import installer.Settings;
import installer.JarProperties;
import installer.screens.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;


public class InstallerTabs extends JTabbedPane {


	Rectangle closeRect, iconifyRect;
	Rectangle selected;

	private final static Point CLOSERECTMARGIN = new Point(5,3);

	public InstallerTabs() throws IOException{
		this.setOpaque(false);
		if (Settings.hasLicense){
			this.addTab(Settings.getString("welcome.title"), new WelcomePanel(this));
		}
		//this.addTab("locale", new JLabel());
		if (Settings.getBoolean("showFiles")){
			this.addTab(Settings.getString("files.title"), new BrowseFilesPanel());		
		}
		this.addTab(Settings.getString("location.title"), new InstallLocationPanel());
		if (Settings.cleanitolFiles != null && Settings.dependencyFiles != null){
			this.addTab(Settings.getString("setup.title"), new SetupPanel());
		} else if (Settings.cleanitolFiles != null){
			this.addTab(Settings.getString("cleanitol.title"), new CleanitolPanel());
		} else if(Settings.dependencyFiles != null){
			this.addTab(Settings.getString("depends.title"), new DependencyPanel());
		}
		this.addTab(Settings.getString("install.title"), new InstallProgressPanel());

		new InstallerTabUI(this);	

		if (Settings.hasLicense){
			for (int i = 1; i < this.getComponentCount(); i++){
				this.setEnabledAt(i, false);
			}
		}

		this.setPreferredSize(new Dimension(Settings.getInt("width"),Settings.getInt("height")));
		this.setSize(this.getPreferredSize());

		if (!Settings.getBoolean("decorate")){
			// if window is not decorated we add our own min/close buttons
			closeRect = new Rectangle(0,0,14,14);
			iconifyRect = new Rectangle(0,0,14,14);

			this.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent ev){
					if (closeRect.getBounds().contains(ev.getPoint())){
						JFrame top = (JFrame) InstallerTabs.this.getTopLevelAncestor();
						top.dispose();
					} else if (iconifyRect.getBounds().contains(ev.getPoint())){
						JFrame top = (JFrame) InstallerTabs.this.getTopLevelAncestor();
						top.setState(JFrame.ICONIFIED);
					}
				}
			});

			this.addMouseMotionListener(new MouseMotionAdapter(){
				@Override
				public void mouseMoved(MouseEvent ev) {
					if (closeRect.getBounds().contains(ev.getPoint())){
						InstallerTabs.this.setToolTipText(Settings.getString("frame.close"));
						selected = closeRect;
					} else if (iconifyRect.getBounds().contains(ev.getPoint())){
						InstallerTabs.this.setToolTipText(Settings.getString("frame.minimize"));
						selected = iconifyRect;
					} else {
						InstallerTabs.this.setToolTipText(null);
						selected = null;
					}
					InstallerTabs.this.repaint();
				}			
			});

		}


	}

	public void licenceAgreed(boolean b){
		for (int i = 1; i < this.getComponentCount(); i++){
			this.setEnabledAt(i, b);
		}
	}


	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g);

		if (closeRect != null){
			closeRect.setLocation(this.getWidth() - closeRect.width - CLOSERECTMARGIN.x, CLOSERECTMARGIN.y);
			iconifyRect.setLocation(closeRect.x - iconifyRect.width - 3, closeRect.y);

			g2.setColor(Settings.getColor("default.dark"));
			if (closeRect == selected){
				g2.fill(closeRect);
				g2.setColor(Settings.getColor("default.light"));
			} else {			
				g2.draw(closeRect);
			}
			g2.drawLine(closeRect.x, closeRect.y, closeRect.x + closeRect.width, closeRect.y + closeRect.height);
			g2.drawLine(closeRect.x + closeRect.width, closeRect.y, closeRect.x, closeRect.y + closeRect.height);

			g2.setColor(Settings.getColor("default.dark"));
			if (iconifyRect == selected){
				g2.fill(iconifyRect);
				g2.setColor(Settings.getColor("default.light"));
			} else {
				g2.draw(iconifyRect);				
			}
			g2.drawLine(iconifyRect.x + 3, iconifyRect.y + iconifyRect.height - 3, 
					iconifyRect.x + iconifyRect.width - 3, iconifyRect.y + iconifyRect.height - 3);
		}
	}


	public static void main(String args[]) throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		
		StaticImageBox splashBox = null;
		JFrame splashFrame = null;
		long startSplash = 0;

		try {
			// new File(Settings.splash))
			splashBox = new StaticImageBox(ImageIO.read(InstallerTabs.class.getClassLoader().getResourceAsStream(Settings.splash)));
			Settings.hasSplash = true;
		} catch(Exception e){
			Settings.hasSplash = false;
		}	

		if (Settings.hasSplash){
			splashFrame = new JFrame();
			splashFrame.setUndecorated(true);
			splashFrame.getContentPane().add(splashBox);
			splashFrame.setVisible(true);
			splashFrame.pack();
			splashFrame.setLocationRelativeTo(null);
			startSplash = System.currentTimeMillis();
		}

		JarProperties.get();

		if (Settings.hasSplash){
			while(System.currentTimeMillis() < startSplash + (long) Settings.getInt("splash_ms")){
				Thread.yield();
			}		
			splashFrame.dispose();
		}

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		UIManager.put("TabbedPane.selected", Settings.getColor("default.light"));
		//UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		ToolTipManager.sharedInstance().setInitialDelay(0);

		JFrame f = new QuitDialogJFrame(Settings.getString("apptitle"));
		f.setBackground(Settings.getColor("default.light"));	

		Box box = Box.createHorizontalBox();
		box.add(new InstallerTabs());

		Box contentPane = Box.createHorizontalBox();
		if (Settings.Sidebar != null){
			contentPane.add(new StaticImageBox(Settings.Sidebar));
		}
		contentPane.add(box);

		f.setContentPane(contentPane);
		f.setUndecorated(!Settings.getBoolean("decorate"));
		f.setVisible(true);
		f.pack();
		f.setLocationRelativeTo(null);

		if (Settings.getBoolean("decorate")){
			((JComponent) f.getContentPane()).setBorder(new LineBorder(Settings.getColor("default.dark")));
		} else {
			((JComponent) f.getContentPane()).setBorder(new CompoundBorder(
					new CompoundBorder(new LineBorder(Settings.getColor("default.dark")),new LineBorder(Settings.getColor("default.light"))),
					new LineBorder(Settings.getColor("default.dark"))
			));
		}
		f.getContentPane().setBackground(Settings.getColor("default.light"));


	}


}
