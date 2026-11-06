package com.artificial.smart_loan_risk_assessment_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {

    private String customerName;

    private BigDecimal requestedLoanAmount;

    private BigDecimal estimatedMonthlyEmi;

    private BigDecimal debtToIncomeRatio;

    private int creditScore;

    private int riskScore;

    private String riskLevel;

    private String decision;

    private String assessmentReason;
}