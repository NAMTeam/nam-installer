package installer.screens.scanner;

import installer.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Scanner implements Runnable {

  private volatile boolean runScan;

  File rootFolder;
  List<String> files[];
  FileSearcher searchers[];

  public Scanner(List<String> files, FileSearcher searcher){
    this(new List[]{files}, new FileSearcher[]{searcher});
  }

  public Scanner(List<String> files[], FileSearcher searchers[]){
    this.files = files;
    this.searchers = searchers;
  }

  public void startScan(File rootFolder){
    this.rootFolder = rootFolder;
    new Thread(this).start();
  }


  public void run(){
    runScan = true;
    for (FileSearcher f : searchers){
      f.searchStarted();
    }
    scan(rootFolder);
    if (runScan){
      for (FileSearcher f : searchers){
        f.searchFinished();
      }
    }
  }


  public void stopScan(){
    runScan = false;
    for (FileSearcher f : searchers){
      f.searchAborted();
    }
  }


  private void scan(File folder){
    File f[] = folder.listFiles();
    for (int i = 0; i < f.length && runScan; i++ ){
      if (f[i].isDirectory()){
        scan(f[i]);
      } else {
        for (int j = 0; j < searchers.length && runScan; j++){
          for (String fileName : files[j]){
            if (f[i].getName().equals(fileName)){
              searchers[j].found(f[i]);
            }
          }
        }
      }
    }
  }


}
