package dev.weisz.ssh;

import dev.weisz.boba.tea.Cmd;
import dev.weisz.boba.tea.Msg;
import dev.weisz.boba.tea.Program;
import org.apache.sshd.common.keyprovider.FileHostKeyCertificateProvider;
import org.apache.sshd.common.keyprovider.HostKeyCertificateProvider;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class SSHTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(12133);

        var hostKeys = new SimpleGeneratorHostKeyProvider(Paths.get("/tmp/a.ser"));
        sshd.setShellFactory(new BobaShellFactory(
                (input, out) -> new TestProgram()
        ));

        sshd.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, Duration.ofSeconds(5));
        sshd.setKeyPairProvider(hostKeys);
        hostKeys.loadKeys(null);

        sshd.setPasswordAuthenticator((username, password, session) -> true);
        sshd.setPublickeyAuthenticator((username, pubkey, session) -> true);
        sshd.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
        sshd.start();
        Thread.sleep(10000000);
    }

    public static final class TestProgram extends Program {

        @Override
        protected @Nullable Cmd update(Msg msg) {
            return null;
        }

        @Override
        protected String view() {
            return "Test from ssh server, q to close connection";
        }
    }
}
