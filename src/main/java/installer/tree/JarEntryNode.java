package installer.tree;
/**
 *
 */



import installer.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;


public class JarEntryNode extends DefaultMutableTreeNode  {

  private class ExclusionGroup {
    public List<JarEntryNode> nodes = new Vector<JarEntryNode>();
    public JarEntryNode defaultNode = null;
  }

  private static final String[] ControlPrefix = {"_","-","=","^","!"};
  public static final String INSTALLFOLDER = "installation";

  private static final String getSimpleName(String name){
    for (String i : ControlPrefix){
      if (name.startsWith(i)){
        return name.substring(1);
      }
    }
    return name;
  }

  private static boolean isExclusiveFile(String name){
    return name.startsWith("-") || name.startsWith("=");
  }
  private static boolean isDefaultExclusiveFile(String name){
    return name.startsWith("=");
  }
  private static boolean isMandatory(String name){
    return name.startsWith("^");
  }
  private static boolean isUnselectable(String name){
    return name.startsWith("_");
  }
  private static boolean isDefaultUnselected(String name){
    return name.startsWith("!");
  }


  private String name;
  private JarEntry entry;
  private List<JarEntryNode> invisibleChildren; // the files in this folder
  private ExclusionGroup exclusion;
  private String description;
  private int index = 0;

  protected Icon icon = null;

  protected boolean selected, exclusive, subFoldersAllowed;
  private SelectionCapability selection;

  private List<JarEntryNode> getInvisibleChildren(){
    if (invisibleChildren == null){
      invisibleChildren = new Vector<JarEntryNode>();
    }
    return invisibleChildren;
  }

  public List<JarEntryNode> getInstallNodes(){
    return invisibleChildren;
  }

  private ExclusionGroup getExclusionGroup(boolean create){
    if (create && exclusion == null){
      exclusion = new ExclusionGroup();
    }
    return exclusion;
  }

  public JarEntryNode(JarEntry entry, JarFile file, Map<String, JarEntryNode> tree) throws IOException{
    this.entry = entry;
    this.subFoldersAllowed = true;
    tree.put(entry.toString(), this);
    String url = entry.toString();
    if (entry.isDirectory()){
      // cut off last "/"
      url = url.substring(0, url.length()-1);
    }
    if (!url.equals(INSTALLFOLDER)){
      String[] path = url.split("/");
      name = path[path.length-1];
      JarEntryNode parent = tree.get(url.substring(0, url.length() - name.length()));
      // parse name
      if (name.equals("icon.png")){
        parent.icon = new ImageIcon(ImageIO.read(file.getInputStream(entry)));
      } else if (!name.endsWith(".txt")){
        if (entry.isDirectory()){
          if (name.startsWith("@")){  // originally this was the `$` character
            index = Integer.parseInt(name.substring(1,2), 36);  // allows single character [0-9a-z] (case insensitive)
            name = name.substring(2);
          }
          if (name.endsWith("#")){
            this.subFoldersAllowed = false;
            name = name.substring(0, name.length()-1);
          }
          parent.add(this);
          setSelection(parent.getSelection());
          selected = parent.isSelected();
          if (isDefaultUnselected(name)){
            selected = false;
          } else if (isExclusiveFile(name)){
            exclusive = true;
            ExclusionGroup group = ((JarEntryNode)getParent()).getExclusionGroup(true);
            group.nodes.add(this);
            if (isDefaultExclusiveFile(name)){
              group.defaultNode = this;
            } else {
              selected = false;
            }
          } else if(isMandatory(name)){
            setSelection(SelectionCapability.PARENT);
            //setSelected(parent.isSelected());
          } else if(isUnselectable(name)){
            setSelection(SelectionCapability.NEVER);
            selected = false;
          }
        } else {
          parent.getInvisibleChildren().add(this);
        }
      } else {
        String line;
        StringBuffer str = new StringBuffer();
        BufferedReader r = new BufferedReader(new InputStreamReader(file.getInputStream(entry)));
        while ((line = r.readLine()) != null){
          str.append(line);
          str.append('\n');
        }
        parent.setDescription(Settings.compileString(str.toString()));
      }
      name = getSimpleName(name);
    } else {
      name = Settings.getString("defaultfolder");
      setSelection(SelectionCapability.DEFAULT);
      setSelected(true);
    }
  }

  private static final Comparator<JarEntryNode> COMPARATOR = new Comparator<JarEntryNode>(){
    @Override
    public int compare(JarEntryNode node1, JarEntryNode node2) {
      return node1.index - node2.index;
    }
  };

  @Override
  public void add(MutableTreeNode node){
    super.add(node);
    if (((JarEntryNode)node).index > 0){
      Collections.<JarEntryNode>sort(this.children, COMPARATOR);
    }
  }

  public void setDescription(String d){
    description = d;
  }

  public String getDescription(){
    return description;
  }

  public JarEntry getEntry(){
    return entry;
  }

  public String toString(){
    return name;
  }

  public void checkSelection(){
    boolean select = false;
    for (int i = 0; i < this.getChildCount(); i++){
      if (((JarEntryNode)this.getChildAt(i)).isSelected()){
        select = true;
        break;
      }
    }
    if (select != isSelected()){
      // don't dig selection here!
      setSelected(select, false);
      if (getParent() != null){
        ((JarEntryNode)getParent()).checkSelection();
      }
    }
  }

  public void setSelected(boolean sel){
    setSelected(sel, true);
  }

  public void setSelected(boolean sel, boolean dig){
    // first check selection capabilities
    switch(getSelection()){
    case PARENT:
      if (!isExclusive()) {  // exclusion groups remain selectable
        sel = ((JarEntryNode)getParent()).isSelected();
      } else {
        // exclusive button
        if (!((JarEntryNode)getParent()).isSelected()) {
          sel = false;
        }
      }
      break;
    case ALWAYS: if (!sel) return;
    case NEVER: if (sel) return;
    default: ;
    }

    this.selected = sel;

    if (dig){
      // if we are an exclusion group folder, only select one
      // of the children in the exclusion group.
      ExclusionGroup group = this.getExclusionGroup(false);
      for (int i = 0; i < this.getChildCount(); i++){
        JarEntryNode node = (JarEntryNode)this.getChildAt(i);
        if (!sel || group == null || !group.nodes.contains(node)){
          node.setSelected(sel, true);
        }
      }
      if (sel && group != null){
        if (group.defaultNode == null){
          group.nodes.get(0).setSelected(true, true);
        } else {
          group.defaultNode.setSelected(true, true);
        }
      }
    }

    if (sel && !dig){
      // set nodes that follow parent selection, even if we don't dig
      for (int i = 0; i < this.getChildCount(); i++){
        JarEntryNode node = (JarEntryNode)this.getChildAt(i);
        if (node.getSelection() == SelectionCapability.PARENT){
          node.setSelected(true, true);
        }
      }
    }

    if (sel && isExclusive()){
      // if we are an exclusive node, unselect the other nodes in the exclusion group
      ExclusionGroup group = ((JarEntryNode)getParent()).getExclusionGroup(false);
      for (JarEntryNode f : group.nodes){
        if (!this.equals(f)){
          f.setSelected(false, true);
        }
      }
    }
  }

  public boolean isExclusive(){
    return exclusive;
  }

  public boolean isSelected() {
    return selected;
  }

  public boolean isClickable(boolean ignoreOwnSelection) {
    // mandatory radio buttons remain clickable if parent is selected
    return getSelection() == SelectionCapability.DEFAULT && (ignoreOwnSelection || !(isExclusive() && isSelected())) ||
      getSelection() == SelectionCapability.PARENT && isExclusive() && ((JarEntryNode)getParent()).isSelected() && (ignoreOwnSelection || !isSelected());
  }

  public boolean allowsSubfolders(){
    return this.subFoldersAllowed;
  }

  public void setSelection(SelectionCapability selection) {
    if (this.selection == null || selection.strength > this.selection.strength){
      this.selection = selection;
    }
  }

  public SelectionCapability getSelection() {
    return selection;
  }

  private JarEntryRenderer renderer;

  public JarEntryRenderer getRenderer(){
    if (renderer == null){
      renderer = new JarEntryRenderer(this);
    }
    return renderer;
  }

  public void click(int x, int y){
    getRenderer().click(x, y);
  }



}