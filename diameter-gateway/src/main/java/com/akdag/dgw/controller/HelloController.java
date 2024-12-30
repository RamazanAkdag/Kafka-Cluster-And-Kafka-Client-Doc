package com.akdag.dgw.controller;

import akka.actor.typed.ActorRef;
import com.akdag.akka.keys.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
@RequiredArgsConstructor
public class HelloController {

    private final ActorRef<Command> dgwActor;

    @GetMapping
    public String getHello() {

        dgwActor.tell(new Command("Hello from REST API", null));

        return "Hello";
    }
}
