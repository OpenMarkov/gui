package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.BundledOpenMarkovException;
import org.openmarkov.core.model.network.potential.PotentialRole;

public class WrongRoleException extends BundledOpenMarkovException {
    public final PotentialRole expectedRole;
    public final PotentialRole foundRole;
    
    public WrongRoleException(PotentialRole expectedRole, PotentialRole foundRole) {
        this.expectedRole = expectedRole;
        this.foundRole = foundRole;
    }
    
}
