package org.openmarkov.core.gui.treeadd;

import java.util.HashSet;

import org.openmarkov.core.model.graph.LabelledLink;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.treeadd.BranchData;
import org.openmarkov.core.model.network.potential.treeadd.BranchInterval;

/** This class formats the branch info that will be showed in the JTree component
 * @author Jorge
 *
 */
public class SummaryBox {
	/**
	 * 
	 */
	protected Node source;
	protected Node parent;
	protected int branchIndex; 
	
	/**
	 * @param source
	 */
	public SummaryBox (Node source, Node parent, int branchIndex) {
		this.source= source;
		this.parent= parent;
		this.branchIndex= branchIndex;
	}
	
	public Node getSource() {
		return source;
	}

	public Node getParent() {
		return parent;
	}
	
	/**
	 * @param precisionProxy
	 * @return
	 */
	public String getHTML(PrecisionProxy precisionProxy) {

		String txtIzq="<html><table border=1>";

		if( parent==null ) {
			throw new RuntimeException();
		}
		else {
			if (!(parent.getObject() instanceof Variable)) {
				throw new RuntimeException("Expected Variable class: found " + parent.getObject().getClass().getName());
			}
			
			Variable parentVar= (Variable) parent.getObject();
			String varName= parentVar.getName();
			
			Link link= source.getGraph().getLink(parent, source, true);
			
			if (link instanceof LabelledLink) {
				LabelledLink labelledLink= (LabelledLink) link;
								
				if (!(labelledLink.getLabel() instanceof BranchData)) {
					throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
				}
				
				// TODO: test cardinality of variable states and InnerNode
				BranchData branchData= (BranchData) labelledLink.getLabel();
				
				// TODO: Use branchData
				if (parentVar.getVariableType()==VariableType.NUMERIC) {
					HashSet<BranchInterval> branchIntervals= branchData.getBranchIntervals();

					String intervalString= "";					
					if (branchIntervals.size()>1) {
						intervalString += "{";
					}
					
					boolean bFirst= true;
					for (BranchInterval o : branchIntervals) {
						if (bFirst) {
							bFirst= false;
						}
						else {
							intervalString += ", ";
						}

						intervalString += o.isLeftClosed() ? "[" : "(";
						
						intervalString += "<span style='color: blue;'>";
						intervalString += precisionProxy.formatValue(this, o.getLeft());
						intervalString += "</span>";
						
						intervalString += ", ";
						intervalString += "<span style='color: blue;'>";
						intervalString += precisionProxy.formatValue(this, o.getRight());
 						intervalString += "</span>";
						
						intervalString += o.isRightClosed() ? "]" : ")";
					}
					
					if (branchIntervals.size()>1) {
						intervalString += "}";
					}
					
					txtIzq+= "<td align=center border=0>" + varName + "=" + intervalString +"</td>"; 										
				}
				else {
					HashSet<State> branchStates= branchData.getBranchStates();
					String varStateNames= "";
					
					if (branchStates.size()>1) {
						varStateNames += "{";
					}
					
					boolean bFirst= true;
					for (State o : branchData.getBranchVariable().getStates()) {
						if (!branchStates.contains(o)) {
							continue;
						}
						
						if (bFirst) {
							bFirst= false;
						}
						else {
							varStateNames += ", ";
						}
						
						varStateNames += o.getName();						
					}
					
					if (branchStates.size()>1) {
						varStateNames += "}";
					}
					
					txtIzq+= "<td align=center border=0>" + varName + "=" + varStateNames +"</td>"; 					
				}
			}
			else /* if (link==null)*/ {
				// TODO: test cardinality of variable states and InnerNode
				String varStateName= parentVar.getStateName(branchIndex);				
				txtIzq+= "<td align=center border=0>" + varName + "=" + varStateName +"</td>"; 
			}
			/*
			else {
				throw new RuntimeException("Unexpected Link class: found " + link.getClass().getName());
			}
			*/
		}
	
		txtIzq+="</table></html>";
			
		return txtIzq;
	}
}
