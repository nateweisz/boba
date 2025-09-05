package dev.weisz.ssh;

import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

import java.io.IOException;

public record BobaShellFactory(ProgramSupplier programSupplier) implements ShellFactory {
    @Override
    public Command createShell(ChannelSession channelSession) {
        return new BobaShellCommand(programSupplier);
    }
}
