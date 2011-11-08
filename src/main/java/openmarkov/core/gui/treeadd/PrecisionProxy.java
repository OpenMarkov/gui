package openmarkov.core.gui.treeadd;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

/**
 * Class PrecisionProxy stores precision of displayed values in the ADD/Tree component
 */
public class PrecisionProxy {

	/**
	 * 
	 */
	private HashMap<Object,Integer> mapPrecision;
	
	/**
	 * 
	 */
	private int defaultPrecision;

	
	/**
	 * @param utilityPrecision
	 * @param chancePrecision
	 */
	public PrecisionProxy( int precision ) {
		setDefaultPrecision (precision);
		
		mapPrecision= new HashMap<Object,Integer>();
	}
		
	/**
	 * @param precision
	 */
	public void setDefaultPrecision(int precision) {
		defaultPrecision= precision;
	}

	/**
	 * @return
	 */
	public int getDefaultPrecision() {
		return defaultPrecision;
	}
		
	/**
	 * @param n
	 * @param precision
	 */
	public void setObjectPrecision(Object n,int precision) {
		// Overwrite existing object
		mapPrecision.put(n, new Integer(precision));
	}

	/**
	 * @param n
	 * @return
	 */
	public int getObjectPrecision(Object n) {
		if( mapPrecision.containsKey(n) ) {
			return mapPrecision.get(n).intValue();
		}
		
		return getDefaultPrecision();
	}
	
	/**
	 * @param n
	 */
	public void removeObjectPrecision(Object n) {
		mapPrecision.remove(n);
	}

	public Vector<Object> removeAllObjectPrecisions() {
		Vector<Object> v= new Vector<Object>();
		v.addAll(mapPrecision.keySet());
		
		mapPrecision.clear();
		return v;
	}
	
	/**
	 * @param valor
	 * @return
	 */
	public String formatValue(Object obj, double value) {
		NumberFormat nf = NumberFormat.getInstance(new Locale("en"));
		
		int precision= getObjectPrecision (obj);
		
		nf.setMaximumFractionDigits (precision);
		nf.setMinimumFractionDigits (precision);
		
		return nf.format (value);
	}
}