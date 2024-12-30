package com.akdag.akka.keys;

import akka.actor.typed.receptionist.ServiceKey;

public class SharedServiceKeys {

    public static final ServiceKey<Command> OCS_ACTOR_SERVICE_KEY =
            ServiceKey.create(Command.class, "OCS_ACTOR_SERVICE_KEY");


}
