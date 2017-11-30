package gov.cdc.epiinfo_ento.interpreter;

import com.creativewidgetworks.goldparser.engine.Reduction;

public class Rule_AutoSearch extends EnterRule
{
    boolean IsExceptList = false;
    String field = null;
    String TitleText = null;

    public Rule_AutoSearch(Rule_Context pContext, Reduction pToken)
    {
    	super(pContext);

    	this.field = this.ExtractIdentifier(pToken.get(1)).replace("\"", "").toString();
    }


    /// <summary>
    /// performs execution of the HIDE command via the EnterCheckCodeInterface.Hide method
    /// </summary>
    /// <returns>object</returns>
    @Override
    public Object Execute()
    {
        this.Context.CheckCodeInterface.AutoSearch(new String[]{this.field},null,false);
        return null;
    }
}
