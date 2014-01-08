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
*/

package org.bahmni.feed.openelis.feed.service.impl;

import junit.framework.Assert;
import org.bahmni.feed.openelis.externalreference.dao.ExternalReferenceDao;
import org.bahmni.feed.openelis.externalreference.valueholder.ExternalReference;
import org.bahmni.openelis.domain.AccessionDetails;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.dbhelper.DBHelper;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.valueholder.Note;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.result.dao.ResultSignatureDAO;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.result.valueholder.ResultSignature;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AccessionServiceTest {
    @Mock
    private SampleDAO sampleDao;
    @Mock
    private SampleHumanDAO sampleHumanDAO;
    @Mock
    private ExternalReferenceDao externalReferenceDao;
    @Mock
    private NoteDAO noteDao;
    @Mock
    private SystemUserDAO systemUserDao;
    @Mock
    private DictionaryDAO dictionaryDao;
    @Mock
    private ResultSignatureDAO resultSignatureDao;

    private Sample sample;
    private Patient patient;

    @Before
    public void setUp() {
        initMocks(this);
        sample = DBHelper.createEntireSampleTreeWithResults();
        patient = DBHelper.createPatient();
    }

    @Test
    public void shouldReturnAccessionDetails() {
        AccessionService accessionService = new TestableAccessionService(sampleDao, sampleHumanDAO, externalReferenceDao, noteDao, dictionaryDao, resultSignatureDao, systemUserDao);
        when(sampleDao.getSampleByUUID(sample.getUUID())).thenReturn(sample);
        when(sampleHumanDAO.getPatientForSample(sample)).thenReturn(patient);
        when(externalReferenceDao.getDataByItemId(anyString(), anyString())).thenReturn(new ExternalReference(456789, "Ex Id", "type"));
        List<ResultSignature> resultSignatures = new ArrayList<>();
        resultSignatures.add(new ResultSignature());
        when(resultSignatureDao.getResultSignaturesByResult((Result) anyObject())).thenReturn(resultSignatures);
        when(systemUserDao.getUserById(anyString())).thenReturn(new SystemUser());
        AccessionDetails accessionDetails = accessionService.getAccessionDetailsFor(sample.getUUID());
        assert accessionDetails != null;
    }

    @Test
    public void shouldGetAccessionDetailsForUuid() {
        AccessionService accessionService = new TestableAccessionService(sampleDao, sampleHumanDAO, externalReferenceDao, noteDao, dictionaryDao, resultSignatureDao, systemUserDao);
        ExternalReference externalReferences = new ExternalReference(98743123, "ExternalId", "Type");

        when(sampleDao.getSampleByUUID(sample.getUUID())).thenReturn(sample);
        when(sampleHumanDAO.getPatientForSample(sample)).thenReturn(patient);
        Analysis analysis = (Analysis) ((SampleItem) sample.getSampleItems().toArray()[0]).getAnalyses().toArray()[0];
        when(externalReferenceDao.getDataByItemId(analysis.getTest().getId(), "Test")).thenReturn(externalReferences);
        when(externalReferenceDao.getDataByItemId(analysis.getPanel().getId(), "Panel")).thenReturn(externalReferences);
        when(noteDao.getNoteByRefIAndRefTableAndSubject(anyString(), anyString(), anyString())).thenReturn(new ArrayList<Note>());
        List<ResultSignature> resultSignatures = new ArrayList<>();
        resultSignatures.add(new ResultSignature());
        when(resultSignatureDao.getResultSignaturesByResult((Result) anyObject())).thenReturn(resultSignatures);
        when(systemUserDao.getUserById(anyString())).thenReturn(new SystemUser());

        AccessionDetails accessionDetails = accessionService.getAccessionDetailsFor(sample.getUUID());

        Assert.assertEquals(accessionDetails.getAccessionUuid(), sample.getUUID());
        Assert.assertNotNull(accessionDetails.getTestResults());
        Assert.assertNotNull(accessionDetails.getPatientUuid());
        Assert.assertEquals(accessionDetails.getPatientUuid(), patient.getUuid());
    }

    private class TestableAccessionService extends AccessionService {

        public TestableAccessionService(SampleDAO sampleDao, SampleHumanDAO sampleHumanDAO, ExternalReferenceDao externalReferenceDao, NoteDAO noteDao, DictionaryDAO dictionaryDao, ResultSignatureDAO resultSignatureDao, SystemUserDAO systemUserDAO) {
            super(sampleDao, sampleHumanDAO, externalReferenceDao, noteDao, dictionaryDao, resultSignatureDao, systemUserDAO);
        }

        @Override
        protected String getResultReferenceTableId() {
            return "1";
        }
    }

}
