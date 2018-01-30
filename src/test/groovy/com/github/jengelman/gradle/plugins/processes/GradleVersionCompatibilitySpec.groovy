package com.github.jengelman.gradle.plugins.processes

import com.github.jengelman.gradle.plugins.processes.tasks.Fork
import com.github.jengelman.gradle.plugins.processes.util.GradleVersionRunnerFactory
import com.github.jengelman.gradle.plugins.processes.util.PluginSpecification
import org.gradle.testkit.runner.BuildResult
import spock.lang.Unroll

class GradleVersionCompatibilitySpec extends PluginSpecification {
    
    @Unroll
    def 'plugin works with Gradle #gradleVersion'() {
        given:
            File testFile = dir.newFile('touchFile')
            runner = GradleVersionRunnerFactory.create()
                .withGradleVersion(gradleVersion).withPluginClasspath().withProjectDir(dir.root)
            
            buildFile << """
        plugins {
            id 'com.github.johnrengelman.processes'
        }

        task forkMain(type: ${Fork.name}) {
            executable = 'touch'
            workingDir = "${dir.root}"
            args "${testFile.path}"
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
            BuildResult result = runner.withArguments('forkMain').build()
        
        then:
            assert result.output.contains('Process completed')
        
        where:
            gradleVersion << ['4.5', '3.5', '1.8', '1.9', '1.10', '1.11', '1.12']
    }
    
}
