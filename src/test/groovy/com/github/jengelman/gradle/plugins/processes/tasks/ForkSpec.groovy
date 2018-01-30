package com.github.jengelman.gradle.plugins.processes.tasks

import com.github.jengelman.gradle.plugins.processes.ProcessHandle
import com.github.jengelman.gradle.plugins.processes.ProcessHandleListener
import com.github.jengelman.gradle.plugins.processes.util.PluginSpecification
import org.gradle.process.ExecResult
import org.gradle.testkit.runner.BuildResult

class ForkSpec extends PluginSpecification {
    
    def setup() {
        buildFile << '''
plugins {
    id 'com.github.johnrengelman.processes'
}
'''
    }
    
    @SuppressWarnings('Println')
    def forkTask() {
        given:
            File testFile = file('someFile')
            
            buildFile << """
        task forkMain(type: ${Fork.name}) {
            executable = 'touch'
            workingDir = "${dir.root}"
            args "${testFile.path}"
            listener(new ${ProcessHandleListener.name}() {
                void executionStarted(${ProcessHandle.name} handle) {
                    println 'Execution Started'
                }
                void executionFinished(${ProcessHandle.name} handle, ${ExecResult.name} result) {
                    println 'Execution Finished'
                }
            })
        }

        task waitForFinish() {
            doLast {
                forkMain.processHandle.waitForFinish().assertNormalExitValue()
                println 'Process completed'
            }
        }

        forkMain.finalizedBy waitForFinish
        """
        
        when:
            BuildResult result = runner.withArguments(['forkMain', '--stacktrace'])
                .withPluginClasspath().build()
        
        then:
            assert result.output.contains('Process completed')
            assert result.output.contains('Execution Started')
            assert result.output.contains('Execution Finished')
    }
}
