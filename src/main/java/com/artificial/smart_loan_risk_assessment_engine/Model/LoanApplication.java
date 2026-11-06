package com.artificial.smart_loan_risk_assessment_engine.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    private BigDecimal monthlyIncome;

    private BigDecimal existingMonthlyEmi;

    private int creditScore;

    private BigDecimal requestedLoanAmount;

    private int loanTenureMonths;

    private BigDecimal estimatedMonthlyEmi;

    private BigDecimal debtToIncomeRatio;

    private int riskScore;

    private String riskLevel;

    private String decision;

    @Column(length = 1000)
    private String assessmentReason;

    private LocalDateTime assessedAt;
}