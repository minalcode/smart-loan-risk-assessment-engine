package com.artificial.smart_loan_risk_assessment_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDto {

    private String customerName;

    private BigDecimal monthlyIncome;

    private BigDecimal existingMonthlyEmi;

    private int creditScore;

    private BigDecimal requestedLoanAmount;

    private int loanTenureMonths;
}