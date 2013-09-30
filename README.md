jmacinator
==========

This library is a Java/Mac OS X integrator. It allows your Java application to more fully interact with Mac OS X like a native application while also still being cross-platform. It requires Java 7 or higher and Mac OS X Lion (10.7) or higher.
Mac OS X 10.7 is the lowest version of Mac OS that Java 7 supports.

An example application that adds a quit handler:
```java
import javax.swing.*;
import lib.apple.eawt.*;
import lib.apple.eawt.AppEvent.*;

public class MacExample {
  private static final boolean IS_MAC = System.getProperty("os.name").contains("Mac");
  
  public static void main(String[] args) {
    // Swing components should be created on the
    // Event Dispatch Thread
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        createAndShowGUI();
      }
    });
  }
  
  private static void createAndShowGUI() {
    // Create the window and center it on the screen
    final JFrame frame = new JFrame("Test Window");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(true);
    frame.setSize(300, 300);
    frame.setLocationRelativeTo(null);
    
    // Need to block this off so that it will not be
    // used on non-Mac OS systems.
    if (IS_MAC) {
      Application.setQuitHandler(new QuitHandler() {
        @Override
        public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
          frame.setVisible(false);
          frame.dispose();
          response.performQuit();
        }
      });
      Application.setQuitStrategy(QuitStrategy.SYSTEM_EXIT_0);
    }
    
    frame.setVisible(true);
  }
}
```

Other features also include setting the dock image so that it is not the generic Java icon and also getting many other system events.
