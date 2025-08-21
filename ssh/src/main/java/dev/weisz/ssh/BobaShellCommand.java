package dev.weisz.ssh;

import dev.weisz.boba.tea.ProgramOpts;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.SignalListener;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

public class BobaShellCommand implements Command {
    private final ProgramSupplier supplier;

    private InputStream in;
    private OutputStream out;
    private OutputStream err;

    public BobaShellCommand(ProgramSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public void setExitCallback(ExitCallback exitCallback) {

    }

    @Override
    public void setErrorStream(OutputStream outputStream) {
        this.err = outputStream;
    }

    @Override
    public void setInputStream(InputStream inputStream) {
        this.in = inputStream;
    }

    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.out = outputStream;
    }

    @Override
    public void start(ChannelSession channelSession, Environment environment) throws IOException {
        Thread.startVirtualThread(() -> {
            var program = supplier.create(in, out);
            program.run(
                    ProgramOpts.builder()
                            .input(in)
                            .output(
                                    out
                            )
                            .build()
            );

            environment.addSignal
        }).start();
    }

    @Override
    public void destroy(ChannelSession channelSession) throws Exception {

    }
}
