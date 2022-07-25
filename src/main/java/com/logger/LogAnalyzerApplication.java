package com.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import com.logger.util.LogEventProcessor;

@SpringBootApplication
@ComponentScan("com")
public class LogAnalyzerApplication implements CommandLineRunner {
  public static final Logger logger = LoggerFactory.getLogger(LogAnalyzerApplication.class);
  @Autowired
  private LogEventProcessor logProcessor;


  public static void main(String[] args) {
    SpringApplication.run(LogAnalyzerApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info("Started execution..");
    logProcessor.processLogEvents("files/logfile.txt");
    logger.info("Execution completed sucessfully..");
  }

}
