package com.freelancer.domain

class Option {
    String name
    Boolean enforced
    Boolean required
    String description
    Integer sortIndex
    String defaultValue
    String regex
    SortedSet<String> values
    Boolean multivalued
    String delimiter
    Boolean secureInput
    Boolean secureExposed

    static Option fromMap(String name, Map data) {
        Option opt = new Option()
        opt.name = name
        opt.enforced = data.enforced?true:false
        opt.required = data.required?true:false
        if(data.description) {
            opt.description = data.description
        }
        if(data.sortIndex != null) {
            opt.sortIndex = data.sortIndex
        }
        if(data.value) {
            opt.defaultValue = data.value
        }
        if(null != data.regex) {
            opt.regex = data.regex
        }
        if(data.values) {
            opt.values = data.values instanceof Collection?new TreeSet(data.values):new TreeSet([data.values])
        }
        if(data.multivalued) {
            opt.multivalued = true
            if(data.delimiter) {
                opt.delimiter = data.delimiter
            }
        }
        if(data.secure) {
            opt.secureInput = Boolean.valueOf(data.secure)
        } else {
            opt.secureInput = false
        }
        if(opt.secureInput && data.valueExposed) {
            opt.secureExposed = Boolean.valueOf(data.valueExposed)
        } else {
            opt.secureExposed = false
        }
        return opt
    }
}
