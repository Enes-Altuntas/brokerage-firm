package com.inghubs.adapters.order.event.consumer;

import com.inghubs.common.command.BeanAwareCommandPublisher;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventConsumer extends BeanAwareCommandPublisher {

  @RetryableTopic(
      attempts = "3",
      backoff = @Backoff(delay = 2000, multiplier = 3, maxDelay = 20000)
  )
  @KafkaListener(topics = "order.public.outbox", groupId = "order-query-group", containerFactory = "kafkaListenerContainerFactory")
  public void consumeOrderEvent(String eventPayload, @Headers Map<String, Object> headers, Acknowledgment acknowledgment) {
    log.info("Received order event {}", eventPayload);
  }

  @KafkaListener(topics = "order.public.outbox-dlt", groupId = "order-query-group", containerFactory = "kafkaListenerContainerFactory")
  public void consumeOrderEventDLT(String eventPayload, @Headers Map<String, Object> headers,
      Acknowledgment acknowledgment) {
    log.info("Received order event {}", eventPayload);
  }
}
