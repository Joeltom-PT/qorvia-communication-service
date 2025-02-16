package com.qorvia.communicationservice.config;

import com.qorvia.communicationservice.utils.AppConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    // --- Communication Service Async Queue, Exchange, and Routing ---

    @Bean
    public Queue communicationServiceAsyncQueue() {
        return new Queue(AppConstants.COMMUNICATION_SERVICE_ASYNC_QUEUE, true);
    }

    @Bean
    public Exchange communicationServiceAsyncExchange() {
        return new DirectExchange(AppConstants.COMMUNICATION_SERVICE_EXCHANGE, true, false);
    }

    @Bean
    public Binding communicationServiceAsyncBinding() {
        return BindingBuilder
                .bind(communicationServiceAsyncQueue())
                .to(communicationServiceAsyncExchange())
                .with(AppConstants.COMMUNICATION_SERVICE_ROUTING_KEY)
                .noargs();
    }


    // Configure the RPC Listener Container for the RPC queues
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        return factory;
    }
}