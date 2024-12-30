package com.akdag.config;


import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import com.akdag.akka.OcsActor;
import com.akdag.akka.keys.Command;
import com.akdag.kafka.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AkkaConfig {

    @Bean
    public ActorSystem<Void> actorSystem() {
        return ActorSystem.create(Behaviors.empty(), "AkkaCluster");
    }

    @Bean
    public ActorRef<Command> ocsActor(ActorSystem<Void> actorSystem, KafkaProducer<String, Message> kafkaProducer) {
        return actorSystem.systemActorOf(OcsActor.create(kafkaProducer), "ocsActor", Props.empty());
    }

}