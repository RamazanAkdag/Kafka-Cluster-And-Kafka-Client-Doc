package com.akdag.dgw.config;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.Behaviors;
import com.akdag.akka.keys.Command;
import com.akdag.dgw.akka.DgwActor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AkkaConfiguration {

    @Bean
    public ActorSystem<Void> actorSystem(){
        return ActorSystem.create(Behaviors.empty(),"AkkaCluster");
    }

    @Bean
    public ActorRef<Command> dgwActor(ActorSystem<Void> actorSystem) {

        return actorSystem.systemActorOf(
                DgwActor.create(),
                "DgwActor", Props.empty()
        );
    }


}
