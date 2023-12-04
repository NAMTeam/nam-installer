/**
 * 
 */
package installer.mainframe;

import installer.Settings;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class InstallerTabUI extends BasicTabbedPaneUI {
	
	/* first attempt at a tabbedUI, so be gentle ... */
	
	private JTabbedPane tabPane;
	private int tabWidth;
	
	public InstallerTabUI(JTabbedPane tabPane){
		this(tabPane, 60);
	}
	public InstallerTabUI(JTabbedPane tabPane, int tabWidth){
		this.tabPane = tabPane;
		tabPane.setUI(this);
		this.tabWidth = tabWidth;
		
	}
	
	//@Override
	//protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int tabWidth){
	//	return 40;
	//}
	
	protected  int	calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
		return tabWidth;
	}
	protected  int	calculateTabHeight(int tabPlacement, int tabIndex,  int fontHeight) {
		return 18;
	}
	public Rectangle	getTabBounds(JTabbedPane pane, int i) {
		return new Rectangle(1+i*tabWidth,0,tabWidth,20);
	}
	protected  void	paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, true);
	}
	protected  void	layoutLabel(int tabPlacement, FontMetrics metrics, int tabIndex, String title, Icon icon, Rectangle tabRect, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
		super.layoutLabel(tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, false);
	}
	protected  void	paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
	}
	protected  void	paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
		Graphics2D g2 = ((Graphics2D)g) ;
		g.setColor(isSelected ? Settings.getColor("tabs.line.select") : Settings.getColor("tabs.line.default")); 
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//				g.fillOval(textRect.x - 6,textRect.y + textRect.height - 2, 6 , 6);
		//				g.fillRect(textRect.x - 4,textRect.y + textRect.height, textRect.width + 10,2);
		Rectangle bounds = this.getTabBounds(tabPane, tabIndex);
		g.fillOval(bounds.x - 1, bounds.y + bounds.height - 3, 6 , 6);
		g.fillRect(bounds.x, bounds.y + bounds.height, bounds.width -1, 2);
		//g2.setPaint(Color.GRAY);
		//TextLayout text = new TextLayout(title, g.getFont(), g2.getFontRenderContext());
		//g2.draw(text.getOutline(AffineTransform.getTranslateInstance(textRect.x + 1, textRect.y + textRect.height * 0.75)));
		Composite c = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));				
		super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
		g2.setComposite(c);
	}			
	protected  void	paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
		//super.paintFocusIndicator(g, tabPlacement, rects, tabIndex, iconRect, textRect, false);
		//if (isSelected) {
		//	g.drawString("this is the " +tabIndex+ "th tab", 10, 15);
		//}
	}
	protected  void	paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}
	protected  void	paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}	           
	protected  void	paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}			           
	//protected  void	paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}
}