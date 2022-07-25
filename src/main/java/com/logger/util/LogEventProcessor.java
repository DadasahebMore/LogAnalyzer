package com.logger.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logger.entity.Event;
import com.logger.repository.LogEventRepository;

@Component
public class LogEventProcessor {
  public final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private LogEventRepository logEventRepository;
  private ObjectMapper mapper = new ObjectMapper();

  @Value("${log.event.timestamp.threshold:4}")
  private Integer logEventTimestampThreshold;
  
  public void processLogEvents(String filePath) {
    if (isValidFile(filePath)) {
      // Below map will contain n/2 element in the worse case, so we can use any cache provider for
      // large files
      ConcurrentHashMap<String, LogEvent> map = new ConcurrentHashMap<>();
      try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
        lines.forEach(line -> processLogEvent(map, line));
      } catch (Exception e) {
        logger.error("Exception while reading file: {}", e);
      }
    }
  }

  // Processing of event can be done concurrently using thread pool
  private void processLogEvent(Map<String, LogEvent> map, String currentLine) {
    try {
      LogEvent logEvent = mapper.readValue(currentLine, LogEvent.class);
      LogEvent previus = map.putIfAbsent(logEvent.getId(), logEvent);
      if (!ObjectUtils.isEmpty(previus)) {
        Event event = this.getEvent(previus, logEvent);
        logger.info(event.toString());
        logEventRepository.save(event);
        map.remove(previus.getId());
      }
    } catch (Exception e) {
      logger.error("Exception while processing log file: {}", e);
    }

  }

  protected boolean isValidFile(String path) {
    try {
      Path p = Paths.get(path);
      if (!Files.isReadable(p)) {
        logger.error("File not readable");
        return false;
      }
    } catch (Exception ex) {
      logger.error("Invalid Path File" + Arrays.toString(ex.getStackTrace()));
      return false;
    }
    return true;
  }

  private Event getEvent(LogEvent previusEvent, LogEvent currentEvent) {
    Event event = new Event();
    event.setId(previusEvent.getId());
    event.setDuration(
        this.calculateLogEventTime(previusEvent.getTimestamp(), currentEvent.getTimestamp()));
    event.setHost(previusEvent.getHost());
    event.setType(previusEvent.getType());
    event.setAlert(event.getDuration() > logEventTimestampThreshold);
    return event;
  }

  private long calculateLogEventTime(long previousEventTimestamp, long currenteventTimestamp) {
    return previousEventTimestamp > currenteventTimestamp
        ? previousEventTimestamp - currenteventTimestamp
        : currenteventTimestamp - previousEventTimestamp;
  }
}
