package com.freelancer.domain

class JobExec {
    String jobGroup
    String jobName
    String argString
    Boolean nodeStep
    String nodeFilter
    Integer nodeThreadCount
    Boolean nodeKeepgoing
    String nodeRankAttribute
    Boolean nodeRankOrderAscending

    static JobExec fromMap(Map map) {
        JobExec exec = new JobExec()
        exec.jobGroup = map.jobref.group
        exec.jobName = map.jobref.name
        if(map.jobref.args) {
            exec.argString = map.jobref.args
        }
        if(map.jobref.nodeStep in ['true',true]) {
            exec.nodeStep = true
        } else {
            exec.nodeStep = false
        }
        exec.keepgoingOnSuccess = !!map.keepgoingOnSuccess
        exec.description = map.description?.toString()
        if(map.jobref.nodefilters) {
            exec.nodeFilter = map.jobref.nodefilters.filter?.toString()
            if(exec.nodeFilter) {
                def dispatch = map.jobref.nodefilters.dispatch
                if(dispatch?.threadcount) {
                    if(dispatch.threadcount instanceof Integer) {
                        exec.nodeThreadcount = dispatch.threadcount ?: 1
                    } else {
                        exec.nodeThreadcount = Integer.parseInt(dispatch.threadcount.toString()) ?: 1
                    }
                }
                if(null!=dispatch?.keepgoing) {
                    if (dispatch.keepgoing in ['true', true]) {
                        exec.nodeKeepgoing = true
                    }else{
                        exec.nodeKeepgoing = false
                    }
                }
                if (null != dispatch?.rankOrder) {
                    exec.nodeRankOrderAscending = (dispatch.rankOrder == 'ascending')
                }
                exec.nodeRankAttribute= dispatch?.rankAttribute
            }
        }
        //nb: error handler is created inside Workflow.fromMap
        return exec
    }
}
