package gov.cdc.epiinfo_ento.interpreter.functions;

import gov.cdc.epiinfo_ento.interpreter.EnterRule;
import gov.cdc.epiinfo_ento.interpreter.Rule_Context;

import com.creativewidgetworks.goldparser.engine.Reduction;

public class Rule_CurrentUser extends EnterRule
{
    public Rule_CurrentUser(Rule_Context pContext, Reduction pToken)
	{
    	super(pContext);
	    // UserId
	}
	
	/// <summary>
	/// Executes the reduction.
	/// </summary>
	/// <returns>Returns the current system date.</returns>
    @Override
	public Object Execute()
	{
	    return "NOUSERNAME";
	}

}
