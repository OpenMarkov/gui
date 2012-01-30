package org.openmarkov.core.gui.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
//TODO leak potentials
//TODO check that columns sum one
public class ICITablePotentialValueEdit extends  SimplePNEdit {
	/**
	 * The column of the table where is the potential
	 */
	private int col;
	/**
	 * The row of the table where is the potential
	 */
	private int row;
	/**
	 * The new value of the potential
	 */
	private Double newValue;
	/**
	 * The node
	 */
	private ProbNode probNode;
	/**
	 * 
	 */
	private int decimalPositions = 10;
	/**
	 * 
	 */
	private ArrayList<double []> lastNoisyValues;
	
	/**
	 * 
	 */
	private ArrayList<double []> newNoisyValues;
	
	/*
	 * 
	 */
	private Object[][] editableTableValues = null;
	/*
	 * 
	 */
	private ICIPotential iciPotential;
	/**
	 * 
	 */
	protected Logger logger;
	/**
	 * 
	 */
	private ArrayList<Variable> variables;
	/**
	 * 
	 */
	private double[] lastNoisyParameters;
	/**
	 * 
	 */
	private double[] newNoisyParameters;
	/**
	 * 
	 */
	private Variable noisyVariable;
	
	/**
	 * 
	 */
	private double[] lastLeakyParameters;
	/**
	 * 
	 */
	private double[] newLeakyParameters;
	/**
	 * 
	 */
	private boolean leakyFlag = false;
	
//	
	private int position = 0;
	
	private int columnGroup = 0;
	/**
	 * A list that store the edition order 
	 */
	private LinkedList<Integer> priorityList;
	// Constructor
	/**
	 * Creates a new <code>NodePotentialEdit</code> specifying the node to be 
	 * edited, the new value of the potential, the row and column where is the
	 * value to be modified and a priority list for potentials updating.   
	 * 
	 * @param probNode the node to be edited
	 * @param newValue the new value
	 * @param col the column in the edited table
	 * @param row the row in the edited table
	 * */
	public ICITablePotentialValueEdit(ProbNode probNode, Double 
			newValue, int row, int col, LinkedList<Integer> priorityList) {
		this.probNode = probNode;
		this.row = row;
		this.col = col;
		this.newValue = newValue;
		this.priorityList = priorityList;
		
		this.iciPotential = (ICIPotential) getThisICIPotential(probNode.getPotentials());
		this.variables = iciPotential.getVariables();
		
		int conditionedStates = variables.get(0).getNumStates();
		int numColumnsParents []= new int[variables.size()];
		for (int i = 1; i < variables.size(); ++i) {
			numColumnsParents [i-1] = variables.get(i).getNumStates();
		}
		numColumnsParents [variables.size()-1] = 1;
		
		int acummulativeColumns[] =  new int[variables.size()];
		acummulativeColumns[0]= numColumnsParents [0];
		for (int i = 1; i < numColumnsParents.length ; ++i) {
			acummulativeColumns [i]= numColumnsParents [i] + acummulativeColumns[i-1];
		}
		
		
		columnGroup = 0;
		//leak
		if (col == acummulativeColumns[acummulativeColumns.length-1]){//last column for the table leak potential
			leakyFlag = true ;
			this.lastLeakyParameters = iciPotential.getLeakyParameters();
			this.position = (columnGroup)* conditionedStates + (conditionedStates+1) - row;
			lastLeakyParameters[position] = newValue;
			this.newLeakyParameters = lastLeakyParameters;
			//initializes priorityList for the leaky potential
			for (int i = 0 ;i <conditionedStates; i++){
				this.priorityList.add(i);
			}
			
		//noisy	
		}else{
			leakyFlag = false ;
			for (int i = 0; i < acummulativeColumns.length -1 ; ++i) {
				if (i==0){ 
					if (col<=acummulativeColumns[i]){
						this.noisyVariable = variables.get(i+1);//first variable
						break;
					}
					
				}else if(acummulativeColumns[i-1]<col && col<=acummulativeColumns[i]){
					this.noisyVariable = variables.get(i+1);
					 columnGroup = (col-1) - acummulativeColumns[i-1];//offset within the noisy parameters array
					break;
					}
			}
			int offset = 0;
			//initialize priorList for noisy potential
			for (int i = 0; i<noisyVariable.getNumStates(); i++){//columngroup
				if (columnGroup == i){
					for (int j = 0 ;j<conditionedStates; j++){
						this.priorityList.add(j+offset);
					}
					break;
				}
				offset += conditionedStates;
			}
			
			this.lastNoisyParameters = iciPotential.getNoisyParameters(noisyVariable);
			// number of previous columns of the variable*number of conditioned states + number of rows -1 - row
			this.position = (columnGroup) * conditionedStates + (conditionedStates+1) - row;
			lastNoisyParameters[position] = newValue;
			this.newNoisyParameters = lastNoisyParameters;
			
		}
		
	}
	/**
	 * Initializes editableTableValues that represent the table of the editable
	 *  values of the canonical table without headers
	 */
	/*
	
	
	/**
	 * Retrieves probeNode ICIPotential
	 * @param listPotentials
	 * @return
	 */
	
	private Potential getThisICIPotential(ArrayList<Potential> listPotentials) {

		Potential aPotential = null;
		try {
			aPotential = ((ICIPotential) listPotentials.get( 0 ));
		} catch (Exception ex) {
			//ExceptionsHandler.handleException(
				//ex, "no Potential.get(0) !!!", false );
			logger.error("no Potential.get(0) !!!");
			
		}

		return aPotential;
	}
	
	/** Gets the new value 
	 * @return */
	public double getNewValue() {
		return newValue;
	}
	
	/** Gets the table-potential of the node 
	 * @return */
	public ICIPotential getPotential() {
		return iciPotential;
	}
	
	/** Gets the table-potential of the node 
	 * @return variable1 <code>Variable</code> */
	public Variable getNoisyVariable() {
		return noisyVariable;
	}
	
	/** Gets the position edited 
	 * @return position <code>Integer</code> */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Gets the row position associated to value edited if priorityList no exists
	 * @param position position of the value in the array of values
	 * @return the position in the table
	 */
	public int getRowPosition() {
		return  row;
	}
	
	public int getRowPosition(int position) {
		
		return toPositionOnJtable(position, columnGroup, probNode.getVariable().
				getNumStates());
			
		
	}
	public static int toPositionOnJtable(int index, int columnGroup, int numOfStates){
		
		return (columnGroup*numOfStates) + numOfStates+1 - index;
		
	}
	/**
	 * Gets the column position associated to value edited if priorityList no exists
	 * @param position position of the value in the array of values
	 * @return the position in the table
	 */
	public int getColumnPosition() {
		return  col;
	}
	
	/**
	 * Gets the priority list
	 * @return the priority list
	 */
	public LinkedList<Integer> getPriorityList(){
		
		return priorityList;
		
	}
	
	public double[] getNewNoisyValues(){
		return newNoisyParameters;
	}
	
	/**
	 * First position is the noisy potential
	 * Second position is the index within it
	 * @param row
	 * @param column
	 * @return
	 */
	private double roundingDouble(double number) {

		double positions = Math.pow( 10, (double) decimalPositions );
		return Math.round( number * positions ) / positions;
	}
	
	@Override
	public void doEdit() throws DoEditException {
		// TODO Auto-generated method stub

	
		Iterator<Integer> listIterator = priorityList.listIterator();
		
		Double sum = 0.0;
		Double rest = 0.0;
		int priorityListPosition=0;
		
		if (!leakyFlag){
			while (listIterator.hasNext()== true){
				priorityListPosition = (Integer) listIterator.next();
				sum = roundingDouble(sum + newNoisyParameters[priorityListPosition]);
				//sum += newTable[pos];
			}
			rest = Math.abs(roundingDouble(1-sum));
			//rest = Math.abs( 1 - sum );
		
			if (sum > 1.0){
				listIterator = priorityList.listIterator();
				while (listIterator.hasNext()== true && rest != 0){
					priorityListPosition = (Integer) listIterator.next();
					rest = roundingDouble(rest - newNoisyParameters[priorityListPosition]);
					//rest = rest - newTable[pos];
					if (rest < 0){
						newNoisyParameters[priorityListPosition] = Math.abs(rest);
						break;
					}else
						newNoisyParameters[priorityListPosition] = 0;
				
					}
			}else{
				priorityListPosition = (Integer) priorityList.getFirst();
				newNoisyParameters[priorityListPosition] = roundingDouble(newNoisyParameters[priorityListPosition] + rest);
				//newTable[pos] = newTable[pos] + rest;
			}
			iciPotential.setNoisyParameters(noisyVariable,newNoisyParameters);
		}else if (leakyFlag) {
			
			while (listIterator.hasNext()== true){
				priorityListPosition = (Integer) listIterator.next();
				sum = roundingDouble(sum + newLeakyParameters[priorityListPosition]);
				//sum += newTable[pos];
			}
			rest = Math.abs(roundingDouble(1-sum));
			//rest = Math.abs( 1 - sum );
		
			if (sum > 1.0){
				listIterator = priorityList.listIterator();
				while (listIterator.hasNext()== true && rest != 0){
					priorityListPosition = (Integer) listIterator.next();
					rest = roundingDouble(rest - newLeakyParameters[priorityListPosition]);
					//rest = rest - newTable[pos];
					if (rest < 0){
						newLeakyParameters[priorityListPosition] = Math.abs(rest);
						break;
					}else
						newLeakyParameters[priorityListPosition] = 0;
				
					}
			}else{
				priorityListPosition = (Integer) priorityList.getFirst();
				newLeakyParameters[priorityListPosition] = roundingDouble(newLeakyParameters[priorityListPosition] + rest);
				//newTable[pos] = newTable[pos] + rest;
			}
			iciPotential.setLeakyParameters(newLeakyParameters);
		}                                         
		
		
		ArrayList <Potential> potentials = new ArrayList<Potential>();
		potentials.add(iciPotential);
		probNode.setPotentials(potentials);
	}
	
	public void undo() {
		super.undo();
		if (!leakyFlag){	
			iciPotential.setNoisyParameters(noisyVariable,newNoisyParameters);
		}else if (leakyFlag) {
			iciPotential.setLeakyParameters(newLeakyParameters);
		}                                         
		
		
	}
	


}
