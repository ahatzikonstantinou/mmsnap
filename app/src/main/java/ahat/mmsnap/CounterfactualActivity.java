package ahat.mmsnap;

import android.content.Intent;
import android.os.Bundle;

public class CounterfactualActivity extends IfThenListActivity
{

    public final String FILENAME = "counterfactual.json";

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
        return "Counterfactual thoughts could not be loaded.";
    }

    protected String getDeleteItemsErrorMessage()
    {
        return "Could not delete selected counterfactual thoughts";
    }

    protected int getSubtitleStringResId()
    {
        return R.string.title_activity_counterfactual;
    }

    protected int getTitleStringResId()
    {
        return R.string.title_activity_if_then;
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
