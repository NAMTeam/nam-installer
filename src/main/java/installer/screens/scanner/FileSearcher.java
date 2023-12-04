package installer.screens.scanner;

import java.io.File;

public interface FileSearcher {

  public void found(File file);

  public void searchStarted();
  public void searchAborted();
  public void searchFinished();

}
