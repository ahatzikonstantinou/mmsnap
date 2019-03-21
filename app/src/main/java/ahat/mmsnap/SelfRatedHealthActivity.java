package ahat.mmsnap;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class SelfRatedHealthActivity extends MassDisableActivity // AppCompatActivity
{

    protected int getActivityResLayout(){ return R.layout.activity_self_rated_health; }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_self_rated_health );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        getSupportActionBar().setIcon( getResources().getDrawable( R.drawable.assessments_section_logo) );
        getSupportActionBar().setTitle( R.string.title_activity_assessments );
        getSupportActionBar().setSubtitle( R.string.title_activity_self_rated_health );

        final int[] radiogroupResIds = {
            R.id.one_condition_more_serious_rdgrp,
            R.id.time_spent_managing_rdgrp,
            R.id.feel_overwhelmed_rdgrp,
            R.id.causes_are_linked_rdgrp,
            R.id.difficult_all_medications_rdgrp,
            R.id.limited_activities_rdgrp,
            R.id.different_medications_problems_rdgrp,
            R.id.mixing_medications_rdgrp,
            R.id.less_effective_treatments_rdgrp,
            R.id.one_cause_another_rdgrp,
            R.id.one_dominates_rdgrp,
            R.id.conditions_interact_rdgrp,
            R.id.difficult_best_treatment_rdgrp,
            R.id.reduced_social_life_rdgrp,
            R.id.unhappy_rdgrp,
            R.id.anxious_rdgrp,
            R.id.angry_rdgrp,
            R.id.sad_rdgrp,
            R.id.irritable_rdgrp,
            R.id.sad_struggle_rdgrp
        };

        findViewById( R.id.self_rated_health_submit_btn ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view )
            {
                if( !checkAllQuestionsAnswered( radiogroupResIds ) )
                {
                    AlertDialog alertDialog = new AlertDialog.Builder( SelfRatedHealthActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Please answer all questions." );
                    alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK",
                                           new DialogInterface.OnClickListener() {
                                               public void onClick( DialogInterface dialog, int which) {
                                                   dialog.dismiss();
                                               }
                                           });
                    alertDialog.show();
                    return;
                }

                try
                {
                    ApplicationStatus as = ApplicationStatus.getInstance( view.getContext() );

                    as.selfRatedHealth.one_condition_more_serious = getValueFrom( R.id.one_condition_more_serious_rdgrp );
                    as.selfRatedHealth.time_spent_managing = getValueFrom( R.id.time_spent_managing_rdgrp );
                    as.selfRatedHealth.feel_overwhelmed = getValueFrom( R.id.feel_overwhelmed_rdgrp );
                    as.selfRatedHealth.causes_are_linked = getValueFrom( R.id.causes_are_linked_rdgrp );
                    as.selfRatedHealth.difficult_all_medications = getValueFrom( R.id.difficult_all_medications_rdgrp );
                    as.selfRatedHealth.limited_activities = getValueFrom( R.id.limited_activities_rdgrp );
                    as.selfRatedHealth.different_medications_problems = getValueFrom( R.id.different_medications_problems_rdgrp );
                    as.selfRatedHealth.mixing_medications = getValueFrom( R.id.mixing_medications_rdgrp );
                    as.selfRatedHealth.less_effective_treatments = getValueFrom( R.id.less_effective_treatments_rdgrp );
                    as.selfRatedHealth.one_cause_another = getValueFrom( R.id.one_cause_another_rdgrp );
                    as.selfRatedHealth.one_dominates = getValueFrom( R.id.one_dominates_rdgrp );
                    as.selfRatedHealth.conditions_interact = getValueFrom( R.id.conditions_interact_rdgrp );
                    as.selfRatedHealth.difficult_best_treatment = getValueFrom( R.id.difficult_best_treatment_rdgrp );
                    as.selfRatedHealth.reduced_social_life = getValueFrom( R.id.reduced_social_life_rdgrp );
                    as.selfRatedHealth.unhappy = getValueFrom( R.id.unhappy_rdgrp );
                    as.selfRatedHealth.anxious = getValueFrom( R.id.anxious_rdgrp );
                    as.selfRatedHealth.angry = getValueFrom( R.id.angry_rdgrp );
                    as.selfRatedHealth.sad = getValueFrom( R.id.sad_rdgrp );
                    as.selfRatedHealth.irritable = getValueFrom( R.id.irritable_rdgrp );
                    as.selfRatedHealth.sad_struggle = getValueFrom( R.id.sad_struggle_rdgrp );

                    as.addAssessment( ApplicationStatus.Assessment.SELF_RATED_HEALTH );

                    //TODO SEND_TO_SERVER

                    startActivity( getParentActivityIntent() );
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                    Toast.makeText( SelfRatedHealthActivity.this, "An error occurred while saving your answers. Please try again.", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

        try
        {
            ApplicationStatus as = ApplicationStatus.getInstance( this );
            if( ApplicationStatus.NoInitialAssessments.NAME != as.getState().name() &&
                ApplicationStatus.NoFinalAssessments.NAME != as.getState().name()
            )
            {
                findViewById( R.id.self_rated_health_submit_btn ).setVisibility( View.GONE );
                disableAllControls();
            }

            setValue( R.id.one_condition_more_serious_rdgrp, as.selfRatedHealth.one_condition_more_serious );
            setValue( R.id.time_spent_managing_rdgrp, as.selfRatedHealth.time_spent_managing );
            setValue( R.id.feel_overwhelmed_rdgrp, as.selfRatedHealth.feel_overwhelmed );
            setValue( R.id.causes_are_linked_rdgrp, as.selfRatedHealth.causes_are_linked );
            setValue( R.id.difficult_all_medications_rdgrp, as.selfRatedHealth.difficult_all_medications );
            setValue( R.id.limited_activities_rdgrp, as.selfRatedHealth.limited_activities );
            setValue( R.id.different_medications_problems_rdgrp, as.selfRatedHealth.different_medications_problems );
            setValue( R.id.mixing_medications_rdgrp, as.selfRatedHealth.mixing_medications );
            setValue( R.id.less_effective_treatments_rdgrp, as.selfRatedHealth.less_effective_treatments );
            setValue( R.id.one_cause_another_rdgrp, as.selfRatedHealth.one_cause_another );
            setValue( R.id.one_dominates_rdgrp, as.selfRatedHealth.one_dominates );
            setValue( R.id.conditions_interact_rdgrp, as.selfRatedHealth.conditions_interact );
            setValue( R.id.difficult_best_treatment_rdgrp, as.selfRatedHealth.difficult_best_treatment );
            setValue( R.id.reduced_social_life_rdgrp, as.selfRatedHealth.reduced_social_life );
            setValue( R.id.unhappy_rdgrp, as.selfRatedHealth.unhappy );
            setValue( R.id.anxious_rdgrp, as.selfRatedHealth.anxious );
            setValue( R.id.angry_rdgrp, as.selfRatedHealth.angry );
            setValue( R.id.sad_rdgrp, as.selfRatedHealth.sad );
            setValue( R.id.irritable_rdgrp, as.selfRatedHealth.irritable );
            setValue( R.id.sad_struggle_rdgrp, as.selfRatedHealth.sad_struggle );
        }
        catch( Exception e )
        {
            e.printStackTrace();
            Toast.makeText( this, "An error occurred retrieving the application status", Toast.LENGTH_SHORT ).show();
        }
    }

    private void setValue( int radiogroupResId, int value )
    {
        if( value < 0 || value > 3 )
        {
            return;
        }

        RadioGroup radioGroup = findViewById( radiogroupResId );
        View o = radioGroup.getChildAt( value );
        if( o instanceof RadioButton )
        {
            ( (RadioButton) o ).setChecked( true );
        }
    }

    private int getValueFrom( int radiogroupResId ) throws Exception
    {
        RadioGroup radioGroup = findViewById( radiogroupResId );
        int count = radioGroup.getChildCount();
        for( int i = 0 ; i < count ; i++ )
        {
            View o = radioGroup.getChildAt( i );
            if( o instanceof RadioButton )
            {
                if( ( (RadioButton) o ).isChecked() )
                {
                    return i;
                }
            }
        }

        throw new Exception( "No radio button is checked" );
    }

    @Override
    public void onBackPressed()
    {
        startActivity( new Intent( this, AssessmentsActivity.class ) );
    }

    private boolean checkAllQuestionsAnswered( int[] radiogroupResIds )
    {
        boolean allAnswered = true;
        for( int i = 0 ; i < radiogroupResIds.length ; i++ )
        {
            RadioGroup radioGroup = findViewById( radiogroupResIds[i] );
            int count = radioGroup.getChildCount();
            boolean answered = false;
            for( int c = 0 ; c < count ; c++ )
            {
                View o = radioGroup.getChildAt( c );
                if( o instanceof RadioButton && ( (RadioButton) o ).isChecked() )
                {
                    answered = true;
                    break;
                }
            }

            if( !answered )
            {
                Drawable highlightBkg = getResources().getDrawable( R.drawable.custom_radio_highglight, null );
                radioGroup.setBackground( highlightBkg  );
                allAnswered = false;
            }
        }

        return allAnswered;
    }

}
