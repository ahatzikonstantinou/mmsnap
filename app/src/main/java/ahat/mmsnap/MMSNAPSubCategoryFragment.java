package ahat.mmsnap;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class MMSNAPSubCategoryFragment extends Fragment
{

    private int page;
    private String title;
    private String text;
    private int color;

    private MMSNAPSubCategoryViewModel mViewModel;

    public static MMSNAPSubCategoryFragment newInstance( int page, int titleResId, int  textResId, int colorResId )
    {
        MMSNAPSubCategoryFragment f = new MMSNAPSubCategoryFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putInt("titleResId", titleResId);
        args.putInt("textResId", textResId);
        args.putInt("colorResId", colorResId);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState )
    {
        mViewModel = ViewModelProviders.of(getActivity()).get(MMSNAPSubCategoryViewModel.class);

        View view = inflater.inflate( R.layout.mmsnapsub_category_fragment, container, false );

//        LinearLayout ll = ( LinearLayout ) view.findViewById( R.id.mmsnap_subcategory_linearlayout );
//        ll.getBackground().setColorFilter( getResources().getColor( getArguments().getInt( "colorResId" ) ), PorterDuff.Mode.SRC_ATOP );

        TextView title = ( TextView ) view.findViewById( R.id.mmsnap_subcategory_title_textview );
        title.setText( getResources().getString( getArguments().getInt( "titleResId" ) ) );
        title.setBackgroundColor( getResources().getColor( getArguments().getInt( "colorResId" ) ) );

        TextView text = ( TextView ) view.findViewById( R.id.mmsnap_subcategory_textview );
//        text.setText( getResources().getString( getArguments().getInt( "textResId" ) ) );
        text.setText( Html.fromHtml( getResources().getString( getArguments().getInt( "textResId" ) ), FROM_HTML_MODE_LEGACY ) );

        return view;
    }

    @Override
    public void onActivityCreated( @Nullable Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );

        // ahat: it seems onActivityCreated is called after onCreateView and therefore mViewModel is empty when updating the UI
//        mViewModel = ViewModelProviders.of( getActivity() ).get( MMSNAPSubCategoryViewModel.class );
//
//        mViewModel.page = getArguments().getInt( "page" );
//        mViewModel.title = getResources().getString( getArguments().getInt( "titleResId" ) );
//        mViewModel.text = getResources().getString( getArguments().getInt( "textResId" ) );
//        mViewModel.color = getResources().getColor( getArguments().getInt( "colorResId" ) );

    }

}
