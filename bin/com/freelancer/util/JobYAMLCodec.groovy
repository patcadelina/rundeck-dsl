package com.freelancer.util

import com.freelancer.domain.Option
import com.freelancer.domain.Orchestrator
import com.freelancer.domain.ScheduledExecution
import com.freelancer.domain.Workflow
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor

class JobYAMLCodec {

    static decode = { input ->
        Yaml yaml = new Yaml(new SafeConstructor())
        def data
        if(input instanceof File) {
            input = input.getInputStream()
        }
        data = yaml.load(input)
    }

    static ScheduledExecution fromMap(Map data) {
        ScheduledExecution se = new ScheduledExecution()
        se.jobName = data.name
        se.groupPath = data['group']? data['group'] : null
        se.description = data.description
        if(data.orchestrator) {
            se.orchestrator = Orchestrator.fromMap(data.orchestrator);
        }
        se.loglevel = data.loglevel? data.loglevel : 'INFO'
        se.project = data.project
        if (data.uuid) {
            se.uuid = data.uuid
        }
        se.timeout = data.timeout? data.timeout.toString() : null
        se.retry = data.retry? data.retry.toString() : null
        if(data.options) {
            TreeSet options = new TreeSet()
            data.options.keySet().each{ optname ->
                Option opt = Option.fromMap(optname, data.options[optname])
                options<<opt
            }
            se.options = options
        }
        if(data.sequence) {
            Workflow wf = Workflow.fromMap(data.sequence as Map)
            se.workflow = wf
        }
        if(data.schedule) {
            se.scheduled = true
            if(data.schedule.crontab) {
                se.crontabString = data.schedule.crontab
                se.parseCrontabString(data.schedule.crontab)
            } else {
                if(data.schedule.time && data.schedule.time instanceof Map) {
                    if(null != data.schedule.time.seconds) {
                        se.seconds = data.schedule.time.seconds
                    }
                    if(null != data.schedule.time.minute) {
                        se.minute = data.schedule.time.minute
                    }
                    if(null != data.schedule.time.hour) {
                        se.hour = data.schedule.time.hour
                    }
                }
                if(null != data.schedule.month) {
                    se.month = data.schedule.month
                } else {
                    se.month = '*'
                }
                if(null != data.schedule.year) {
                    se.year = data.schedule.year
                } else {
                    se.year = '*'
                }
                if(data.schedule.dayofmonth && data.schedule.dayofmonth instanceof Map
                        && null != data.schedule.dayofmonth.day) {
                    se.dayOfMonth = data.schedule.dayofmonth.day
                    se.dayOfWeek = '?'
                } else if(data.schedule.weekday && data.schedule.weekday instanceof Map
                        && null != data.schedule.weekday.day) {
                    se.dayOfWeek = data.schedule.weekday.day
                    se.dayOfMonth = '?'
                } else {
                    se.dayOfMonth = '?'
                    se.dayOfWeek = '*'
                }
            }
        }
        if(data.multipleExecutions) {
            se.multipleExecutions = data.multipleExecutions? true:false
        }
        if(data.nodefilters){
            se.nodesSelectedByDefault = null!=data.nodesSelectedByDefault?(data.nodesSelectedByDefault?true:false):true
            if(data.nodefilters.dispatch){
                se.nodeThreadcount = data.nodefilters.dispatch.threadcount ?: 1
                if(data.nodefilters.dispatch.containsKey('keepgoing')){
                    se.nodeKeepgoing = data.nodefilters.dispatch.keepgoing
                }
                if(data.nodefilters.dispatch.containsKey('excludePrecedence')){
                    se.nodeExcludePrecedence = data.nodefilters.dispatch.excludePrecedence
                }
                if(data.nodefilters.dispatch.containsKey('rankAttribute')){
                    se.nodeRankAttribute = data.nodefilters.dispatch.rankAttribute
                }
                if(data.nodefilters.dispatch.containsKey('rankOrder')){
                    se.nodeRankOrderAscending = data.nodefilters.dispatch.rankOrder=='ascending'
                }
            }
            if(data.nodefilters.filter){
                se.doNodedispatch=true
                se.filter= data.nodefilters.filter
            }else{
                def map = [include: [:], exclude: [:]]
                if (data.nodefilters.include) {
                    se.doNodedispatch = true
                    data.nodefilters.include.keySet().each { inf ->
                        if (null != filterKeys[inf]) {
                            map.include[inf] = data.nodefilters.include[inf]
                        }
                    }

                }
                if (data.nodefilters.exclude) {
                    se.doNodedispatch = true
                    data.nodefilters.exclude.keySet().each { inf ->
                        if (null != filterKeys[inf]) {
                            map.exclude[inf] = data.nodefilters.exclude[inf]
                        }
                    }
                }
                se.filter = asFilter(map)
            }
        }
        if(data.notification){
            def nots=[]
            data.notification.keySet().findAll{it.startsWith('on')}.each{ name->
                if(data.notification[name]){
                        //support for built-in notification types
                    ['urls','email'].each{ subkey->
                        if(data.notification[name][subkey]){
                            nots << Notification.fromMap(name, [(subkey):data.notification[name][subkey]])
                        }
                    }
                    if(data.notification[name]['plugin']){
                        def pluginElement=data.notification[name]['plugin']
                        def plugins=[]
                        if(pluginElement instanceof Map){
                            plugins=[pluginElement]
                        }else if(pluginElement instanceof Collection){
                            plugins= pluginElement
                        }else{

                        }
                        plugins.each{ plugin->
                            def n=Notification.fromMap(name, plugin)
                            if(n){
                                nots << n
                            }
                        }
                    }
                }
            }
            se.notifications=nots
        }
        return se
    }
}
