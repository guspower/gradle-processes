package com.github.jengelman.gradle.plugins.processes.util

import org.gradle.api.internal.file.DefaultFileCollectionFactory
import org.gradle.api.internal.file.DefaultFileLookup
import org.gradle.api.internal.file.DefaultFileOperations
import org.gradle.api.internal.file.DefaultSourceDirectorySetFactory
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileLookup
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.internal.file.collections.DefaultDirectoryFileTreeFactory
import org.gradle.api.internal.file.collections.DirectoryFileTreeFactory
import org.gradle.api.tasks.util.PatternSet
import org.gradle.api.tasks.util.internal.PatternSets
import org.gradle.internal.Factory
import org.gradle.internal.hash.DefaultContentHasherFactory
import org.gradle.internal.hash.DefaultFileHasher
import org.gradle.internal.hash.DefaultStreamHasher
import org.gradle.internal.nativeintegration.filesystem.FileSystem
import org.gradle.internal.nativeintegration.services.NativeServices
import org.gradle.internal.reflect.DirectInstantiator
import org.gradle.internal.resource.local.FileResourceConnector
import org.gradle.internal.resource.local.FileResourceRepository
import org.gradle.process.internal.*

class TestFiles {
    
    private static final FileSystem FILE_SYSTEM = {
        NativeServices.initialize(new File(System.properties['user.dir']))
        NativeServices.instance.get(FileSystem)
    }()
    
    private static final DefaultFileLookup FILE_LOOKUP = new DefaultFileLookup(FILE_SYSTEM, PatternSets.getNonCachingPatternSetFactory())
    private static final DefaultExecActionFactory EXEC_FACTORY = new DefaultExecActionFactory(resolver())
    
    static FileLookup fileLookup() {
        return FILE_LOOKUP
    }
    
    static FileSystem fileSystem() {
        return FILE_SYSTEM
    }
    
    static FileResourceRepository fileRepository() {
        return new FileResourceConnector(FILE_SYSTEM)
    }
    
    static FileResolver resolver() {
        return FILE_LOOKUP.getFileResolver()
    }
    
    static FileResolver resolver(File baseDir) {
        return FILE_LOOKUP.getFileResolver(baseDir)
    }
    
    static DirectoryFileTreeFactory directoryFileTreeFactory() {
        return new DefaultDirectoryFileTreeFactory(getPatternSetFactory(), fileSystem())
    }
    
    static FileOperations fileOperations(File basedDir) {
        return new DefaultFileOperations(resolver(basedDir), null, null, DirectInstantiator.INSTANCE, fileLookup(), directoryFileTreeFactory(), streamHasher(), fileHasher(), execFactory())
    }
    
    static DefaultStreamHasher streamHasher() {
        return new DefaultStreamHasher(new DefaultContentHasherFactory())
    }
    
    static DefaultFileHasher fileHasher() {
        return new DefaultFileHasher(streamHasher())
    }
    
    static FileCollectionFactory fileCollectionFactory() {
        return new DefaultFileCollectionFactory()
    }
    
    static SourceDirectorySetFactory sourceDirectorySetFactory() {
        return new DefaultSourceDirectorySetFactory(resolver(), new DefaultDirectoryFileTreeFactory())
    }
    
    static SourceDirectorySetFactory sourceDirectorySetFactory(File baseDir) {
        return new DefaultSourceDirectorySetFactory(resolver(baseDir), new DefaultDirectoryFileTreeFactory())
    }
    
    static ExecFactory execFactory() {
        return EXEC_FACTORY
    }
    
    static ExecActionFactory execActionFactory() {
        return execFactory()
    }
    
    static ExecHandleFactory execHandleFactory() {
        return execFactory()
    }
    
    static ExecHandleFactory execHandleFactory(File baseDir) {
        return execFactory().forContext(resolver(baseDir), DirectInstantiator.INSTANCE)
    }
    
    static JavaExecHandleFactory javaExecHandleFactory(File baseDir) {
        return execFactory().forContext(resolver(baseDir), DirectInstantiator.INSTANCE)
    }
    
    static Factory<PatternSet> getPatternSetFactory() {
        return resolver().getPatternSetFactory()
    }
    
    static String systemSpecificAbsolutePath(String path) {
        return new File(path).getAbsolutePath()
    }
    
}
