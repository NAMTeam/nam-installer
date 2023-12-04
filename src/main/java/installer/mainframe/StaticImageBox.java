package installer.mainframe;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.TileObserver;
import java.awt.image.WritableRenderedImage;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StaticImageBox extends JPanel implements SwingConstants {

  public Image image;

  public StaticImageBox(Image im){
    super();
    image = im;
    this.setPreferredSize(new Dimension(image.getWidth(this),image.getHeight(this)));
    this.setMinimumSize(this.getPreferredSize());
    this.setSize(this.getPreferredSize());
    //System.out.println(this.getWidth());
  }

  @Override
  public void paint(Graphics g){
    //super.paint(g);
    g.drawImage(image, 0, 0, this);
  }

}
