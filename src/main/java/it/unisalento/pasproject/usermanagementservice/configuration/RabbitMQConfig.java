package it.unisalento.pasproject.usermanagementservice.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ------  SECURITY  ------ //

    // Needed by authentication service
    @Value("${rabbitmq.queue.security.name}")
    private String securityRequestsQueue;

    @Value("${rabbitmq.exchange.security.name}")
    private String securityExchange;

    @Value("${rabbitmq.routing.security.key}")
    private String securityRequestRoutingKey;

    @Bean
    public Queue securityResponseQueue() {
        return new Queue(securityRequestsQueue);
    }

    @Bean
    public TopicExchange securityExchange() {
        return new TopicExchange(securityExchange);
    }

    @Bean
    public Binding securityBinding() {
        return BindingBuilder
                .bind(securityResponseQueue())
                .to(securityExchange())
                .with(securityRequestRoutingKey);
    }

    // ------  END SECURITY  ------ //



    // ------  AUTH DATA  ------ //

    // Needed by user management service
    @Value("${rabbitmq.queue.data.name}")
    private String dataQueue;

    @Value("${rabbitmq.exchange.data.name}")
    private String dataExchange;

    @Value("${rabbitmq.routing.data.key}")
    private String dataRoutingKey;

    @Bean
    public Queue dataQueue() {
        return new Queue(dataQueue);
    }

    @Bean
    public TopicExchange dataExchange() {
        return new TopicExchange(dataExchange);
    }

    @Bean
    public Binding dataBinding() {
        return BindingBuilder
                .bind(dataQueue())
                .to(dataExchange())
                .with(dataRoutingKey);
    }

    // ------  END AUTH DATA  ------ //

    // ------  PROFILE DATA  ------ //

    @Value("${rabbitmq.queue.update.name}")
    private String updatedDataQueue;

    @Value("${rabbitmq.exchange.update.name}")
    private String updatedDateExchange;

    @Value("${rabbitmq.routing.update.key}")
    private String updatedDataRoutingKey;

    @Bean
    public Queue updatedDataQueue() {
        return new Queue(updatedDataQueue);
    }

    @Bean
    public TopicExchange updatedDataExchange() {
        return new TopicExchange(updatedDateExchange);
    }

    @Bean
    public Binding updatedDataBinding() {
        return BindingBuilder
                .bind(updatedDataExchange())
                .to(updatedDataExchange())
                .with(updatedDataRoutingKey);
    }

    // ------  END PROFILE DATA  ------ //

    /**
     * Creates a message converter for JSON messages.
     *
     * @return a new Jackson2JsonMessageConverter instance.
     */
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Creates an AMQP template for sending messages.
     *
     * @param connectionFactory the connection factory to use.
     * @return a new RabbitTemplate instance.
     */
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
