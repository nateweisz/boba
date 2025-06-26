package dev.weisz.components;

import dev.weisz.boba.tea.Msg;
import dev.weisz.boba.tea.Program;
import dev.weisz.boba.tea.UpdateResult;
import dev.weisz.boba.terminal.WinSize;

public abstract class LayoutBasedProgram<T> extends Program<T> {

    /**
     * Rendered every frame, store state in the model and do NOT do any IO operations inside this. Term
     * is represented by a single {@link Div} with the width and height of the active terminal. All components
     * be given as children inside this div.
     */
    protected abstract Div render(T t);

    @Override
    protected UpdateResult<T> update(T t, Msg msg) {
        // should call update on each child component (we really should just switch to a event subscription system
        // only components alive/rendered should even be able to subscribe.
        //
        return null;
    }

    @Override
    protected String view(T t) {
        return "";
    }
}
