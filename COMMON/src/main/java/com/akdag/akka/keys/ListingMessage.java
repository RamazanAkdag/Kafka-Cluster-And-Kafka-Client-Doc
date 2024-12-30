package com.akdag.akka.keys;

import akka.actor.typed.receptionist.Receptionist;

public class ListingMessage extends Command{
    private final Receptionist.Listing listing;
    public ListingMessage( Receptionist.Listing listing) {
        super(null,null);
        this.listing = listing;
    }

    public Receptionist.Listing getListing() {
        return listing;
    }
}
