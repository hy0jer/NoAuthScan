package example.scanchecks;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.scanner.AuditResult;
import burp.api.montoya.scanner.ConsolidationAction;
import burp.api.montoya.scanner.audit.insertionpoint.AuditInsertionPoint;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.scanner.audit.issues.AuditIssueConfidence;
import burp.api.montoya.scanner.audit.issues.AuditIssueSeverity;
import example.scanchecks.ui.ConfigUi;
import example.scanchecks.model.View_model;

import java.util.ArrayList;
import java.util.List;

import static burp.api.montoya.scanner.AuditResult.auditResult;
import static burp.api.montoya.scanner.ConsolidationAction.KEEP_BOTH;
import static burp.api.montoya.scanner.ConsolidationAction.KEEP_EXISTING;
import static burp.api.montoya.scanner.audit.issues.AuditIssue.auditIssue;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;


class ScanCheck implements burp.api.montoya.scanner.ScanCheck {
    private final View_model tableModel;
    private final MontoyaApi api;
    public ConfigUi configUi;

    ScanCheck(MontoyaApi api, View_model tableModel, ConfigUi configUi) {
        this.tableModel = tableModel;
        this.api = api;
        this.configUi = configUi;

    }

    @Override
    public AuditResult activeAudit(HttpRequestResponse baseRequestResponse, AuditInsertionPoint auditInsertionPoint) {
        return null;
    }

    @Override
    public AuditResult passiveAudit(HttpRequestResponse baseRequestResponse) {
        List<AuditIssue> auditIssueList = new ArrayList<>();
        if (!this.configUi.getAutoSendRequest()) {
            return auditResult(auditIssueList);
        }
        Boolean flag = true;
        if (!this.configUi.getConfigStatus()) {
            flag = false;
        }
        TestModel model = new TestModel(this.api, baseRequestResponse);
        HttpRequestResponse result_package = model.test_engine(this.api, baseRequestResponse, this.tableModel,flag);
        if (result_package != null) {
            auditIssueList = singletonList(
                    auditIssue(
                            "No Auth",
                            "There is a risk of unauthorized access to this interface " ,
                            "It is recommended to add permission verification",
                            result_package.request().url(),
                            AuditIssueSeverity.HIGH,
                            AuditIssueConfidence.CERTAIN,
                            null,
                            null,
                            AuditIssueSeverity.HIGH,
                            result_package));
        } else {
            auditIssueList = emptyList();
        }
        return auditResult(auditIssueList);
    }

    @Override
    public ConsolidationAction consolidateIssues(AuditIssue newIssue, AuditIssue existingIssue) {
        return existingIssue.name().equals(newIssue.name()) ? KEEP_EXISTING : KEEP_BOTH;
    }
}