package ahat.mmsnap;

import android.os.Bundle;

public class ActionPlansActivity extends IfThenListActivity
{

    public final String FILENAME = "action_plans.json";

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
    }

    protected IfThenListAdapter createListAdapter()
    {
        return new CounterfactualListAdapter( this, items, delete );
    }

    protected String getFilename()
    {
        return FILENAME;
    }

    protected String getLoadItemsErrorMessage()
    {
        return "Action plans could not be loaded.";
    }

    protected String getDeleteItemsErrorMessage()
    {
        return "Could not delete selected action plans";
    }

    protected int getSubtitleStringResId()
    {
        return R.string.title_activity_action_plans;
    }

    protected int getLogoDrawableResId()
    {
        return R.drawable.if_then_section_logo;
    }

    protected Class<?> getDetailActivityClass()
    {
        return ActionPlansDetailActivity.class;
    }

}
