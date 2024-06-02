package com.twd.SpringSecurityJWT.entity;

import java.util.Date;

public class FeedbackSearchCriteria {
    private Users createdBy;
    private String contenu;
    private Date submissionDate;

    // Getters and setters for each criteria field
    public Users getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
}
