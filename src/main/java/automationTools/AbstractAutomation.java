package automationTools;

import java.util.List;
import logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The AbstractAutomation class serves as
 * the base for every type of web automation.
 * Any behavior described in this class should
 * be used by every subclass; therefore, any
 * specialized behavior should be implemented
 * into subclasses.
 * 
 * @author Matt Crow
 */
public abstract class AbstractAutomation {
    private final String name;
    private final String desc;
    private WebDriver driver;
    private WebDriverWait wait;
    private boolean running;
    
    private final StringBuilder outputLog;
    private final StringBuilder errorLog;
    private Logger logger;
    private Logger errorLogger;
    
    /**
     * 
     * @param autoName the name of this automation
     * @param description a brief description of what the automation does,
     * which should allow the user to know what the automation does.
     */
    public AbstractAutomation(String autoName, String description){
        name = autoName;
        desc = description;
        driver = null;
        wait = null;
        running = false;
        outputLog = new StringBuilder();
        errorLog = new StringBuilder();
        
        //default output log
        logger = new Logger() {
            @Override
            public void log(String s) {
                outputLog.append(s).append('\n');
                System.out.println(s);
            }

            @Override
            public String getLog() {
                return outputLog.toString();
            }
        };
        
        errorLogger = new Logger(){
            @Override
            public void log(String s) {
                errorLog.append(s).append('\n');
                System.err.println(s);
            }

            @Override
            public String getLog() {
                return errorLog.toString();
            }
        };
    }
    
    public final String getName(){
        return name;
    }
    
    /**
     * Returns the textual description
     * of what this automation does.
     * @return  
     */
    public final String getDesc(){
        return desc;
    }
    
    /**
     * Used to get whether or not this
     * automation is currently being
     * run.
     * 
     * @return 
     */
    public final boolean isRunning(){
        return running;
    }
    
    public final AbstractAutomation quit(){
        running = false;
        
        return this;
    }
    
    /**
     * Sets the object which should receive output from the automation.
     * This defaults to sending output to System.out, but RunWindow calls this method,
     * passing in its ScrollableTextDisplay.
     * @param l an object implementing the logging.Logger interface
     * @return this, for chaining purposes
     */
    public AbstractAutomation setLogger(Logger l){
        logger = l;
        return this;
    }
    
    /**
     * Sets the Logger which should receive any error messages
     * this automation produces.
     * 
     * @param l an object implementing the logging.Logger interface
     * @return this, for chaining purposes
     */
    public AbstractAutomation setErrorLogger(Logger l){
        errorLogger = l;
        return this;
    }
    
    /**
     * Sets the WebDriver that this automation will use to perform 
     * its automation. Calling this method will also create a WebDriverWait
     * alongside the driver instance.
     * 
     * @param d the WebDriver to use for this' process.
     * @return this, for chaining purposes
     */
    public final AbstractAutomation setDriver(WebDriver d){
        if(running){
            throw new UnsupportedOperationException("Cannot set WebDriver while automation is running");
        }
        driver = d;
        wait = new WebDriverWait(d, 10);
        return this;
    }
    
    /**
     * Used to get the WebDriver currently
     * being used to perform automation,
     * if any.
     * 
     * @return this' driver.
     */
    public final WebDriver getDriver(){
        if(!running){
            throw new NullPointerException("Automation is not being run, so the driver is not set");
        }
        return driver;
    }
    
    /**
     * While the automation is being run,
     * this method can be used to obtain
     * the WebDriverWait associated with
     * the WebDriver being used to run the
     * automation.
     * 
     * @return the wait associated with this' driver.
     */
    public final WebDriverWait getWait(){
        if(!running){
            throw new NullPointerException("Automation is not being run, so the wait is not set");
        }
        return wait;
    }
    
    /**
     * Waits for an element in the webpage to load,
     * then returns it.
     * 
     * Since FireFoxDriver appears to not block while the
     * webpage is loading, using driver.findElement(By by)
     * will usually through a StaleElementException,
     * so this method circumvents this problem.
     * 
     * @param by the locator used to find the element
     * @return the WebElement found by the "by" parameter.
     */
    public final WebElement awaitFindElement(By by){
        if(wait == null || driver == null){
            throw new NullPointerException("process is not running, so the WebDriver isn't set");
        }
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }
    
    /**
     * Waits for elements in the webpage to load,
     * then returns them.
     * 
     * Since FireFoxDriver appears to not block while the
     * webpage is loading, using driver.findElement(By by)
     * will usually through a StaleElementException,
     * so this method circumvents this problem.
     * 
     * @param by the locator used to find the elements
     * @return the WebElements found by the "by" parameter.
     */
    public final List<WebElement> awaitFindElements(By by){
        if(wait == null || driver == null){
            throw new NullPointerException("process is not running, so the WebDriver isn't set");
        }
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }
    
    /**
     * Sends a string to the current logger.
     * 
     * @param output the text to write to output, with a newline appended to the end.
     * @return this, for chaining purposes
     */
    public final AbstractAutomation writeOutput(String output){
        logger.log(output + "\n");
        return this;
    }
    
    /**
     * Sends an error message to the error log.
     * 
     * @param msg the error text to write.
     * @return this, for chaining purposes
     */
    public final AbstractAutomation reportError(String msg){
        errorLogger.log(msg + "\n");
        return this;
    }
    
    private AbstractAutomation start(){
        running = true;
        return this;
    }
    
    /**
     * Shuts down the WebDriver and wait used
     * by this automation. Only works if the
     * automation is currently being run.
     * 
     * @return this, for chaining purposes. 
     */
    private AbstractAutomation finish(){
        if(wait == null || driver == null){
            throw new NullPointerException("Process is not running, so it cannot finish");
        }
        
        driver.quit();
        driver = null;
        wait = null;
        running = false;
        
        return this;
    }
    
    /**
     * Performs the automation.
     * This process handles both setup and
     * cleanup.
     * 
     * @param d the WebDriver to use for running
     * @return this, for chaining purposes
     */
    public final AbstractAutomation run(WebDriver d){
        writeOutput("Running " + getClass().getName());
        setDriver(d);
        try{
            start();
            doRun();
            finish();
        } catch(Exception e){
            finish(); //make sure we finish
            throw e;
        }
        return this;
    }
    
    /**
     * This method should contain the process executed by the automation.
     * This method is called by the run() method.
     */
    public abstract void doRun();
}