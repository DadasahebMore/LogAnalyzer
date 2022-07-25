package com.logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.logger.util.LogEventProcessor;

@SpringBootTest
class LogAnalyzerApplicationTests {
  private static final String FILE = "files/logfile.txt";
  @Autowired
  private LogEventProcessor logProcessor;

  @Test
  void test_checkInputFile_correct() {
    assertThat(logProcessor.isValidFile(FILE), is(true));
  }


  @Test
  void test_processLogEvents() {
    logProcessor.processLogEvents(FILE);
  }


}
