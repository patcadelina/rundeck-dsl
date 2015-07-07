package com.freelancer.domain

class Workflow {
    Boolean keepgoing = false
    String strategy = 'node-first'
    List<WorkflowStep> commands

    static Workflow fromMap(Map data) {
        Workflow wf = new Workflow()
        if(data.keepgoing) {
            wf.keepgoing = true
        }
        wf.strategy = data.strategy? data.strategy:'node-first'
        if(data.commands) {
            ArrayList commands = new ArrayList()
            Set handlers = new HashSet()
            def createStep = { Map map->
                WorkflowStep exec
                if (map.jobref != null) {
                    exec = JobExec.fromMap(map)
                } else if (map.exec != null || map.script != null || map.scriptfile != null || map.scripturl != null) {
                    exec = CommandExec.fromMap(map)
                } else {
                    exec = PluginStep.fromMap(map)
                }
                exec
            }
            data.commands.each{ map->
                WorkflowStep exec = createStep(map)
                if(map.errorhandler) {
                    WorkflowStep handler = createStep(map.errorhandler)
                    exec.errorHandler = handler
                }
                commands << exec
            }
            wf.commands = commands
        }
        return wf
    }
}
