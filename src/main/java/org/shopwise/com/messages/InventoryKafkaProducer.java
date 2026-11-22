package org.shopwise.com.messages;

import org.shopwise.com.event.InventoryResponseEvent;
import org.shopwise.com.utils.KafkaConfigProperties;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class InventoryKafkaProducer {

    private final KafkaTemplate<String, InventoryResponseEvent> kafkaTemplate;

    public InventoryKafkaProducer(
            KafkaTemplate<String, InventoryResponseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishInventoryResponse(InventoryResponseEvent event) {

        try {
            SendResult<String, InventoryResponseEvent> result =
                    kafkaTemplate.send(
                            KafkaConfigProperties.INVENTORY_RESPONSE_TOPIC,
                            event.getOrderId().toString(),
                            event
                    ).get(10, TimeUnit.SECONDS);

            System.out.println(
                    "Inventory response published successfully. "
                            + "Order ID: " + event.getOrderId()
                            + ", Partition: "
                            + result.getRecordMetadata().partition()
                            + ", Offset: "
                            + result.getRecordMetadata().offset()
            );

        } catch (Exception ex) {

            System.err.println(
                    "Failed to publish inventory response. Order ID: "
                            + event.getOrderId()
            );

            throw new RuntimeException(
                    "Unable to publish inventory response to Kafka", ex
            );
        }
    }
}
