package installer.screens;

import installer.JarProperties;
import installer.Settings;
import installer.tree.JarEntryNode;
import installer.tree.JarFileTreeRenderer;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreePath;


public class BrowseFilesPanel extends Box implements MouseListener, MouseMotionListener{

	final static String INSTALLFOLDER = JarEntryNode.INSTALLFOLDER;

	final private JTree treeView;
	final private JTextPane info;
	
	final private EditorKit htmlEditor = new HTMLEditorKit();
	final private EditorKit basicEditor = new StyledEditorKit();

	public BrowseFilesPanel() throws IOException {	
		super(SwingConstants.HORIZONTAL);

		info = new JTextPane();
		info.setOpaque(false);
		info.setBorder(new TitledBorder(Settings.getString("files.infoborder")));
		info.setEditable(false);
		treeView = new JTree(JarProperties.getRoot());
		treeView.setRowHeight(0);
		treeView.setOpaque(false);
		//treeView.setRootVisible(false);
		treeView.expandRow(0);
		treeView.setCellRenderer(new JarFileTreeRenderer());
		treeView.addMouseListener(this);
		treeView.addMouseMotionListener(this);	
		
		JScrollPane p1 = new JScrollPane();
		p1.getViewport().add(treeView);
		p1.getViewport().setOpaque(false);
		p1.setBorder(new TitledBorder(Settings.getString("files.treeborder")));
		p1.setOpaque(false);
		JSplitPane split = new JSplitPane();		
		split.setRightComponent(info);
		split.setLeftComponent(p1);
		split.setDividerLocation(Settings.getInt("files.divider.pos"));
		if (Settings.getBoolean("files.divider.fixed")){
			split.setDividerSize(0);
		}

		split.setUI(new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					public void setBorder(Border b) {
					}
				};
			}
		});
		split.setBorder(null);
		split.setOpaque(false);
		
		Box splitBox = Box.createHorizontalBox();
		splitBox.add(split);
		
		Box contentPane = Box.createVerticalBox();
		contentPane.add(Box.createVerticalStrut(3));
		contentPane.add(splitBox);
		
		this.add(contentPane);
		this.setOpaque(false);
	}

	@Override
	public void mouseClicked(MouseEvent ev) {
		TreePath path = treeView.getPathForLocation(ev.getX(), ev.getY());		
		if (path != null) {
			JarEntryNode f = (JarEntryNode) path.getLastPathComponent();
			Rectangle rect = treeView.getPathBounds(path);
			f.click((int) (ev.getX() - rect.getX()), (int)(ev.getY() - rect.getY()));
			treeView.repaint();
		}
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseMoved(MouseEvent ev) {
		TreePath path = treeView.getPathForLocation(ev.getX(), ev.getY());		
		if (path != null) {
			JarEntryNode f = (JarEntryNode) path.getLastPathComponent();
			if (f.getDescription() != null){
				if (f.getDescription().toLowerCase().startsWith("<html>")){
					info.setEditorKit(htmlEditor);
				} else {
					info.setEditorKit(basicEditor);
				}
				info.setText(f.getDescription());
			} else {
				info.setText("");
			}
		}

	}


}
