package com.logger.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Event {
  @Id
  private String id;
  private String host;
  private String type;
  private long duration;
  private boolean alert;
}
