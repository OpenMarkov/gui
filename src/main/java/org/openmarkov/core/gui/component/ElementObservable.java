/**
 * OpenMarkov - ElementObservable.java
 */
package org.openmarkov.core.gui.component;


import java.util.Observable;


/**
 * Convenience class to implement the Observable-Observer design pattern between
 * the OpenMarkov GUI panels. <br>
 * When you first look at the online documentation for
 * Observable, it's a bit confusing because it appears that you can use an
 * ordinary Observable object to manage the updates. But this doesn't work; try
 * it  inside NodePropertiesDialog (the observer), create an Observable object
 * instead of a ElementObservable object and see what happens: nothing. <br>
 * To get
 * an effect, you must inherit from Observable and somewhere in your
 * derived-class code call setChanged( ). This is the method that sets the
 * 'changed' flag, which means that when you call notifyObservers( ) all of the
 * observers will, in fact, get notified.
 * 
 * @author jlgozalo
 * @version 1.0 13 Feb 2010
 */
public class ElementObservable extends Observable {

	public void notifyObservers(Object b) {

		setChanged();
		super.notifyObservers( b );
	}
}
