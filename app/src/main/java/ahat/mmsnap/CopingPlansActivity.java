package ahat.mmsnap;

import android.os.Bundle;

public class CopingPlansActivity extends IfThenListActivity
{

    public final String FILENAME = "coping_plans.json";

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
        return "Coping plans could not be loaded.";
    }

    protected String getDeleteItemsErrorMessage()
    {
        return "Could not delete selected coping plans";
    }

    protected int getSubtitleStringResId()
    {
        return R.string.title_activity_coping_plans;
    }

    protected int getLogoDrawableResId()
    {
        return R.drawable.if_then_section_logo;
    }

    protected Class<?> getDetailActivityClass()
    {
        return CounterfactualDetailActivity.class;
    }

}
