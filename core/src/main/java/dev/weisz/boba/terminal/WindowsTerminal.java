package dev.weisz.boba.terminal;

import dev.weisz.boba.c.windows.x64.windows_wrapper_h;

import java.lang.foreign.Arena;

final class WindowsTerminal extends Terminal {
    @Override
    public boolean isTerminal(int fd) {
        var handle = windows_wrapper_h.GetStdHandle(windows_wrapper_h.STD_OUTPUT_HANDLE());
        try (var arena = Arena.ofConfined()) {
            var st = arena.allocate(windows_wrapper_h.C_INT);
            return windows_wrapper_h.GetConsoleMode(
                    handle,
                    st
            ) == 0;
        }
    }

    @Override
    public void makeRaw(int fd) {
        try (var arena = Arena.ofConfined()) {
            var inputReadSide = arena.allocate(windows_wrapper_h.HANDLE);
            var inputWriteSide = arena.allocate(windows_wrapper_h.HANDLE);

            var outputReadSide = arena.allocate(windows_wrapper_h.HANDLE);
            var outputWriteSide = arena.allocate(windows_wrapper_h.HANDLE);

            if (windows_wrapper_h.CreatePipe(
                    inputReadSide,
                    inputWriteSide,
                    null,
                    0
            ) == 0) {
                // handle GetLastError here
                // https://stackoverflow.com/questions/79679982/java-ffm-linker-option-capturecallstate-does-not-successfully-capture-getlast

                return;
            }

            if (windows_wrapper_h.CreatePipe(
                    outputReadSide,
                    outputWriteSide,
                    null,
                    0
            ) == 0) {
                // read above

                return;
            }

            windows_wrapper_h.
        }
    }

    @Override
    public void makeCooked(int fd) {

    }

    @Override
    public WinSize getWinSize() {
        return null;
    }

}
