package dev.weisz.boba.tea;

import dev.weisz.boba.Renderer;
import dev.weisz.boba.StandardRenderer;
import dev.weisz.boba.terminal.Terminal;
import dev.weisz.boba.terminal.WinSize;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

import java.io.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Program {
    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);

    private final BlockingQueue<Msg> msgQueue = new LinkedBlockingQueue<>();
    private Terminal terminal;
    private Renderer renderer;

    protected abstract @Nullable Cmd update(Msg msg);
    protected abstract String view();

    public WinSize getWinSize() {
        return renderer.getWinSize();
    }

    public void run(ProgramOpts opts) {
        // this might not support any other input and output streams than the default ones
        // we will support another mode of input like tty later
        // 0 = input
        // 1 = output
        // ref look at FileDescriptor.java
        terminal = Terminal.create();
        if (!terminal.isTerminal(0)) {
            throw new UnsupportedOperationException("The system input is not a terminal.");
        }
        if (!terminal.isTerminal(1)) {
            throw new UnsupportedOperationException("The system output is not a terminal.");
        }

        // we need the input and output both to be raw
        terminal.makeRaw(0);

        // rn we will assume that its running inside a terminal but in the future we need to handle everything
        // from my testing, I don't think these signals are ever sent (even when terminal is closed forcefully)
        if (!opts.useDefaultSignalHandler()) {
            opts.interruptHandler().addHandler(
                    terminal,
                    msgQueue::offer
            );

            opts.terminateHandler().addHandler(
                    terminal,
                    msgQueue::offer
            );
        }

        // TODO: discuss further on discord the usage of this "unsafe" api
        // from some core java devs: they dont have any plans to remove it and if they do
        // a actual api will replace it
        // this is how apache handled avoiding the usage of it:
        // https://issues.apache.org/jira/browse/HADOOP-19329
        opts.winchHandler().addHandler(
                terminal,
                msgQueue::offer
        );

        // TODO: allow configurable renderer
        renderer = new StandardRenderer(opts.output(), opts.fps());
        renderer.hideCursor();

        if (!opts.startupTitle().isEmpty()) {
             renderer.setWindowTitle(opts.startupTitle());
        }

        if (opts.useAltScreen()) {
            renderer.enterAltScreen();
        }

        if (opts.useBracketedPaste()) {
            renderer.enableBracketedPaste();
        }

        if (opts.enableMouseCellMotion()) {
            renderer.enableMouseCellMotion();
            renderer.enableMouseSGRMode();
        }// TODO: mouse all motion opts

        if (opts.reportFocusChange()) {
            renderer.enableReportFocus();
        }

        // set initial size inside the renderer
        processCmd(() -> {
            WinSize winSize = terminal.getWinSize();
            return new Msg.WindowSizeMsg(winSize.height(), winSize.width());
        });

        renderer.start();
        renderer.write(view());

        LOGGER.debug("Program started with initial frame printed.");

        if (opts.input() != null) {
            Thread.startVirtualThread(() -> handleUserInput(opts.input()));
        }

        // handle settings term back to old settings on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            terminal.makeCooked(0);
        }));

        try {
            eventLoop();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        // write last frame
        renderer.write(view());
    }

    /**
     * <p>Handles the processing of every message inside the {@link Program#msgQueue}, handles any
     * internal messages such as {@link Msg.QuitMsg} first. After that it passes messages onto the
     * renderer to handle before passing it to the {@link Program#update(Msg)}. When this
     * function returns a value it will indicate that the program has finished. It will only return
     * when an {@link Msg.QuitMsg} or {@link Msg.InteruptMsg} are received.</p>
     */
    private void eventLoop() throws InterruptedException {
        while (true) {
            Msg msg = msgQueue.take();

            switch (msg) {
                case Msg.QuitMsg _ -> {
                    return;
                }
                case Msg.ClearScreenMsg _ ->
                    renderer.clearScreen();

                case Msg.SetTitleMsg(String title) ->
                    renderer.setWindowTitle(title);

                case BatchCmd.BatchMsg(List<Cmd> cmds) ->
                    cmds.forEach(this::processCmd);

                default -> { /* ignore */ }
            }

            renderer.handleMessages(msg);

            var updateCmd = update(msg);
            if (updateCmd != null) {
                // handle command async
                processCmd(updateCmd);
            }

            renderer.write(view());
        }
    }

    private void processCmd(Cmd cmd) {
        Thread.startVirtualThread(() -> {
            Msg msg;
            try {
                msg = cmd.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (msg != null) {
                msgQueue.add(msg);
            }
        });
    }

    // https://ecma-international.org/wp-content/uploads/ECMA-48_5th_edition_june_1991.pdf
    // ^^^^ use this as a reference
    private void handleUserInput(InputStream in) {
        while (true) {
            try {
                int input = in.read();

                if (input == -1) {
                    continue;
                }

                processCmd(() -> new Msg.KeyClickMsg((char) input));

                LOGGER.debug("Input: (Raw) {} (Char) {}", input, (char) input);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
