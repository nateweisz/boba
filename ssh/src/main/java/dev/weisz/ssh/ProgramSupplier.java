package dev.weisz.ssh;

import dev.weisz.boba.tea.Program;

import java.io.InputStream;
import java.io.OutputStream;

@FunctionalInterface
public interface ProgramSupplier {
    Program create(InputStream in, OutputStream out);
}
