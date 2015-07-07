package com.freelancer.support

import com.freelancer.support.BaseNodeFilters

class ExecutionContext extends BaseNodeFilters {
    String project
    String loglevel = 'WARN'
    String timeout
    String retry
    Integer nodeThreadcount = 1
    Boolean nodeKeepgoing = false
    String nodeRankAttribute
    Boolean nodeRankOrderAscending = true
    Boolean doNodedispatch = false
}
