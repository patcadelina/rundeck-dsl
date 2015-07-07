package com.freelancer.domain

class PluginStep extends WorkflowStep {
    Boolean nodeStep
    String type

    static PluginStep fromMap(Map data) {
        PluginStep ps = new PluginStep()
        ps.nodeStep = data.nodeStep
        ps.type = data.type

        ps.keepgoingOnSuccess = !!data.keepgoingOnSuccess
        ps.description = data.description?.toString()
        return ps
    }
}
