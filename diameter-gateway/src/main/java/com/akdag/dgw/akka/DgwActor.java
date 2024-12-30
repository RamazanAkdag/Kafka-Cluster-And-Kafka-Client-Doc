package com.akdag.dgw.akka;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import com.akdag.akka.keys.Command;
import com.akdag.akka.keys.Error;
import com.akdag.akka.keys.ListingMessage;
import com.akdag.akka.keys.SharedServiceKeys;

import java.util.Optional;

public class DgwActor extends AbstractBehavior<Command> {

    private ActorRef<Command> ocsActor;
    private final ActorRef<Receptionist.Listing> listingAdapter;

    public DgwActor(ActorContext<Command> context) {
        super(context);

        listingAdapter = context.messageAdapter(Receptionist.Listing.class, ListingMessage::new);

        context.getSystem().receptionist().tell(Receptionist.subscribe(SharedServiceKeys.OCS_ACTOR_SERVICE_KEY,listingAdapter));

    }

    public static Behavior<Command> create(){
        return Behaviors.setup(
                context ->
                {
                    DgwActor dgwActor = new DgwActor(context);

                    return dgwActor;
                }
        );
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(ListingMessage.class, this::onListingMessage)
                .onMessage(Error.class,this::onError)
                .onMessage(Command.class,this::onCommand)
                .build();
    }

    private Behavior<Command> onListingMessage(ListingMessage message) {
        Receptionist.Listing listing = message.getListing();
        Optional<ActorRef<Command>> actor = listing.getServiceInstances(SharedServiceKeys.OCS_ACTOR_SERVICE_KEY).stream().findFirst();
        if (actor.isPresent()) {
            getContext().getLog().info("Discovered OCS actor: {}", actor.get());
            ocsActor = actor.get();
        } else {
            getContext().getLog().warn("No OCS actor found in the cluster!");
        }

        return this;
    }

    private Behavior<Command> onError(Error error) {
        getContext().getLog().error("error occured in ocs :" + error.getMessage());
        return this;
    }


    private Behavior<Command> onCommand(Command command) {
        getContext().getLog().info("message forwarding to ocs : " + command.getMessage());

        ocsActor.tell(new Command(command.getMessage(),getContext().getSelf()));

        return this;
    }

}
