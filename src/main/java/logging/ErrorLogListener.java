package logging;

/**
 * Classes which need to know when an ErrorLogger
 * logs a message should implement this interface,
 * and add themselves as an errorLogListener to that
 * error logger.
 * 
 * @author matt Crow
 */
@FunctionalInterface
public interface ErrorLogListener {
    /**
     * Fires whenever the given log logs an error message.
     * @param log the ErrorLog which logged an error.
     * @param msg the error message which was logged.
     */
    public void errorLogged(ErrorLogger log, String msg);
}
