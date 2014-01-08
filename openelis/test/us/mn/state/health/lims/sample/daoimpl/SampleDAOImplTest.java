/*
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
*
* The Original Code is OpenELIS code.
*
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*
* Contributor(s): CIRG, University of Washington, Seattle WA.
*/

package us.mn.state.health.lims.sample.daoimpl;

import org.bahmni.feed.openelis.IT;
import org.junit.Assert;
import org.junit.Test;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.dbhelper.DBHelper;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.statusofsample.util.StatusOfSampleUtil;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

public class SampleDAOImplTest extends IT{

    @Test
    public void shouldBeAbleToRetrieveCompleteSampleTreeByUuid() {
        String accessionNumber = "12345";

        Sample sample = DBHelper.createAndSaveSample(accessionNumber);
        SampleItem sampleItem = DBHelper.createAndSaveSampleItem(sample);
        Panel panel = DBHelper.createAndSavePanel();
        us.mn.state.health.lims.test.valueholder.Test test = DBHelper.createAndSaveTest();
        Analysis analysis = DBHelper.createAndSaveAnalysis(sampleItem, StatusOfSampleUtil.AnalysisStatus.NotTested, "Hematology", panel, test);
        TestResult testResult = DBHelper.createAndSaveTestResult(test);
        Result result = DBHelper.createAndSaveResult(analysis, testResult);


        Sample retrievedSample = new SampleDAOImpl().getSampleByUUID(sample.getUUID());

        Assert.assertNotNull(retrievedSample);
        SampleItem retrievedSampleItem = (SampleItem) retrievedSample.getSampleItems().toArray()[0];
        Assert.assertNotNull(retrievedSampleItem);
        Analysis retrievedAnalysis = (Analysis) retrievedSampleItem.getAnalyses().toArray()[0];
        Assert.assertNotNull(retrievedAnalysis);
        Assert.assertNotNull(retrievedAnalysis.getPanel());
        Result retrievedResults = (Result) retrievedAnalysis.getResults().toArray()[0];
        Assert.assertNotNull(retrievedResults);
        Assert.assertNotNull(retrievedResults.getTestResult());
        Assert.assertNotNull(retrievedResults.getTestResult().getTest());
    }

}
