package com.logger.util;

import lombok.Data;

@Data
public class LogEvent {
  private String id;
  private long timestamp;
  private String type;
  private String host;
  private String state;

}
