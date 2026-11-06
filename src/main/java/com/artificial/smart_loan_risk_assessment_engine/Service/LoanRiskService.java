package com.artificial.smart_loan_risk_assessment_engine.Service;

import com.artificial.smart_loan_risk_assessment_engine.Model.LoanApplication;
import com.artificial.smart_loan_risk_assessment_engine.Repository.LoanApplicationRepository;
import com.artificial.smart_loan_risk_assessment_engine.config.RiskConfig;
import com.artificial.smart_loan_risk_assessment_engine.dto.LoanRequestDto;
import com.artificial.smart_loan_risk_assessment_engine.dto.LoanResponseDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanRiskService {

    private final LoanApplicationRepository loanRepository;

    public LoanRiskService(LoanApplicationRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Transactional
    public LoanResponseDto assessLoan(LoanRequestDto dto) {

        validateRequest(dto);

        // Calculate estimated monthly EMI

        BigDecimal monthlyEmi = calculateEmi(
                dto.getRequestedLoanAmount(),
                dto.getLoanTenureMonths()
        );

        // Calculate DTI ratio

        BigDecimal totalMonthlyEmi =
                dto.getExistingMonthlyEmi().add(monthlyEmi);

        BigDecimal dtiRatio = totalMonthlyEmi
                .multiply(new BigDecimal("100"))
                .divide(dto.getMonthlyIncome(), 2, RoundingMode.HALF_UP);

        // Calculate risk score

        int riskScore = 0;

        List<String> reasons = new ArrayList<>();

        // Rule 1: Credit score

        if (dto.getCreditScore() >= 750) {

            riskScore += 30;
            reasons.add("Excellent credit score: +30");

        } else if (dto.getCreditScore() >= 700) {

            riskScore += 20;
            reasons.add("Good credit score: +20");

        } else if (dto.getCreditScore() >= 650) {

            riskScore += 10;
            reasons.add("Average credit score: +10");

        } else {

            riskScore -= 20;
            reasons.add("Low credit score: -20");
        }

        // Rule 2: Debt-to-income ratio

        if (dtiRatio.compareTo(RiskConfig.LOW_DTI) <= 0) {

            riskScore += 30;
            reasons.add("Low DTI ratio: +30");

        } else if (dtiRatio.compareTo(RiskConfig.MEDIUM_DTI) <= 0) {

            riskScore += 20;
            reasons.add("Moderate DTI ratio: +20");

        } else if (dtiRatio.compareTo(RiskConfig.HIGH_DTI) <= 0) {

            riskScore += 10;
            reasons.add("High DTI ratio: +10");

        } else {

            riskScore -= 20;
            reasons.add("Very high DTI ratio: -20");
        }

        // Rule 3: Monthly income

        if (dto.getMonthlyIncome().compareTo(
                new BigDecimal("50000")) >= 0) {

            riskScore += 15;
            reasons.add("Strong monthly income: +15");

        } else if (dto.getMonthlyIncome().compareTo(
                new BigDecimal("30000")) >= 0) {

            riskScore += 10;
            reasons.add("Moderate monthly income: +10");

        } else {

            reasons.add("Low monthly income: +0");
        }

        // Rule 4: Loan amount compared to income

        BigDecimal incomeMultiple = dto.getMonthlyIncome()
                .multiply(new BigDecimal("12"));

        if (dto.getRequestedLoanAmount().compareTo(incomeMultiple) <= 0) {

            riskScore += 15;
            reasons.add("Loan amount is within annual income: +15");

        } else if (dto.getRequestedLoanAmount().compareTo(
                incomeMultiple.multiply(new BigDecimal("2"))) <= 0) {

            riskScore += 5;
            reasons.add("Loan amount is within two times annual income: +5");

        } else {

            riskScore -= 15;
            reasons.add("Loan amount is too high: -15");
        }

        // Determine decision

        String decision;
        String riskLevel;

        if (dto.getCreditScore() < RiskConfig.MIN_CREDIT_SCORE ||
                dtiRatio.compareTo(RiskConfig.MAX_DTI) > 0) {

            decision = "REJECTED";
            riskLevel = "HIGH";

            reasons.add("Automatic rejection rule applied");

        } else if (riskScore >= RiskConfig.APPROVAL_SCORE) {

            decision = "APPROVED";
            riskLevel = "LOW";

        } else if (riskScore >= RiskConfig.REVIEW_SCORE) {

            decision = "REVIEW";
            riskLevel = "MEDIUM";

        } else {

            decision = "REJECTED";
            riskLevel = "HIGH";
        }

        String assessmentReason = String.join(", ", reasons);

        // Save application in database

        LoanApplication application = new LoanApplication();

        application.setCustomerName(dto.getCustomerName());
        application.setMonthlyIncome(dto.getMonthlyIncome());
        application.setExistingMonthlyEmi(dto.getExistingMonthlyEmi());
        application.setCreditScore(dto.getCreditScore());
        application.setRequestedLoanAmount(dto.getRequestedLoanAmount());
        application.setLoanTenureMonths(dto.getLoanTenureMonths());
        application.setEstimatedMonthlyEmi(monthlyEmi);
        application.setDebtToIncomeRatio(dtiRatio);
        application.setRiskScore(riskScore);
        application.setRiskLevel(riskLevel);
        application.setDecision(decision);
        application.setAssessmentReason(assessmentReason);
        application.setAssessedAt(LocalDateTime.now());

        loanRepository.save(application);

        // Return assessment response

        return new LoanResponseDto(
                application.getCustomerName(),
                application.getRequestedLoanAmount(),
                application.getEstimatedMonthlyEmi(),
                application.getDebtToIncomeRatio(),
                application.getCreditScore(),
                application.getRiskScore(),
                application.getRiskLevel(),
                application.getDecision(),
                application.getAssessmentReason()
        );
    }

    // EMI calculation using reducing balance formula

    private BigDecimal calculateEmi(
            BigDecimal principal,
            int tenureMonths) {

        double monthlyRate =
                RiskConfig.ANNUAL_INTEREST_RATE.doubleValue()
                        / 12 / 100;

        double amount = principal.doubleValue();

        double factor = Math.pow(
                1 + monthlyRate,
                tenureMonths
        );

        double emi = amount * monthlyRate * factor / (factor - 1);

        return BigDecimal.valueOf(emi)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Input validation

    private void validateRequest(LoanRequestDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        if (dto.getCustomerName() == null ||
                dto.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }

        if (dto.getMonthlyIncome() == null ||
                dto.getMonthlyIncome().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monthly income must be greater than zero");
        }

        if (dto.getExistingMonthlyEmi() == null ||
                dto.getExistingMonthlyEmi().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Existing EMI cannot be negative");
        }

        if (dto.getRequestedLoanAmount() == null ||
                dto.getRequestedLoanAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Loan amount must be greater than zero");
        }

        if (dto.getCreditScore() < 300 ||
                dto.getCreditScore() > 900) {
            throw new IllegalArgumentException("Credit score must be between 300 and 900");
        }

        if (dto.getLoanTenureMonths() <= 0 ||
                dto.getLoanTenureMonths() > 360) {
            throw new IllegalArgumentException("Loan tenure must be between 1 and 360 months");
        }
    }
}