package com.fieldbook.tracker.brapi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;

import com.fieldbook.tracker.R;
import com.fieldbook.tracker.preferences.PreferencesActivity;
import com.fieldbook.tracker.utilities.Constants;

public class BrapiLoadDialog extends Dialog implements android.view.View.OnClickListener{

    private Button saveBtn, cancelBtn;
    private BrapiStudySummary study;
    private BrapiStudyDetails studyDetails;
    private BrAPIService brAPIService;
    private Context context;

    public BrapiLoadDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public void setSelectedStudy(BrapiStudySummary selectedStudy){
        this.study = selectedStudy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_brapi_import);

        ;
        String brapiBaseURL = this.context.getSharedPreferences("Settings", 0)
                .getString(PreferencesActivity.BRAPI_BASE_URL, "") + Constants.BRAPI_PATH;
        brAPIService = new BrAPIService(this.context, brapiBaseURL);
        saveBtn = findViewById(R.id.brapi_save_btn);
        saveBtn.setOnClickListener(this);
        cancelBtn = findViewById(R.id.brapi_cancel_btn);
        cancelBtn.setOnClickListener(this);
        studyDetails = new BrapiStudyDetails();

        buildStudyDetails();
        loadStudy();
    }

    private void buildStudyDetails() {
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        brAPIService.getStudyDetails(study.getStudyDbId(), new Function<BrapiStudyDetails, Void>() {
            @Override
            public Void apply(final BrapiStudyDetails study) {
                BrapiStudyDetails.merge(studyDetails, study);
                loadStudy();
                return null;
            }
        });
        brAPIService.getPlotDetails(study.getStudyDbId(), new Function<BrapiStudyDetails, Void>() {
            @Override
            public Void apply(final BrapiStudyDetails study) {
                BrapiStudyDetails.merge(studyDetails, study);
                loadStudy();
                return null;
            }
        });
        brAPIService.getTraits(study.getStudyDbId(), new Function<BrapiStudyDetails, Void>() {
            @Override
            public Void apply(final BrapiStudyDetails study) {
                BrapiStudyDetails.merge(studyDetails, study);
                loadStudy();
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                return null;
            }
        });
    }

    private void loadStudy() {
        if(this.studyDetails.getStudyName() != null)
            ((TextView) findViewById(R.id.studyNameValue)).setText(this.studyDetails.getStudyName());
        if(this.studyDetails.getStudyDescription() != null)
            ((TextView) findViewById(R.id.studyDescriptionValue)).setText(this.studyDetails.getStudyDescription());
        if(this.studyDetails.getStudyLocation() != null)
            ((TextView) findViewById(R.id.studyLocationValue)).setText(this.studyDetails.getStudyLocation());
        if(this.studyDetails.getNumberOfPlots() != null)
            ((TextView) findViewById(R.id.studyNumPlotsValue)).setText(this.studyDetails.getNumberOfPlots().toString());
        if(this.studyDetails.getTraits() != null)
            ((TextView) findViewById(R.id.studyNumTraitsValue)).setText(String.valueOf(this.studyDetails.getTraits().size()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.brapi_save_btn:
                saveStudy();
                ((Activity) this.context).finish();
                break;
            case R.id.brapi_cancel_btn:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void saveStudy() {
        brAPIService.saveStudyDetails(studyDetails);
    }

}