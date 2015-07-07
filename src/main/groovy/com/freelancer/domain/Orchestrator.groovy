package com.freelancer.domain

class Orchestrator {
    String type

    static Orchestrator fromMap(Map data) {
        Orchestrator o = new Orchestrator(type: data.type)
        return o
    }
}
