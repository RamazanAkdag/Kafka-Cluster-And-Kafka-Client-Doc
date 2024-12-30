package com.akdag.akka;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import com.akdag.akka.keys.Command;
import com.akdag.akka.keys.Error;
import com.akdag.akka.keys.SharedServiceKeys;
import com.akdag.kafka.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class OcsActor extends AbstractBehavior<Command> {

    private KafkaProducer<String, Message> kafkaProducer;

    public OcsActor(ActorContext<Command> context, KafkaProducer<String, Message> kafkaProducer) {
        super(context);
        context.getLog().info("registering to receptionist..");
        context.getSystem().receptionist().tell(Receptionist.register(SharedServiceKeys.OCS_ACTOR_SERVICE_KEY, context.getSelf()));
        this.kafkaProducer = kafkaProducer;
    }

    public static Behavior<Command> create(KafkaProducer<String,Message> kafkaProducer){
        return Behaviors.setup(context -> new OcsActor(context,kafkaProducer));
    }
    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Command.class,this::onMessage)
                .build();
    }

    private Behavior<Command> onMessage(Command command) {
        getContext().getLog().info("message received: " + command.getMessage());
        getContext().getLog().info("reply is sending to dgw...");

        String topic = "topic";
        String key = "commandKey";


        Message kafkaMessage = new Message("1", command.getMessage());


        ProducerRecord<String, Message> record = new ProducerRecord<>(topic, key, kafkaMessage);


        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception == null) {
                System.out.println(String.format(
                        "Message sent successfully to topic=%s, partition=%d, offset=%d",
                        metadata.topic(), metadata.partition(), metadata.offset()
                ));
            } else {
                System.err.println("Failed to send message to Kafka: " + exception.getMessage());
            }
        });

        // Reply to the sender
        command.getReplyTo().tell(new Error("Hey, I am OCS. Your message \"" + command.getMessage() + "\" was received by me."));
        return this;
    }

}
