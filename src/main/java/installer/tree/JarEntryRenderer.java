package installer.tree;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;



public class JarEntryRenderer {

  private static final Color selectedColor = UIManager.getColor("Tree.selectionBackground");
  private static final Color defaultColor  = UIManager.getColor("Tree.textBackground");
  private static final Color selectedFontColor = UIManager.getColor("Tree.selectionForeground");
  private static final Color defaultFontColor = UIManager.getColor("Tree.textForeground");
  private static final Font treeFont = UIManager.getFont("Tree.font");

  private static int defaultHeight;

  static {
    Icon i1 = UIManager.getIcon("Tree.leafIcon");
    Icon i2 = UIManager.getIcon("Tree.openIcon");
    Icon i3 = UIManager.getIcon("Tree.closedIcon");
    int h1 = i1 == null ? 0 : i1.getIconHeight();
    int h2 = i2 == null ? 0 : i2.getIconHeight();
    int h3 = i3 == null ? 0 : i3.getIconHeight();
    defaultHeight = Math.max(Math.max(h1, h2),h3);
  }

  private JComponent renderBox = new Box(SwingConstants.HORIZONTAL);
  private JToggleButton renderCheck;
  private JLabel renderLabel, renderIcon;
  private JarEntryNode node;

  public JarEntryRenderer(JarEntryNode f){
    node = f;
    renderLabel = new JLabel(" " + f.toString() + " ");
    //renderLabel.setOpaque(true);
    renderLabel.setFont(treeFont);
    renderIcon = new JLabel();
    renderIcon.setOpaque(false);
    if (f.isExclusive()){
      renderCheck = new JRadioButton();
    } else {
      renderCheck = new JCheckBox();
    }
    renderCheck.setBackground(defaultColor);
    renderCheck.setOpaque(false);
    renderBox.add(renderCheck);
    renderBox.add(renderIcon);
    //renderBox.add(Box.createHorizontalStrut(2));
    renderBox.add(renderLabel);
    //renderBox.setBorder(new LineBorder(Color.BLACK));
  }

  public Component render(boolean selected, boolean expanded, boolean hasFocus){
    //if (node.getDescription() != null && node.getParent() != null){
    //  renderLabel.setBorder(new LineBorder(selectedColor));
    //}
    renderCheck.setSelected(node.isSelected());
    renderCheck.setEnabled(node.isClickable(true));
    if (node.icon == null){
      if (node.isLeaf()) {
        renderIcon.setIcon(UIManager.getIcon("Tree.leafIcon"));
      } else if (expanded) {
        renderIcon.setIcon(UIManager.getIcon("Tree.openIcon"));
      } else {
        renderIcon.setIcon(UIManager.getIcon("Tree.closedIcon"));
      }
      renderBox.setPreferredSize(new Dimension(renderBox.getPreferredSize().width,
          Math.max(defaultHeight,renderLabel.getPreferredSize().height)));
    } else {
      renderIcon.setIcon(node.icon);
      renderBox.setPreferredSize(new Dimension(renderBox.getPreferredSize().width, node.icon.getIconHeight() + 2));
    }
    return renderBox;
  }

  public void click(int x, int y){
    if (renderCheck.getBounds().contains(x, y) && node.isClickable(false)) {
      node.setSelected(!node.isSelected());
      if (node.getParent() != null){
        ((JarEntryNode)node.getParent()).checkSelection();
      }
    }
  }



}
