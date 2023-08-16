package com.myproject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class,args);
    }
    @KafkaListener(topics = "notificationTopic")
    public void hundleNOtification(OrderPlaceEvent orderPlaceEvent){
        //send out email notification
        log.info("Received notification for order {}",orderPlaceEvent.getOrderNumber());

    }
}