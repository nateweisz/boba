package dev.weisz.ssh;

import dev.weisz.boba.SignalHandler;
import dev.weisz.boba.tea.Msg;
import dev.weisz.boba.terminal.Terminal;
import dev.weisz.boba.terminal.WinSize;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.Signal;
import org.apache.sshd.server.SignalListener;

import java.util.function.Consumer;

public final class SSHSignalHandler {

    record Winch(Environment environment) implements SignalHandler {

        @Override
        public void addHandler(Terminal terminal, Consumer<Msg> msgConsumer) {
            environment.addSignalListener((channel, signal) -> {
                var winSize = new WinSize()
            }, Signal.WINCH);
        }
    }
}
