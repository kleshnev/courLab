package org.example;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

import java.util.*;
class Courier {
    private final ActorRef actorRef;
    private final int maxDistance;

    public Courier(ActorRef actorRef, int maxDistance) {
        this.actorRef = actorRef;
        this.maxDistance = maxDistance;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }

    public int getMaxDistance() {
        return maxDistance;
    }
}