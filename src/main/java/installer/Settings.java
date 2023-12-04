package installer;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class Settings {

  private static final String PATHSEP = File.separator;

  public static Image Sidebar;
  public static List<String> cleanitolFiles = null;
  public static Map<String,String> dependencyFiles = null;
  public static String INSTALL_DESTINATION, PLUGINS_FOLDER, CLEANITOL_BACKUP;
  public static final String splash = "splash.jpg";
  public static boolean hasSplash = true;
  public static boolean hasLicense = false;
  public static boolean installerFinished = false;

  static {
    String simcityfolder = new JFileChooser().getFileSystemView().getDefaultDirectory().getPath();
    if (OS.isMacOSX()){
      simcityfolder += PATHSEP + "Documents";
    }
    simcityfolder += PATHSEP + "SimCity 4" ;
    INSTALL_DESTINATION = simcityfolder + PATHSEP + "Plugins";
    PLUGINS_FOLDER = INSTALL_DESTINATION;
    CLEANITOL_BACKUP = simcityfolder + PATHSEP + "Backup_Plugins";
  }

  private static String[] sidebarExts, licenseExts;

  protected static boolean isSidebar(String s){
    String sidebar = properties.getProperty("sidebar");
    for(String ext : sidebarExts){
      if (s.equals(sidebar + "." + ext)){
        return true;
      }
    }
    return false;
  }

  protected static boolean isLicense(String s){
    String license = properties.getProperty("license");
    for(String ext : licenseExts){
      if (s.equals(license + "." + ext)){
        return true;
      }
    }
    return false;
  }

  protected static boolean isCleanitol(String s){
    return s.equals(properties.getProperty("cleanitol"));
  }

  protected static boolean isDependencies(String s){
    return s.equals(properties.getProperty("depends"));
  }

  // compile strings: replace "${key}" with the property value of "key"
  private static final String REGEX = "\\$\\{\\S*\\}";
  private static final Pattern PATTERN = Pattern.compile(REGEX);
  public static String compileString(String text){
    Matcher m = PATTERN.matcher(text);
    while (m.find()){
      String key = text.substring(m.start()+2,m.end()-1);
      if (properties.containsKey(key)){
        // use Matcher.quoteReplacement() to ensure correct replacement
        // if there are $'s in the replacement string
        String value = Matcher.quoteReplacement(properties.getProperty(key));
        text = text.replaceFirst(REGEX, value);
        m = PATTERN.matcher(text); // update matcher
      }
    }
    return text;
  }


  //protected static final String LICENCE = "license.txt";
  protected static final String SETTINGSFILE = "settings.txt";

  // keep a cached Color table so colors only need to be compiled once for each color property
  private final static Map<String, Color> colors = new HashMap<String, Color>();
  private final static Properties properties = new Properties();

  protected static void loadProperties(InputStream in){
    try {
      properties.load(in);
      // compile all properties
      for (Object key : properties.keySet()){
        String value = properties.getProperty((String) key);
        properties.setProperty((String) key, compileString(value));
      }
      // load some special properties
      sidebarExts = ((String) properties.get("sidebar.exts")).split(",");
      licenseExts = ((String) properties.get("license.exts")).split(",");
      INSTALL_DESTINATION += PATHSEP + properties.getProperty("defaultfolder");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static Color parseColor(String value){
    String rgb[] = value.split(",");
    return new Color(Integer.parseInt(rgb[0]),
        Integer.parseInt(rgb[1]),
        Integer.parseInt(rgb[2]));
  }

  public static String getString(String key){
    return properties.getProperty(key);
  }
  public static int getInt(String key){
    return Integer.parseInt(properties.getProperty(key));
  }
  public static boolean getBoolean(String key){
    return Boolean.parseBoolean(properties.getProperty(key));
  }
  public static Color getColor(String key){
    if (colors.containsKey(key)){
      return colors.get(key);
    } else {
      Color c = parseColor(properties.getProperty(key));
      colors.put(key, c);
      return c;
    }
  }




}
