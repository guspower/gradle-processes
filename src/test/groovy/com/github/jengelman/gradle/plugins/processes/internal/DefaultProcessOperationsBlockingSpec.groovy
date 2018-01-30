package com.github.jengelman.gradle.plugins.processes.internal

import com.github.jengelman.gradle.plugins.processes.util.TestFiles
import com.github.jengelman.gradle.plugins.processes.util.TestMain
import org.gradle.api.Action
import org.gradle.api.internal.file.DefaultFileOperations
import org.gradle.internal.classloader.ClasspathUtil
import org.gradle.internal.reflect.DirectInstantiator
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.gradle.process.JavaExecSpec
import org.gradle.process.internal.ExecException
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DefaultProcessOperationsBlockingSpec extends Specification {
    
    private DefaultProcessOperations processOperations
    
    @Rule
    TemporaryFolder tmpDir
    
    def setup() {
        processOperations = new DefaultProcessOperations(DirectInstantiator.INSTANCE, resolver(), fileOps())
    }
    
    def javaexec() {
        File testFile = tmpDir.newFile('someFile')
        List<File> files = ClasspathUtil.getClasspath(this.class.classLoader).asFiles
        
        when:
            ExecResult result = processOperations.javaexec { JavaExecSpec spec ->
                spec.classpath(files)
                spec.main = TestMain.name
                spec.args testFile.absolutePath
            } as Action<JavaExecSpec>
        
        then:
            testFile.isFile()
            result.exitValue == 0
    }
    
    def javaexecWithNonZeroExitValueShouldThrowException() {
        when:
            processOperations.javaexec { JavaExecSpec spec ->
                spec.main = 'org.gradle.UnknownMain'
            }
        
        then:
            thrown(ExecException)
    }
    
    def javaexeckWithNonZeroExitValueAndIgnoreExitValueShouldNotThrowException() {
        when:
            ExecResult result = processOperations.javaexec { JavaExecSpec spec ->
                spec.main = 'org.gradle.UnknownMain'
                spec.ignoreExitValue = true
            }
        
        then:
            result.exitValue != 0
    }
    
    def exec() {
        given:
            File testFile = tmpDir.newFile('someFile')
        
        when:
            ExecResult result = processOperations.exec { ExecSpec spec ->
                spec.executable = 'touch'
                spec.workingDir = tmpDir.root
                spec.args testFile.name
            }
        
        then:
            testFile.isFile()
            result.exitValue == 0
    }
    
    def execWithNonZeroExitValueShouldThrowException() {
        when:
            processOperations.exec { ExecSpec spec ->
                spec.executable = 'touch'
                spec.workingDir = tmpDir.root
                spec.args tmpDir.root.name + '/nonExistingDir/someFile'
            }
        
        then:
            thrown(ExecException)
    }
    
    def execWithNonZeroExitValueAndIgnoreExitValueShouldNotThrowException() {
        when:
            ExecResult result = processOperations.exec { ExecSpec spec ->
                spec.ignoreExitValue = true
                spec.executable = 'touch'
                spec.workingDir = tmpDir.root
                spec.args tmpDir.root.name + '/nonExistingDir/someFile'
            }
        
        then:
            result.exitValue != 0
    }
    
    def resolver() {
        return TestFiles.resolver(tmpDir.root)
    }
    
    private DefaultFileOperations fileOps() {
        return TestFiles.fileOperations(tmpDir.root)
    }
    
}
