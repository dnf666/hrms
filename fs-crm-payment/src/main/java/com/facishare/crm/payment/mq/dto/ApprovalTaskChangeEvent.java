package com.facishare.crm.payment.mq.dto;

import java.util.List;
import lombok.Data;

@Data
public class ApprovalTaskChangeEvent {
  private String tag;
  private String eventType;
  private String id;
  private EventData eventData;

  @Data
  public class EventData {
    private String tenantId;
    private String entityId;
    private String dataId;
    private List<String> candidateIds;
    private String instanceId;
    private String taskId;
    private List<String> processedPersons;
  }
}
