package gui;

import automationTools.AbstractAutomation;
import automationTools.QueryingAutomation;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Matt
 */
public class RunWindow extends Page{
    private final ScrollableTextDisplay text;
    
    public RunWindow(ApplicationPane app) {
        super(app);
        
        setLayout(new BorderLayout());
        
        add(new JLabel("Please wait while the automation runs..."), BorderLayout.PAGE_START);
        text = new ScrollableTextDisplay("***Program output will appear here***\n");
        add(text, BorderLayout.CENTER);
        
        JPanel bottom = new JPanel();
        
        JButton back = new JButton("Go Back");
        back.addActionListener((e)->{
            prev();
        });
        bottom.add(back);
        
        JButton finish = new JButton("Return to start");
        finish.addActionListener((e)->{
            next();
        });
        setOnDone(()->{
            //do nothing
        });
        bottom.add(finish);
    }
    
    public final void run(AbstractAutomation aa, String fileText, Class<? extends WebDriver> driverClass){
        Page p = this;
        new Thread(){
            @Override
            public void run(){
                try{
                    text.setText("***Program output will appear here***\n");
                    aa.setLogger(text);
                    aa.setErrorLogger(p.getHost().getHostingWindow().getRunningApplication().getErrorLog());
                    
                    if(aa instanceof QueryingAutomation){
                        ((QueryingAutomation)aa).setInputFile(fileText);
                    }
                    
                    aa.run(driverClass);
                } catch (Exception ex){
                    p.getHost().getHostingWindow().getRunningApplication().getErrorLog().log(ex);
                }
            }
        }.start();
    }
}
