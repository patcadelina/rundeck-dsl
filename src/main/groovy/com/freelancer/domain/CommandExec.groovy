package com.freelancer.domain

class CommandExec {
    Boolean adhocExecution = false
    String adhocRemoteString
    String adhocLocalString
    String adhocFilePath
    String scriptInterpreter
    Boolean interpreterArgsQuoted
    String fileExtension
    String argString

    static CommandExec fromMap(Map data) {
        CommandExec ce = new CommandExec()
        if(data.exec != null) {
            ce.adhocExecution = true
            ce.adhocRemoteString = data.exec.toString()
        }else if(data.script != null) {
            ce.adhocExecution = true
            ce.adhocLocalString = data.script.toString()
        }else if(data.scriptfile != null) {
            ce.adhocExecution = true
            ce.adhocFilepath = data.scriptfile.toString()
        }else if(data.scripturl != null) {
            ce.adhocExecution = true
            ce.adhocFilepath = data.scripturl.toString()
        }
        if(data.scriptInterpreter != null && !ce.adhocRemoteString) {
            ce.scriptInterpreter = data.scriptInterpreter
            ce.interpreterArgsQuoted = !!data.interpreterArgsQuoted
        }
        if(data.fileExtension != null && !ce.adhocRemoteString) {
            ce.fileExtension = data.fileExtension
        }
        if(data.args != null && !ce.adhocRemoteString) {
            ce.argString = data.args.toString()
        }
        ce.keepgoingOnSuccess = !!data.keepgoingOnSuccess
        ce.description = data.description?.toString()
        //nb: error handler is created inside Workflow.fromMap
        return ce
    }
}
