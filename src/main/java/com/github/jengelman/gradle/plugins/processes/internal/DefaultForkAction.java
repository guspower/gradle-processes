package com.github.jengelman.gradle.plugins.processes.internal;

import com.github.jengelman.gradle.plugins.processes.ProcessHandle;
import com.github.jengelman.gradle.plugins.processes.ProcessHandleListener;
import org.gradle.internal.file.PathToFileResolver;
import org.gradle.process.internal.DefaultExecHandleBuilder;
import org.gradle.process.internal.ExecException;
import org.gradle.process.internal.ExecHandle;

import java.util.concurrent.Executor;

import static com.github.jengelman.gradle.plugins.processes.internal.ExecHandleWrapper.wrap;

public class DefaultForkAction extends DefaultExecHandleBuilder implements ForkAction {

    public DefaultForkAction(PathToFileResolver fileResolver, Executor executor) {
        super(fileResolver, executor);
    }

    public ProcessHandle fork() throws ExecException {
        ExecHandle execHandle = build();
        return wrap(execHandle.start(), isIgnoreExitValue());
    }

    @Override
    public ForkAction listener(ProcessHandleListener listener) {
        super.listener(new ExecHandleListenerAdapter(listener));
        return this;
    }


}
