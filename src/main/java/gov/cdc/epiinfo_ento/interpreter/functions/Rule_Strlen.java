package gov.cdc.epiinfo_ento.interpreter.functions;

import gov.cdc.epiinfo_ento.interpreter.EnterRule;
import gov.cdc.epiinfo_ento.interpreter.Rule_Context;

import java.util.ArrayList;
import java.util.List;

import com.creativewidgetworks.goldparser.engine.Reduction;

public class Rule_Strlen extends EnterRule 
{
    private List<EnterRule> ParameterList = new ArrayList<EnterRule>();

    public Rule_Strlen(Rule_Context pContext, Reduction pToken)
        
    {
    	super(pContext);
        this.ParameterList = EnterRule.GetFunctionParameters(pContext, pToken);
    }

    /// <summary>
    /// Executes the reduction.
    /// </summary>
    /// <returns>Returns the absolute value of two numbers.</returns>
    @Override
    public Object Execute()
    {
        int result = 0;
        try
        {

        	result = this.ParameterList.get(0).Execute().toString().length();
            return result;
        }
        catch(Exception ex)
        {
            return null;
        }
    }
}
