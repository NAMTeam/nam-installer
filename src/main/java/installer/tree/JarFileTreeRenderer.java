package installer.tree;


import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.TreeCellRenderer;


public class JarFileTreeRenderer implements TreeCellRenderer  {
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (value != null && value instanceof JarEntryNode){
			return (((JarEntryNode)value).getRenderer().render(selected, expanded, hasFocus));
		}
		return null;
	}

}
