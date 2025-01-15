package com.example.symptom_checker;

public class DecisionTreeNode {

    private String question;
    private String diagnosis;
    private DecisionTreeNode yesNode;
    private DecisionTreeNode noNode;

    public boolean isLeaf() {
        return diagnosis != null;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public DecisionTreeNode getYesNode() {
        return yesNode;
    }

    public void setYesNode(DecisionTreeNode yesNode) {
        this.yesNode = yesNode;
    }

    public DecisionTreeNode getNoNode() {
        return noNode;
    }

    public void setNoNode(DecisionTreeNode noNode) {
        this.noNode = noNode;
    }
}