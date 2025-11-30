package org.openmarkov.gui.action;

import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.ProbNet;

public class ImposePolicyEdit extends PNEdit {
    /**
     * @param probNet {@code ProbNet}
     */
    public ImposePolicyEdit(ProbNet probNet) {
        super(probNet);
    }

    @Override
    protected void doEdit() throws DoEditException {

    }



}
