/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package launch;

import gui.LauncherFrame;
import io.FileSystem;
import java.io.File;
import java.io.IOException;
import main.EntryPoint;

/**
 *
 * @author Matt Crow
 */
public class Launcher extends EntryPoint{
    private static Launcher instance;
    
    private Launcher(){
        super();
        if(instance != null){
            throw new RuntimeException("Cannot instanciate more than 1 instance of Launcher; use Launcher.getInstance() instead");
        }
    }
    
    public static final Launcher getInstance(){
        if(instance == null){
            instance = new Launcher();
        }
        return instance;
    }
    
    @Override
    public void doRun(){
        LauncherFrame window = new LauncherFrame();
        listenToWindow(window);
        checkForUpdates(window.getContent().getTextDisplay());
        
        Launcher l = this;
        Thread appThread = new Thread(){
            @Override
            public void run(){
                //https://stackoverflow.com/questions/4936266/execute-jar-file-from-a-java-program
                ProcessBuilder builder = new ProcessBuilder("java", "-jar", FileSystem.JAR_FOLDER_PATH + File.separator + "Application.jar");
                try {
                    builder.start();
                } catch (IOException ex) {
                    l.getLog().logError(ex);
                }
            }
        };
        appThread.start();
        writeLog();
        window.dispose();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Launcher.getInstance().run();
    }
    
}
