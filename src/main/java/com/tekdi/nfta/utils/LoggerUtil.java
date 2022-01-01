package com.tekdi.nfta.utils;

import com.tekdi.nfta.config.Constant;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.openqa.selenium.WebElement;

/**
 * LoggerUtil class encapsulates several utility methods that helps logging.
 * It consolidates all logs in one single file and creates new file for every run.
 * The log file is generated in 'logs' directory.
 * It provides convenient methods for:
 * - measuring time of execution. 
 * - Dumping stack trace in log files 
 * 
 * It also sets pattern of logs with TimeStamp and Log level
 *
 * @author Mandar Wadhavekar
 * 
 */
public class LoggerUtil {
    
     private static FileHandler logFileHandler=null;
     private static SimpleFormatter simpleFormatter = null;  
     private static Logger logger = null;
     private static String logFileDir = "."+java.io.File.separator+java.io.File.separator+Constant.LOGS_DIR.getValue() +java.io.File.separator+java.io.File.separator;
     private static Instant startInstant = null; //to measure time
     //static initializer for the class
     static {
         //System.setProperty("java.util.logging.SimpleFormatter.format",
         // "[%1$tF %1$tT] [%4$-7s] %5$s %n");
         System.setProperty(
            "java.util.logging.SimpleFormatter.format",
            "[%1$tF %1$tT] [%4$-7s] %5$s %n");
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("-YY-MM-dd_HHmmss");
         System.out.println("======>>>> LOG_FILE_NAME from Constants "+Constant.LOG_FILE_NAME.getValue());
         String logFileName = Constant.LOG_FILE_NAME.getValue() + simpleDateFormat.format(Calendar.getInstance().getTime()) + ".log";
         //String logFileName = System.getProperty(Constant.LOG_FILE_NAME.getValue()) + simpleDateFormat.format(Calendar.getInstance().getTime()) + ".log";
         System.out.println("======:::: LOG_FILE_NAME "+logFileName);
         System.out.println("======:::: LOG_FILE_DIR "+logFileDir);
         try {
            File file = new File(logFileDir);
            
            if (file.exists()== false)
            {
              System.out.println("Creating directory: "+ logFileDir);
              file.mkdir(); //create logs directory
            } else {
              System.out.println("The directory already exists: "+ logFileDir);  
            }
            
         logFileHandler = new FileHandler(logFileDir+logFileName, true);
         simpleFormatter = new SimpleFormatter(); 
         
        
         logFileHandler.setFormatter(simpleFormatter); 
         logger = Logger.getLogger("NFTA-logger"); //just naming the logger
         //logger.setUseParentHandlers(false);
         logger.addHandler(logFileHandler);
         logger.setLevel(Level.FINEST); //TODO: read this from properties file Mandar Wadhavekar
        
         } catch (IOException ioe) {
             System.out.println("================= FATAL ERROR ===========================");
             System.out.println("IOException while creating log file: "+logFileName+" in directory: "+logFileDir);
             ioe.printStackTrace();           
         } catch (Exception e) {
             System.out.println("================= FATAL ERROR ===========================");
             System.out.println("Exception while creating log file : "+logFileName+ " in directory: "+logFileDir);
             e.printStackTrace();           
         }
     } //end of static block
     
     /**
      * This method returns the logFileHandler
      * This method may be needed only if some class want to do specific coding
      * @return FileHandler
      */
     public static FileHandler getLogFileHandler()
     {
         return logFileHandler;
     }
     /**
      * This methods returns Logger so that classes can put in log statements
      * @return Logger
      */
     public static Logger getLogger(){ 
         
         return logger;  
     }
     
     /**
      * Log details of exception 
      * - exception's message and stack trace
      * @param exception 
      */
     public static void logException(Exception exception){ 
         
         logException(exception, "");                 
     }
     
      /**
      * Log details of exception - exception's error message, custom message and stack trace
      * @param exception 
      */
     public static void logException(Exception exception, String customMessage){ 
         
         logger.severe("!!!    EXCEPTION Occurred    !!!");
         logger.severe(customMessage);
         logger.severe("------------------- Exception Error Message is: --------------");
         logger.severe(exception.getMessage());
         
         /*
         This method does not print stack trace
         logger.log(Level.SEVERE, customMessage+"trying logger.log with throwable ", exception );
         */
         logger.severe("=================== Exception stack trace ====================");
         logger.severe(convertStackTraceToString(exception));
                         
     }
     /**
      * Starts measurement of time using Instant. 
      * Returns java.time.Instant class but caller method can ignore it if they
      * just want to measure time and not interested in other details of the Instant
      * @return Instant
      */
     public static Instant startTimeMeasurement()
     {
         startInstant = Instant.now();
         logger.log(Level.INFO, "Started measuring time "+startInstant); //TODO: Change level to FINER - Mandar Wadhavekar
         return startInstant;
     }
     /**
      * Stop Time Measurement and print time taken since last startTimeMeasurement.
      */
     public static void stopTimeMeasurement()
     {         
         stopTimeMeasurement(null);
     }
     /**
      * Stop Time Measurement and print time taken since last startTimeMeasurement and 
      * also print the custom message.
      * This can be utilized in following 
      * @param message 
      */
     public static void stopTimeMeasurement(String message)
     {
         Instant finishInstant = Instant.now();
         long timeElapsed = Duration.between(startInstant, finishInstant).toMillis();
         if (message != null) {
             logger.log(Level.INFO, message);
         }
         logger.log(Level.INFO, "Time elapsed - [In milli-seconds: " + timeElapsed +"] [In seconds:"+ timeElapsed/1000.0 +"]");
         
     }
     /**
      * Converts stack trace to String 
      * Mainly to dump it in log file.
      * @param throwable the exception for which stack trace is to be converted into string
      * @return 
      */
      private static String convertStackTraceToString(Throwable throwable) 
      {
        try (StringWriter sw = new StringWriter(); 
               PrintWriter pw = new PrintWriter(sw)) 
        {
            throwable.printStackTrace(pw);
            return sw.toString();
        } 
        catch (IOException ioe) 
        {
            throw new IllegalStateException(ioe);
        }
      }    
      
       /**
       * Logs details of the given WebElement
       */
       public static void logWebElement(WebElement webElement)
       {
           logWebElement(webElement, null);
       }
      
      /**
       * Logs details of the given WebElement and attribute details
       */
       public static void logWebElement(WebElement webElement, String attributeName)
       {
           logger.info("@@@@@@@@@@@@@@@@@@@@@@@@ WebElement Details @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
           if (webElement != null) {
           logger.info("getTagName ()  -> "+ webElement.getTagName());
           logger.info("getText ()     -> "+ webElement.getText());
           logger.info("getLocation () -> "+ webElement.getLocation());
           logger.info("getRect ()     -> "+ webElement.getRect());
           logger.info("getSize ()     -> "+ webElement.getSize());
           if (attributeName != null ){
           logger.info("getAttribute ("+attributeName+")     -> "+ webElement.getAttribute(attributeName));   
           }
           
           logger.info("isDisplayed () -> "+ webElement.isDisplayed());
           logger.info("isEnabled ()   -> "+ webElement.isEnabled());
           logger.info("isSelected ()  -> "+ webElement.isSelected());
           
           } else {
            logger.info("WebElement is null");   
           }
           
           logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
       }
       
    
    
}
