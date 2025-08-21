package dev.weisz.boba;

import dev.weisz.boba.tea.Msg;
import dev.weisz.boba.terminal.Terminal;

import java.util.function.Consumer;

public interface SignalHandler {
    void addHandler(Terminal terminal, Consumer<Msg> msgConsumer);

    class Winch implements SignalHandler {
        @Override
        public void addHandler(Terminal terminal, Consumer<Msg> msgConsumer) {
            sun.misc.Signal.handle(new sun.misc.Signal("WINCH"), _ -> {
                var winSize = terminal.getWinSize();
                msgConsumer.accept(
                        new Msg.WindowSizeMsg(winSize.height(), winSize.width())
                );
            });
        }
    }

    class Interrupt implements SignalHandler {

        @Override
        public void addHandler(Terminal terminal, Consumer<Msg> msgConsumer) {
            sun.misc.Signal.handle(new sun.misc.Signal("INT"), _ -> {
                // always will be successfully since uncapped queue
                msgConsumer.accept(new Msg.InteruptMsg());
            });
        }
    }

    class Terminate implements SignalHandler {

        @Override
        public void addHandler(Terminal terminal, Consumer<Msg> msgConsumer) {
            sun.misc.Signal.handle(new sun.misc.Signal("TERM"), _ -> {
                msgConsumer.accept(new Msg.QuitMsg());
            });
        }
    }
}
