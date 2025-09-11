package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.IBundledOpenMarkovException;

//TODO: This exception is probably badly used, as it is likely intented to be used along
// operations requiring for a network to be opened. But this exception don't come from said operations,
// such as MainPanelListenerAssistant#getCurrentNetworkPanel, when a network is not opened it returns
// null, leading to further bugs.
public class NoNetOpenedException extends Exception implements IBundledOpenMarkovException {
    
    @Override public String toString() {
        return IBundledOpenMarkovException.toString(this);
    }
}
