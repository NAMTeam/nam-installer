package installer;

import installer.tree.JarEntryNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;


public class JarProperties {

  private static final String INSTALLFOLDER = JarEntryNode.INSTALLFOLDER;


  private HashMap<String, JarEntryNode> tree;
  private String licence = null;
  private JarFile file;

  private JarProperties() throws IOException, URISyntaxException {
    File f = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
    if (f.getName().endsWith(".jar")){
      file = new JarFile(f);
    } else {
      file = new JarFile("Installer.jar");
    }
    tree = new HashMap<String, JarEntryNode>();
    Enumeration<JarEntry> e = file.entries();
    Settings.loadProperties(file.getInputStream(file.getJarEntry(Settings.SETTINGSFILE)));
    while(e.hasMoreElements()){
      JarEntry entry = e.nextElement();
      if (entry.getName().startsWith(INSTALLFOLDER)){
        new JarEntryNode(entry, file, tree);
      } else if (Settings.isLicense(entry.getName())) {
        String line;
        StringBuffer str = new StringBuffer();
        BufferedReader r = new BufferedReader(new InputStreamReader(file.getInputStream(entry)));
        while ((line = r.readLine()) != null){
          str.append(line);
          str.append('\n');
        }
        licence = Settings.compileString(str.toString());
        Settings.hasLicense = true;
      } else if (Settings.isSidebar(entry.getName())){
        Settings.Sidebar = ImageIO.read(file.getInputStream(entry));
      } else if (Settings.isCleanitol(entry.getName())){
        Settings.cleanitolFiles = new ArrayList<String>();
        String line;
        BufferedReader r = new BufferedReader(new InputStreamReader(file.getInputStream(entry)));
        while ((line = r.readLine()) != null){
          Settings.cleanitolFiles.add(line);
        }
      } else if (Settings.isDependencies(entry.getName())){
        Map<String, String> dependencies = new HashMap();
        Properties d = new Properties();
        d.load(file.getInputStream(entry));
        for (Object o : d.values()){
          String[] str = ((String)o).split("&&");
          if (str.length == 2){
            dependencies.put(str[0].trim(), str[1].trim());
          }
        }
        Settings.dependencyFiles = dependencies;
      }
    }
  }

  private static JarProperties instance;
  static {
    try {
      instance = new JarProperties();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.exit(-1);
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static JarProperties get(){
    return instance;
  }

  public static JarEntryNode getRoot(){
    return instance.tree.get(INSTALLFOLDER+"/");
  }

  public static String getLicence(){
    return instance.licence;
  }

  public static JarFile getJarFile(){
    return instance.file;
  }


}
